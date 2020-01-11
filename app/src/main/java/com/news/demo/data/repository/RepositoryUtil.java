package com.news.demo.data.repository;

import com.news.demo.data.network.ApiService;
import com.news.demo.models.News;

import retrofit2.Call;

import static com.news.demo.ui.AppConstant.NEWS_API_KEY;
import static com.news.newsnotification.NotificationLibraryConstants.NEWS_BUSINESS_ID;
import static com.news.newsnotification.NotificationLibraryConstants.NEWS_TECH_CHRUNCH_ID;
import static com.news.newsnotification.NotificationLibraryConstants.NEWS_WALL_STREEL_JOURNAL_ID;

public class RepositoryUtil {

    public static Call<News> generateNewsApiCall(ApiService apiService, int newsId) {
        switch (newsId) {
            case NEWS_TECH_CHRUNCH_ID: {
                return apiService.getTechCrunchNewsData("techcrunch",
                        NEWS_API_KEY);
            }
            case NEWS_BUSINESS_ID: {
                return apiService.getBusinessNewsData("us", "business",
                        NEWS_API_KEY);

            }
            case NEWS_WALL_STREEL_JOURNAL_ID: {
                return apiService.getWallStreetNewsData("wsj.com",
                        NEWS_API_KEY);

            }
        }
        return null;
    }
}
