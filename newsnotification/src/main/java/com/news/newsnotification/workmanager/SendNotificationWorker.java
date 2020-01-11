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

package com.news.newsnotification.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.news.newsnotification.NewsNotificationLibrary;
import com.news.newsnotification.R;

import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.news.newsnotification.NewsNotificationLibrary.getNewsId;
import static com.news.newsnotification.NotificationLibraryConstants.FCM_URL;
import static com.news.newsnotification.NotificationLibraryConstants.FCM_SERVER_KEY;
import static com.news.newsnotification.NotificationLibraryConstants.TOPIC;

public class SendNotificationWorker extends Worker {


    /**
     * Creates an instance of the {@link Worker}.
     *
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */

    public SendNotificationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    private static final String TAG = SendNotificationWorker.class.getSimpleName();


    @NonNull
    @Override
    public Result doWork() {
        sendNotification();
        return Result.success();
    }

    public String sendNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);
        String[] newsList = NewsNotificationLibrary.getContext().
                getResources().getStringArray(R.array.news_sources);

        int newsId = getNewsId();

        String title = newsList[newsId];
        String message = "Click to see the latest "+ newsList[newsId];
        String value = String.valueOf(newsId);

        return callFCMNotificationApi(getBodyJsonString(title, message, value));
    }

    private String getBodyJsonString(String title, String message, String value) {
        return "{\n" +
                "  \"to\": \"/topics/" + TOPIC + "\",\n" +
                "  \"data\": {\n" +
                "    \"title\":\"" + title + "\",\n" +
                "    \"message\":\"" + message + "\",\n" +
                "    \"value\":\"" + value + "\"" +
                "   }\n" +
                "}";
    }

    private String callFCMNotificationApi(String requestBodyData) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(requestBodyData, JSON);

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=" + FCM_SERVER_KEY)
                .url(FCM_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String msgId = response.body().string();
            return msgId;
        } catch (IOException e) {
        }
        return "";
    }
}
