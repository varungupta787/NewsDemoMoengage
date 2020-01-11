package com.news.demo;

import android.app.Application;
import android.content.Context;

import com.news.newsnotification.NewsNotificationLibrary;


public class NewsApplication extends Application {


    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        /*  initialize notification library */
        NewsNotificationLibrary.initialize(getContext());

    }

    static public Context getContext() {
        return context;
    }
}
