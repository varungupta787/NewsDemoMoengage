package com.news.demo.data.database;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.news.demo.NewsApplication;
import com.news.demo.data.database.entity.NewsArticleEntity;

import static com.news.demo.ui.AppConstant.NEWS_DATABASE;

@Database(entities = {NewsArticleEntity.class},
        version = 1, exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {
    private static NewsDatabase INSTANCE;

    public abstract NewsDAO newsDAO();


    public static NewsDatabase getAppDatabase() {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(NewsApplication.getContext(), NewsDatabase.class, NEWS_DATABASE)
                            // allow queries on the main thread.
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


}
