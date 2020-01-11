package com.news.demo.models;

public class NewsResponse {

    private News news;
    private ErrorModel errorModel;

    public NewsResponse() {
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ErrorModel errorModel) {
        this.errorModel = errorModel;
    }
}
