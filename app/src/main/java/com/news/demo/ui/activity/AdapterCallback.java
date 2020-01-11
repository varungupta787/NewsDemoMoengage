package com.news.demo.ui.activity;

public interface AdapterCallback {
    void navigateToNewsDetailScreen(int position);
    void shareNews(int position);
    void saveNews(int position);
}
