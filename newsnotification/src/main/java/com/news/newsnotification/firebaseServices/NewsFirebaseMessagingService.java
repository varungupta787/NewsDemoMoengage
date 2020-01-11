package com.news.newsnotification.firebaseServices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.news.newsnotification.NewsNotificationLibrary;
import com.news.newsnotification.NotificationBroadcastManager;
import com.news.newsnotification.NotificationLibraryConstants;
import com.news.newsnotification.R;

import java.util.Map;

import static com.news.newsnotification.NewsNotificationLibrary.INTENT_FILTER_NOTIFICATION;


public class NewsFirebaseMessagingService extends FirebaseMessagingService {

    public static int NOTIFICATION_ID = 1001;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().isEmpty()) {
            createNotification(remoteMessage.getNotification().getBody(),
                    remoteMessage.getNotification().getTitle(), "");

        } else {
            Map<String, String> data = remoteMessage.getData();
            createNotification(data.get(NotificationLibraryConstants.NOTIFICATION_MESSAGE),
                    data.get(NotificationLibraryConstants.NOTIFICATION_TITLE),
                    data.get(NotificationLibraryConstants.NOTIFICATION_VALUE));
        }
    }

    private void createNotification(String message, String title, String value) {

        NotificationCompat.Builder mBuilder = getNotificationBuilder(message, title);

        PendingIntent pendingIntent = getPendingIntent(message, title, value);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        NewsNotificationLibrary.setupNotificationChannel();

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (NOTIFICATION_ID > 1073741) {
            NOTIFICATION_ID = 1001;
        }
        manager.notify(NOTIFICATION_ID++, mBuilder.build());
    }

    private PendingIntent getPendingIntent(String message, String title, String value) {
        Intent intent = new Intent(this, NotificationBroadcastManager.class);
        intent.putExtra(NotificationLibraryConstants.NOTIFICATION_TITLE, title);
        intent.putExtra(NotificationLibraryConstants.NOTIFICATION_MESSAGE, message);
        intent.putExtra(NotificationLibraryConstants.NOTIFICATION_VALUE, value);
        intent.setAction("com.example.androidtest.BroadcastReceiver");

        return PendingIntent.getBroadcast(this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Builder getNotificationBuilder(String message, String title) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.news_small_icon2);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(title + " News available");
        return new NotificationCompat.Builder(this,
                NotificationLibraryConstants.NOTIFICATION_CHANNEL_NAME)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.news_small_icon2)
                .setColor(Color.parseColor("#ed1c24"))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(bigTextStyle)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    }

}
