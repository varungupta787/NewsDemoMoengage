package com.news.newsnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.news.newsnotification.NewsNotificationLibrary.INTENT_FILTER_NOTIFICATION;

public class NotificationBroadcastManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent localIntent = new Intent();
        localIntent.putExtra(NotificationLibraryConstants.NOTIFICATION_TITLE,
                intent.getStringExtra(NotificationLibraryConstants.NOTIFICATION_TITLE));
        localIntent.putExtra(NotificationLibraryConstants.NOTIFICATION_MESSAGE,
                intent.getStringExtra(NotificationLibraryConstants.NOTIFICATION_MESSAGE));
        localIntent.putExtra(NotificationLibraryConstants.NOTIFICATION_VALUE,
                intent.getStringExtra(NotificationLibraryConstants.NOTIFICATION_VALUE));
        localIntent.setAction(INTENT_FILTER_NOTIFICATION);
        LocalBroadcastManager.getInstance(
                NewsNotificationLibrary.getContext()).
                sendBroadcast(localIntent);
    }
}
