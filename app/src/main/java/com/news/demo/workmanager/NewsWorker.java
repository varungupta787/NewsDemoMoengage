/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.news.demo.workmanager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.news.demo.NewsApplication;
import com.news.demo.data.database.NewsDAO;
import com.news.demo.data.database.NewsDatabase;
import com.news.demo.data.database.entity.NewsArticleEntity;
import com.news.demo.data.network.ApiService;
import com.news.demo.data.repository.NewsRepository;
import com.news.demo.data.repository.RepositoryUtil;
import com.news.demo.models.News;
import com.news.demo.models.NewsArticle;
import com.news.demo.viewmodel.NewsViewModel;
import com.news.demo.viewmodel.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.news.demo.ui.AppConstant.INTENT_FILTER_NEW_NEWS;
import static com.news.demo.ui.AppConstant.NEWS_TYPE_NEW;

public class NewsWorker extends Worker {

    /**
     * Creates an instance of the {@link Worker}.
     *
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */


    @Inject
    NewsRepository newsRepository;
    @Inject
    ApiService apiService;

    public NewsWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    private static final String TAG = NewsWorker.class.getSimpleName();
    @Inject
    ViewModelFactory viewModelFactory;
    private NewsViewModel viewModel;
    Context applicationContext;

    @NonNull
    @Override
    public Worker.Result doWork() {
        applicationContext =
                NewsApplication.getContext().getApplicationContext();
        getLatestNewsData();
        return Result.success();
    }


    public void getLatestNewsData() {

        if (apiService != null) {
            for (int i=0; i<3; i++) {
                Call<News> newsCall = RepositoryUtil.generateNewsApiCall(apiService, i);
                newsCall.enqueue(new Callback<News>() {
                    @Override
                    public void onResponse(Call<News> call, Response<News> response) {
                        if (response.isSuccessful()) {
                            News news = response.body();
                            if (news != null && news.getList() != null &&
                                    !news.getList().isEmpty()) {
                                for (NewsArticle newsItem : news.getList()) {
                                    NewsArticleEntity entity = new NewsArticleEntity(
                                            newsItem.getUniqueData(),
                                            newsItem.convertGsonToString());
                                    checkAndInsertNewNews(entity);
                                }
                            }
                        }
                        checkForNewNewsUpdate();
                    }

                    @Override
                    public void onFailure(Call<News> call, Throwable t) {
                        // t.getCause();
                    }
                });
            }
        }
    }

    public void checkForNewNewsUpdate() {
        NewsDAO dao = NewsDatabase.getAppDatabase().newsDAO();
        List<NewsArticleEntity> list = dao.getAllNewsByType(NEWS_TYPE_NEW);
        if (list != null && !list.isEmpty()) {
            sendBroadcastForLatestNews();
        }
    }

    public void sendBroadcastForLatestNews() {
        Intent intent = new Intent();
        intent.setAction(INTENT_FILTER_NEW_NEWS);
        LocalBroadcastManager.getInstance(
                NewsApplication.getContext().getApplicationContext()).
                sendBroadcast(intent);
    }

    private void checkAndInsertNewNews(NewsArticleEntity entity) {
        NewsDAO dao = NewsDatabase.getAppDatabase().newsDAO();
        String value = dao.getNewsItemWithUniqueKey(entity.getUniqueKey());
        if (value == null || value.isEmpty()) {
            dao.insertSingleNews(entity);
        }
    }
}
