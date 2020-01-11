package com.news.newsnotification;

public interface NotificationLibraryConstants {

    String FCM_SENDER_ID = "263999577790";
    String FCM_SERVER_KEY = "AAAAPXeZ3r4:APA91bHlxoXQgBw2rKnDj0kt6qIKC5so0E3b4XeHwp29707IlQQoVYQc1jY9IaStvZ-YGX8XzkaTFwbb91TCiPxxI29pn70uU0GbpL0L4Eq06C4xyJBlyp0fU6DOP1aNk1F1CeKVRqJB";
    String FCM_URL = "https://fcm.googleapis.com/fcm/send";


    //String FCM_URL = "@gcm.googleapis.com";


    String NOTIFICATION_CHANNEL_NAME = "news_notification_channel";
    String NOTIFICATION_NAME = "NewsDemoNotification";
    String NOTIFICATION_DESCRIPTION = "NewsDescription";

    String NOTIFICATION_ADMINCHANNEL_NAME = "New notification";
    String NOTIFICATION_ADMINCHANNEL_DESCRIPTION = "Device to devie notification";


    String NOTIFICATION_MESSAGE = "message";
    String NOTIFICATION_TITLE = "title";
    String NOTIFICATION_VALUE = "value";

    String TOPIC = "NewsUsers";

        /*  News ID */
    int NEWS_TECH_CHRUNCH_ID = 0;
    int NEWS_BUSINESS_ID = 1;
    int NEWS_WALL_STREEL_JOURNAL_ID = 2;
}
