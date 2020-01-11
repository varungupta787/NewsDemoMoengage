package com.news.demo.data.database;

import android.content.ClipData;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.news.demo.data.database.entity.NewsArticleEntity;
import com.news.demo.ui.AppConstant;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;
import static com.news.demo.ui.AppConstant.NEWS_TYPE_NEW;
import static com.news.demo.ui.AppConstant.NEWS_TYPE_OLD;

@Dao
public interface NewsDAO {


    /*  get all types of news entity    */
    @Query("SELECT * FROM " + AppConstant.NEWS_ARTICLE_TABLE)
    List<NewsArticleEntity> getAllNews();


    /*  get all news entity with given newsState    */
    @Query("SELECT * FROM " + AppConstant.NEWS_ARTICLE_TABLE +
            " WHERE " + AppConstant.NEWS_STATE + " == :newsState")
    List<NewsArticleEntity> getSavedNews(int newsState);


    /*  get all NEW / OLD news entity    */
    @Query("SELECT * FROM " + AppConstant.NEWS_ARTICLE_TABLE +
            " WHERE " + AppConstant.NEWS_TYPE + " == :newsType")
    List<NewsArticleEntity> getAllNewsByType(int newsType);


    /*  get all OLD news entity    */
    /*@Query("SELECT * FROM " + AppConstant.NEWS_ARTICLE_TABLE +
            " WHERE " + AppConstant.NEWS_TYPE + " == :newsType")
    List<NewsArticleEntity> getAllOldNews(int newsType);*/


    /*  delete all news entity from db  */
    @Query("DELETE FROM " + AppConstant.NEWS_ARTICLE_TABLE)
    void deleteAllNewsIntoDB();

    @Query("DELETE FROM " + AppConstant.NEWS_ARTICLE_TABLE +
            " WHERE " + AppConstant.UNIQUE_KEY + " == :uniqueKey ")
    void deleteSingleNews(String uniqueKey);


    /*  insert single news entity   */
    @Insert(onConflict = REPLACE)
    void insertSingleNews(NewsArticleEntity newsArticleEntity);


    /*    insert list of news entities  */
    @Insert(onConflict = REPLACE)
    void insertNewsList(List<NewsArticleEntity> newsArticleEntityList);


    /*   check that news entity with particular unique key exists in DB or not   */
    @Query("SELECT " + AppConstant.UNIQUE_KEY + " FROM "
            + AppConstant.NEWS_ARTICLE_TABLE +
            " WHERE " + AppConstant.UNIQUE_KEY + " == :uniqueKey ")
    String getNewsItemWithUniqueKey(String uniqueKey);


    /* update single news entity*/
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNewsItem(NewsArticleEntity newsArticleEntity);

}
