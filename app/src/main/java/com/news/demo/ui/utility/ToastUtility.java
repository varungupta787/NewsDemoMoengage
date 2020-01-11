package com.news.demo.ui.utility;

import android.widget.Toast;

import com.news.demo.NewsApplication;
import com.news.demo.ui.activity.NewsActivity;

public class ToastUtility {

    public static void showToastShort(String data) {
        Toast.makeText(NewsApplication.getContext().getApplicationContext(),
                data,
                Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(String data) {
        Toast.makeText(NewsApplication.getContext().getApplicationContext(),
                data,
                Toast.LENGTH_LONG).show();
    }
}
