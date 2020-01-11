package com.news.demo.ui;

public interface AppConstant {


    long SERVER_SYNCING_THRESHOLD = 24; /* 24 hours */
    int NEWS_UPDATE_THRESHOLD = 20; /* 20 Minutes */

    String SERVER_SYNC_TIME_KEY = "server_sync_time_key";
    String NEWS_DATA_SYNC_TIME_KEY = "news_data_sync_time_key";

    /*  News Saving state   */
    int NON_SAVE = 1;
    int SAVED = 2;

    /*  News Type   */
    int NEWS_TYPE_NEW = 1;
    int NEWS_TYPE_OLD = 2;

    /*  News Database   */
    String NEWS_DATABASE = "news_database";
    String NEWS_ARTICLE_TABLE = "news_article";

    /*  News Article Columns names  */
    String UNIQUE_KEY = "unique_key";
    String NEWS_ARTICLE_DATA = "news_article_data";
    String NEWS_TIMESTAMP = "news_timestamp";
    String NEWS_STATE = "news_state";
    String NEWS_TYPE = "news_type";

    /*  Latest News Intent Filter*/
    String INTENT_FILTER_NEW_NEWS = "intent_filter_new_news";
    String INTENT_FILTER_NEWS_TITLE = "intent_filter_news_title";
    String INTENT_FILTER_NEWS_DETAILS_URL = "intent_filter_news_details_url";

    String NEWS_API_KEY = "ec1997c8a19c4ae6b6fb361be25991dd";


}
