package com.news.demo.ui.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.news.demo.R;
import com.news.demo.data.Preferences.NewsPreferenceManager;
import com.news.demo.data.database.NewsDatabase;
import com.news.demo.data.database.entity.NewsArticleEntity;
import com.news.demo.data.network.NetworkUtility;
import com.news.demo.di.component.DaggerNewsComponents;
import com.news.demo.di.component.NewsComponents;
import com.news.demo.models.NewsArticle;
import com.news.demo.ui.adapter.NewsAdapter;
import com.news.demo.ui.utility.ToastUtility;
import com.news.demo.viewmodel.NewsViewModel;
import com.news.demo.viewmodel.ViewModelFactory;
import com.news.demo.workmanager.NewsWorker;
import com.news.demo.workmanager.ServerConfigSyncWorker;
import com.news.newsnotification.NewsNotificationLibrary;
import com.news.newsnotification.NotificationLibraryConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static com.news.demo.ui.AppConstant.INTENT_FILTER_NEWS_DETAILS_URL;
import static com.news.demo.ui.AppConstant.INTENT_FILTER_NEWS_TITLE;
import static com.news.demo.ui.AppConstant.INTENT_FILTER_NEW_NEWS;
import static com.news.demo.ui.AppConstant.NEWS_DATA_SYNC_TIME_KEY;
import static com.news.demo.ui.AppConstant.NEWS_TYPE_NEW;
import static com.news.demo.ui.AppConstant.NEWS_TYPE_OLD;
import static com.news.demo.ui.AppConstant.SAVED;
import static com.news.demo.ui.AppConstant.SERVER_SYNCING_THRESHOLD;
import static com.news.newsnotification.NewsNotificationLibrary.INTENT_FILTER_NOTIFICATION;
import static com.news.newsnotification.NotificationLibraryConstants.NOTIFICATION_VALUE;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private LinearLayout noInternetScreen;
    private Button tryAgainButton;
    private LinearLayoutManager mLayoutManager;
    private NewsAdapter newsAdapter;
    private List<NewsArticleEntity> newsEntityList;
    @Inject
    ViewModelFactory viewModelFactory;
    private NewsViewModel viewModel;
    private ProgressDialog mProgressLoader;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        injectDependency();
        initViews();
        setToolbar();
        setViews();
        initData();
        setListeners();
    }

    private void setListeners() {
        tryAgainButton.setOnClickListener(v -> {
            hideNoInternetScreen();
            getNewsData();
        });
    }

    private void setViews() {
        newsRecyclerView.setVisibility(View.VISIBLE);
        noInternetScreen.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initData() {
        getNewsData();
        scheduleBackgroundNewsDataFetch();
        scheduleSyncServerDataConfig();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerLatestNewsReceiver();
        registerNotificationNewsReceiver();
    }

    private void registerLatestNewsReceiver() {
        IntentFilter filter = new IntentFilter(INTENT_FILTER_NEW_NEWS);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(latestNewsReceiver, filter);

    }

    private void registerNotificationNewsReceiver() {
        IntentFilter notificationFilter = new IntentFilter(INTENT_FILTER_NOTIFICATION);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(notificationNewsReceiver, notificationFilter);
    }

    private void setToolbar() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        newsRecyclerView = (RecyclerView) findViewById(R.id.news_recyclerview);
        noInternetScreen = (LinearLayout) findViewById(R.id.no_internet_screen);
        tryAgainButton = (Button) findViewById(R.id.try_again_botton);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(mLayoutManager);
        newsAdapter = new NewsAdapter(this, newsEntityList, adapterCallback);
        newsRecyclerView.setAdapter(newsAdapter);
    }

    private void injectDependency() {
        NewsComponents components = DaggerNewsComponents.builder().build();
        components.injectNewsActivity(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewsViewModel.class);
    }

    private void getNewsData() {
        newsEntityList = new ArrayList<>();
        List<NewsArticleEntity> newsList = new ArrayList<>();

        if (NetworkUtility.isNetworkAvailable()) {
            newsList = getAllNews();
            if (newsList != null && !newsList.isEmpty()) {
                newsEntityList.addAll(newsList);
            } else {
                //int id = getNewsId();
                int id = getIntent().getIntExtra(
                        NotificationLibraryConstants.NOTIFICATION_VALUE, 0);
                getNewsFromNetwork(id);
            }
        } else {
            newsList = getUserSavedNews();
            if (newsList != null && !newsList.isEmpty()) {
                newsEntityList.addAll(newsList);
                ToastUtility.showToastShort("You are viewing only offline News\nTurn on internet to see updated news");
            } else {
                showNoInternetScreen();
            }
        }
        sortNewsListAccordingToTimestamp();
        setAdapterData();
    }

    private void sortNewsListAccordingToTimestamp() {
        if (newsEntityList != null && !newsEntityList.isEmpty())
            Collections.sort(newsEntityList, newsComparator);
    }

    Comparator<NewsArticleEntity> newsComparator = (first, second) -> {
        int result = (int) (first.getNewsTimeStamp() - second.getNewsTimeStamp()) * -1;
        return result;
    };

    private void showNoInternetScreen() {
        noInternetScreen.setVisibility(View.VISIBLE);
        newsRecyclerView.setVisibility(View.GONE);
    }

    private void hideNoInternetScreen() {
        newsRecyclerView.setVisibility(View.VISIBLE);
        noInternetScreen.setVisibility(View.GONE);
    }

    private void setAdapterData() {
        newsAdapter.setData(newsEntityList);
        newsAdapter.notifyDataSetChanged();
    }

    private AdapterCallback adapterCallback =
            new AdapterCallback() {
                @Override
                public void navigateToNewsDetailScreen(int position) {
                    String url = newsEntityList.get(position).
                            convertToNewsArticle().getUrl();
                    String title = newsEntityList.get(position).
                            convertToNewsArticle().getTitle();
                    Intent intent = new Intent(
                            NewsActivity.this, NewsDetailActivity.class);

                    intent.putExtra(INTENT_FILTER_NEWS_TITLE, title);
                    intent.putExtra(INTENT_FILTER_NEWS_DETAILS_URL, url);
                    startActivity(intent);
                    //   Toast.makeText(NewsActivity.this, "More " + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void shareNews(int position) {

                    shareNewsData(NewsActivity.this,
                            "NewsDemo", newsEntityList.get(position).
                                    convertToNewsArticle().getUrl());
                }

                @Override
                public void saveNews(int position) {
                    NewsArticleEntity entity = newsEntityList.get(position);
                    updateNews(entity);
                    /*Toast.makeText(NewsActivity.this, "Save" + position +
                            " , " + (entity.getNewsState() == NON_SAVE ?
                            NON_SAVE :
                            SAVED), Toast.LENGTH_SHORT).show();*/

                }
            };

    private void updateNews(NewsArticleEntity entity) {
        NewsDatabase.getAppDatabase().newsDAO().
                updateNewsItem(entity);
    }

    private void getNewsFromNetwork(int newsId) {
        showLoader();
        String[] newsNameList = getResources().getStringArray(R.array.news_sources);
        String newsName = newsNameList[newsId];
        viewModel.getNewsData(newsId).observe(this,
                (newsResponse) -> {
                    if (newsResponse.getNews() != null) {

                        if (newsAdapter != null) {
                            List<NewsArticle> newsList = newsResponse.getNews().getList();
                            List<NewsArticleEntity> dataList = new ArrayList<>();
                            for (NewsArticle newsItem : newsList) {
                                NewsArticleEntity entity = new NewsArticleEntity(
                                        newsItem.getUniqueData(),
                                        newsItem.convertGsonToString());
                                entity.setNewsType(NEWS_TYPE_OLD);
                                dataList.add(entity);
                                //newsEntityList.add(0, entity);
                            }
                            if (dataList != null &&
                                    !dataList.isEmpty()) {
                                NewsDatabase.getAppDatabase().newsDAO().
                                        insertNewsList(dataList);
                                ToastUtility.showToastShort(newsName + " News updated..!!");
                                newsEntityList.clear();
                                newsEntityList.addAll(getAllNews());
                            }
                            sortNewsListAccordingToTimestamp();
                            newsAdapter.setData(newsEntityList);
                            newsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtility.showToastLong(
                                newsResponse.getErrorModel().getError().getInfo());
                    }
                    hideLoader();
                }
        );
    }

    private void scheduleBackgroundNewsDataFetch() {

        PeriodicWorkRequest newsDataRequest = buildNewsDataWorkRequest();
        WorkManager.getInstance().enqueue(newsDataRequest);
    }


    private void downloadNotificationNewsFeed(String newsId) {
       /* OneTimeWorkRequest.Builder notificationWorker =
                new OneTimeWorkRequest.Builder(DownloadNotificationNewsWorker.class);
        notificationWorker.setInputData(createInputData(newsId));
        WorkManager.getInstance().enqueue(notificationWorker.build());*/

        int id = Integer.parseInt(newsId);
        getNewsFromNetwork(id);
    }

    private PeriodicWorkRequest buildNewsDataWorkRequest() {
        int time = NewsPreferenceManager.getInstance().getInt(
                NEWS_DATA_SYNC_TIME_KEY);
        Constraints constraints = new Constraints.Builder().
                build();
        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(
                        NewsWorker.class, time, TimeUnit.MINUTES).build();
        //  setConstraints(constraints).build();
        return workRequest;
    }

    private void scheduleSyncServerDataConfig() {

        Constraints constraints = new Constraints.Builder().
                build();
        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(
                        ServerConfigSyncWorker.class, SERVER_SYNCING_THRESHOLD,
                        TimeUnit.HOURS).build();
        WorkManager.getInstance().enqueue(workRequest);

       /*     viewModel.getServerConfigData().observe(this,
                    (serverConfigResponse) -> {
                        if (serverConfigResponse.getConfigData() != null) {
                            NewsPreferenceManager.getInstance().setLong(
                                    NEWS_DATA_SYNC_TIME_KEY, serverConfigResponse.getConfigData().
                                            getNewsDataSyncTime());
                            NewsPreferenceManager.getInstance().setLong(
                                    SERVER_SYNC_TIME_KEY, System.currentTimeMillis());
                        } else {

                        }
                    }
            );*/
    }

    private List<NewsArticleEntity> getAllNews() {
        return NewsDatabase.getAppDatabase().newsDAO().getAllNews();
    }

    private List<NewsArticleEntity> getLatestNews() {
        return NewsDatabase.getAppDatabase().newsDAO().getAllNewsByType(NEWS_TYPE_NEW);
    }

    private List<NewsArticleEntity> getOldNews() {
        return NewsDatabase.getAppDatabase().newsDAO().getAllNewsByType(NEWS_TYPE_OLD);
    }

    private List<NewsArticleEntity> getUserSavedNews() {
        return NewsDatabase.getAppDatabase().newsDAO().
                getSavedNews(SAVED);
    }

    public void shareNewsData(Activity activity, String text, String url) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //    shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Insert Subject here");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text + " " + url);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
        PackageManager pm = activity.getPackageManager();

    }

    public static void shareTwitter(Activity activity, String text, String url, String via, String hashtags) {
        StringBuilder tweetUrl = new StringBuilder("https://twitter.com/intent/tweet?text=");
        tweetUrl.append(TextUtils.isEmpty(text) ? urlEncode(" ") : urlEncode(text));
        if (!TextUtils.isEmpty(url)) {
            tweetUrl.append("&url=");
            tweetUrl.append(urlEncode(url));
        }
        if (!TextUtils.isEmpty(via)) {
            tweetUrl.append("&via=");
            tweetUrl.append(urlEncode(via));
        }
        if (!TextUtils.isEmpty(hashtags)) {
            tweetUrl.append("&hastags=");
            tweetUrl.append(urlEncode(hashtags));
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl.toString()));
        List<ResolveInfo> matches = activity.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }
        activity.startActivity(intent);
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf("wtf", "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterLatestNewsReceiver();
        UnRegisterNotificationNewsReceiver();
        NewsNotificationLibrary.destroy();
        super.onDestroy();
    }

    private void showLoader() {
        if (mProgressLoader == null) {
            mProgressLoader = new ProgressDialog(this);
            mProgressLoader.setMessage("Getting Latest News Articles...");
            mProgressLoader.setTitle("Please wait..!!");
            mProgressLoader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressLoader.setCancelable(false);
        }
        mProgressLoader.show();
    }

    private void hideLoader() {
        if (mProgressLoader != null) {
            mProgressLoader.dismiss();
        }
    }

    private BroadcastReceiver latestNewsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<NewsArticleEntity> latestNewsList = getLatestNews();
            if (latestNewsList.size() > newsEntityList.size()) {
                setNewsAsOldAndUpdateToCache(latestNewsList);
                newsEntityList.addAll(0, latestNewsList);
                sortNewsListAccordingToTimestamp();
                newsAdapter.notifyDataSetChanged();
                newsRecyclerView.smoothScrollToPosition(latestNewsList.size() + 1);
            }
            ToastUtility.showToastShort("News Feed updated\nScroll up to see latest news");
        }
    };

    private void setNewsAsOldAndUpdateToCache(List<NewsArticleEntity> latestNewsList) {
        for (NewsArticleEntity entity : latestNewsList) {
            entity.setNewsType(NEWS_TYPE_OLD);
            updateNewsToCache(entity);
        }
    }

    private void updateNewsToCache(NewsArticleEntity entity) {
        NewsDatabase.getAppDatabase().newsDAO().updateNewsItem(entity);
    }

    private BroadcastReceiver notificationNewsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            downloadNotificationNewsFeed(intent.getStringExtra(NOTIFICATION_VALUE));
        }
    };

    private void UnRegisterNotificationNewsReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationNewsReceiver);
    }

    private void unRegisterLatestNewsReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(latestNewsReceiver);
    }
}
