package com.news.demo.models;

import java.util.List;

public class News {

    private String status;
    private int totalResults;
    private List<NewsArticle> articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<NewsArticle> getList() {
        return articles;
    }

    public void setList(List<NewsArticle> list) {
        this.articles = list;
    }
}
