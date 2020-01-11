package com.news.demo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.news.demo.R;
import com.news.newsnotification.NotificationLibraryConstants;

import static com.news.newsnotification.NewsNotificationLibrary.getNewsId;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, NewsActivity.class);
                if (getIntent().hasExtra(NotificationLibraryConstants.NOTIFICATION_VALUE)) {
                    /*intent.putExtra(NotificationLibraryConstants.NOTIFICATION_TITLE,
                            getIntent().getStringArrayExtra(NotificationLibraryConstants.NOTIFICATION_TITLE));
                    intent.putExtra(NotificationLibraryConstants.NOTIFICATION_MESSAGE,
                            getIntent().getStringArrayExtra(NotificationLibraryConstants.NOTIFICATION_MESSAGE));*/
                    intent.putExtra(NotificationLibraryConstants.NOTIFICATION_VALUE,
                            getIntent().getStringArrayExtra(NotificationLibraryConstants.NOTIFICATION_VALUE));

                } else {
                    intent.putExtra(NotificationLibraryConstants.NOTIFICATION_VALUE,getNewsId());
                }
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
}
