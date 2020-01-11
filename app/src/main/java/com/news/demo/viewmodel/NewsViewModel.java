package com.news.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.news.demo.data.repository.NewsRepository;
import com.news.demo.models.NewsResponse;
import com.news.demo.models.ServerConfigDataResponse;

import javax.inject.Inject;

public class NewsViewModel extends ViewModel {

    NewsRepository newsRepository;

    @Inject
    public NewsViewModel(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public LiveData<NewsResponse> getNewsData(int newsId) {
        return newsRepository.getNewsData(newsId);
    }

    public LiveData<ServerConfigDataResponse> getServerConfigData() {
        return newsRepository.getServerConfigData();
    }
}
