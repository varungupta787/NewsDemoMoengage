package com.news.demo.models;

public class ServerConfigData {

    String newsDataSyncTime;

    public int getNewsDataSyncTime() {
        return Integer.parseInt(newsDataSyncTime);
    }

    public void setNewsDataSyncTime(int newsDataSyncTime) {
        this.newsDataSyncTime = String.valueOf(newsDataSyncTime);
    }
}
