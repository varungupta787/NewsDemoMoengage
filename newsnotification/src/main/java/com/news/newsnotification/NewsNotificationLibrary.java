package com.news.newsnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.news.newsnotification.workmanager.SendNotificationWorker;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.news.newsnotification.NotificationLibraryConstants.NOTIFICATION_ADMINCHANNEL_DESCRIPTION;
import static com.news.newsnotification.NotificationLibraryConstants.NOTIFICATION_ADMINCHANNEL_NAME;
import static com.news.newsnotification.NotificationLibraryConstants.NOTIFICATION_CHANNEL_NAME;
import static com.news.newsnotification.NotificationLibraryConstants.NOTIFICATION_DESCRIPTION;
import static com.news.newsnotification.NotificationLibraryConstants.NOTIFICATION_NAME;

public class NewsNotificationLibrary {
    private static Context applicationContext = null;

    /*  Notification Intent Filter  */
    public static String INTENT_FILTER_NOTIFICATION = "intent_filter_notification";

    public static void initialize(Context context) {
        applicationContext = context.getApplicationContext();
        init();
    }

    private static void init() {
        setupNotificationChannel();
        scheduleNotificationSender();
    }

    public static void destroy() {
        applicationContext = null;
    }

    public static Context getContext() {
        return applicationContext;
    }

    public static void setContext(Context applicationContext) {
        NewsNotificationLibrary.applicationContext = applicationContext;
    }

    private static void scheduleNotificationSender() {
        PeriodicWorkRequest newsDataRequest = buildSendNotificationWorkRequest();
        WorkManager.getInstance().enqueue(newsDataRequest);
    }

    public static void setupNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_NAME,
                    NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setDescription(NOTIFICATION_DESCRIPTION);
            serviceChannel.enableLights(true);
            serviceChannel.setLightColor(Color.RED);
            serviceChannel.enableVibration(true);
            NotificationManager manager = (NotificationManager) getContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private static PeriodicWorkRequest buildSendNotificationWorkRequest() {
        Constraints constraints = new Constraints.Builder().
                build();
        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(SendNotificationWorker.class,
                        16, TimeUnit.MINUTES).build();
        //  setConstraints(constraints).build();
        return workRequest;
    }

    public static int getNewsId() {
        Random r = new Random();
        return r.nextInt(3);
    }


}
