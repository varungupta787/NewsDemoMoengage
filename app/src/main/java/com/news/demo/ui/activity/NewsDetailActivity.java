package com.news.demo.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.news.demo.R;
import com.news.demo.ui.utility.ToastUtility;

import static com.news.demo.ui.AppConstant.INTENT_FILTER_NEWS_DETAILS_URL;
import static com.news.demo.ui.AppConstant.INTENT_FILTER_NEWS_TITLE;

public class NewsDetailActivity extends AppCompatActivity {

    private WebView newsDetailWB;
    private ProgressDialog mProgressLoader;
    private Toolbar mToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        newsDetailWB = (WebView) findViewById(R.id.wv_news_detail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();
        setToolbarTitle();
        loadWebViewData();
    }

    private void setToolbar() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.toolbar_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbarTitle() {
        String title = getIntent().getStringExtra(INTENT_FILTER_NEWS_TITLE);
        if (title == null || title.isEmpty())
            return;
        if (null != mToolbar) {
            if (title.length() > 30) {
                mToolbar.setTitle(title.substring(0, 20) + "....");
            } else {
                mToolbar.setTitle(title);
            }
        }
    }

    protected void setToolbarNavigationIcon(int drawable) {
        if (null != mToolbar) {
            if (drawable != 0) {
                mToolbar.setNavigationIcon(drawable);
            } else {
                mToolbar.setNavigationIcon(null);
            }
       //     setSupportActionBar(mToolbar);
        }
    }

    private void loadWebViewData() {
        newsDetailWB.getSettings().setJavaScriptEnabled(true);
        newsDetailWB.getSettings().setDomStorageEnabled(true);
        newsDetailWB.getSettings().setLoadsImagesAutomatically(true);
        newsDetailWB.getSettings().setBuiltInZoomControls(true);
        newsDetailWB.setWebChromeClient(new WebChromeClient());

        newsDetailWB.setWebViewClient(new WebViewClients());
        newsDetailWB.loadUrl(getIntent().getStringExtra(INTENT_FILTER_NEWS_DETAILS_URL));
    }


    private void showLoader() {
        if (mProgressLoader == null) {
            mProgressLoader = new ProgressDialog(this);
            mProgressLoader.setMessage("Loading News Details...");
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

    public class WebViewClients extends WebViewClient {

        public WebViewClients() {
            showLoader();
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideLoader();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            hideLoader();
            ToastUtility.showToastShort(getResources().getString(R.string.news_detail_loading_error));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        hideLoader();
        super.onBackPressed();
    }
}
