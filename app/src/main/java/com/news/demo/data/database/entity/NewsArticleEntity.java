package com.news.demo.data.database.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.news.demo.models.NewsArticle;
import com.news.demo.ui.AppConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.news.demo.ui.AppConstant.NEWS_ARTICLE_TABLE;
import static com.news.demo.ui.AppConstant.NEWS_TYPE_NEW;
import static com.news.demo.ui.AppConstant.NON_SAVE;

@Entity(tableName = NEWS_ARTICLE_TABLE)
public class NewsArticleEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    private int id;

    @ColumnInfo(name = AppConstant.UNIQUE_KEY)
    private String uniqueKey;

    @ColumnInfo(name = AppConstant.NEWS_ARTICLE_DATA)
    private String newsArticleData;

    @ColumnInfo(name = AppConstant.NEWS_TIMESTAMP)
    private long newsTimeStamp;

    @ColumnInfo(name = AppConstant.NEWS_STATE)
    private int newsState;

    @ColumnInfo(name = AppConstant.NEWS_TYPE)
    private int newsType;

    public NewsArticleEntity(String uniqueKey, String newsArticleData) {
        this.uniqueKey = uniqueKey;
        this.newsArticleData = newsArticleData;
        this.newsTimeStamp = getCurrentTime();
        this.newsState = NON_SAVE;
        this.newsType = NEWS_TYPE_NEW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getNewsArticleData() {
        return newsArticleData;
    }

    public void setNewsArticleData(String newsArticleData) {
        this.newsArticleData = newsArticleData;
    }

    public NewsArticle convertToNewsArticle() {
        Gson g = new Gson();
        return g.fromJson(getNewsArticleData(), NewsArticle.class);
    }

    public long getNewsTimeStamp() {
        return newsTimeStamp;
    }

    public void setNewsTimeStamp() {
        this.newsTimeStamp = getCurrentTime();
    }

    public void setNewsTimeStamp(long time) {
        this.newsTimeStamp = time;
    }

    public long getCurrentTime() {
        long currentTime = 0;
        try {
            //delay for 1 millisecond
            Thread.sleep(1);

            Date d = new Date();
            SimpleDateFormat systemdateFromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String systemTimeString = systemdateFromat.format(d.getTime());
            try {
                Date systemCurrentTimeString = systemdateFromat.parse(systemTimeString);
                currentTime = systemCurrentTimeString.getTime();
            } catch (ParseException e) {
            }
        } catch (InterruptedException e) {
            return currentTime;
        }
        return currentTime;
    }

    /*public AppConstant.DataState getNewsState() {
        return newsState == NON_SAVE ? AppConstant.DataState.Save : AppConstant.DataState.Saved;
    }*/

    public int getNewsState() {
        return newsState;
    }

    /*public void setNewsState(AppConstant.DataState newsState) {

        this.newsState = (newsState == AppConstant.DataState.Save) ? NON_SAVE : SAVED;
    }*/

    public void setNewsState(int newsState) {
        this.newsState = newsState;
    }

    public int getNewsType() {
        return newsType;
    }

    public void setNewsType(int newsType) {
        this.newsType = newsType;
    }

    public void toggleNewsState() {
        if (this.newsState == AppConstant.NON_SAVE) {
            this.newsState = AppConstant.SAVED;
        } else {
            this.newsState = AppConstant.NON_SAVE;
        }
    }
}
