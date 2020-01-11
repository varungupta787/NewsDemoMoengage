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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.news.demo.data.Preferences.NewsPreferenceManager;
import com.news.demo.models.ServerConfigData;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mock.Behavior;
import okhttp3.mock.MockInterceptor;

import static com.news.demo.data.Urls.BASE_URL;
import static com.news.demo.ui.AppConstant.NEWS_DATA_SYNC_TIME_KEY;
import static com.news.demo.ui.AppConstant.NEWS_UPDATE_THRESHOLD;
import static okhttp3.mock.MediaTypes.MEDIATYPE_JSON;

public class ServerConfigSyncWorker extends Worker {

    /**
     * Creates an instance of the {@link Worker}.
     *
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */

    public ServerConfigSyncWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    private static final String TAG = ServerConfigSyncWorker.class.getSimpleName();

    @NonNull
    @Override
    public Result doWork() {
        getServerConfigData();
        return Result.success();
    }

    public void getServerConfigData() {
        try {
            /*  Mocking Server config Api request and Response  */
            MockInterceptor interceptor = getInterceptorForApiMocking();

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            client.newCall(new Request.Builder().url(BASE_URL + "/server/config").build()).
                    enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            //handle error case or apply retry policy
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            Gson g = new Gson();
                            ServerConfigData obj = g.fromJson(response.body().string(), ServerConfigData.class);
                            if (response.isSuccessful()) {
                                if (obj != null) {
                                    NewsPreferenceManager.
                                            getInstance().setInt(NEWS_DATA_SYNC_TIME_KEY,
                                            obj.getNewsDataSyncTime());
                                } else {
                                    //handle error case or apply retry policy
                                }
                            } else {
                                //handle error case or apply retry policy
                            }
                        }
                    });
        } catch (Exception e) {
        }
    }

    @NotNull
    private MockInterceptor getInterceptorForApiMocking() {
        MockInterceptor interceptor =
                new MockInterceptor(Behavior.UNORDERED);
        interceptor.addRule()
                .url(BASE_URL + "/server/config")
                .respond(getMockingServerResponseJsonString(),
                        MEDIATYPE_JSON).code(200);
        return interceptor;
    }

    private String getMockingServerResponseJsonString() {
        String timeStr = String.valueOf(NEWS_UPDATE_THRESHOLD);
        return "{\n" +
                "  \"newsDataSyncTime\": \"" + timeStr + "\"\n" +
                "}";
    }
}
