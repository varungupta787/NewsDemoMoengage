package com.news.demo.data.network;

import com.news.demo.models.News;
import com.news.demo.models.ServerConfigData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/v2/top-headlines/")
    Call<News> getTechCrunchNewsData(
            @Query("sources") String source,
            @Query("apiKey") String api_key);

    @GET("/v2/top-headlines/")
    Call<News> getBusinessNewsData(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String api_key);

    @GET("/v2/top-headlines/")
    Call<News> getWallStreetNewsData(
            @Query("domains") String domains,
            @Query("apiKey") String api_key);


    @GET("/server/config")
    Call<ServerConfigData> getServerConfigData();
}
