package com.news.demo.models;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.news.demo.ui.AppConstant;

public class NewsArticle {
    //private AppConstant.DataState newsState = AppConstant.DataState.Save;
    private Source source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /*public AppConstant.DataState getNewsState() {
        return newsState;
    }

    public void setNewsState(AppConstant.DataState newsState) {
        this.newsState = newsState;
    }*/

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        NewsArticle newsArticleObj = (NewsArticle) obj;
        return getUniqueData().
                equalsIgnoreCase(getUniqueData(newsArticleObj));
    }

    public String getUniqueData() {
        return getUniqueData(this);
    }

    public String getUniqueData(NewsArticle newsArticle) {
        String uniqueVal = "";
        if (newsArticle != null &&
                newsArticle.getUrl() != null &&
                !newsArticle.getUrl().isEmpty() &&
                newsArticle.getUrlToImage() != null &&
                !newsArticle.getUrlToImage().isEmpty()) {
            uniqueVal = newsArticle.getUrl().trim().toLowerCase() +
                    newsArticle.getUrlToImage().trim().toLowerCase();
        }
        return uniqueVal.toLowerCase();
    }

    public String convertGsonToString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}
