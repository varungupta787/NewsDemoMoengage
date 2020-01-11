package com.news.demo.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.news.demo.data.network.ApiService;
import com.news.demo.models.Error;
import com.news.demo.models.ErrorModel;
import com.news.demo.models.News;
import com.news.demo.models.NewsArticle;
import com.news.demo.models.NewsResponse;
import com.news.demo.models.ServerConfigData;
import com.news.demo.models.ServerConfigDataResponse;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mock.Behavior;
import okhttp3.mock.MockInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.news.demo.data.Urls.BASE_URL;
import static com.news.demo.ui.AppConstant.NEWS_API_KEY;
import static com.news.newsnotification.NotificationLibraryConstants.NEWS_BUSINESS_ID;
import static com.news.newsnotification.NotificationLibraryConstants.NEWS_TECH_CHRUNCH_ID;
import static com.news.newsnotification.NotificationLibraryConstants.NEWS_WALL_STREEL_JOURNAL_ID;
import static okhttp3.mock.MediaTypes.MEDIATYPE_JSON;

public class NewsRepository {

    private ApiService apiService;

    @Inject
    public NewsRepository(ApiService service) {
        apiService = service;
    }

    public MutableLiveData<NewsResponse> getNewsData(int newsId) {
        final MutableLiveData<NewsResponse> newsLivedata =
                new MutableLiveData<>();

        Call<News> newsCall = RepositoryUtil.generateNewsApiCall(apiService, newsId);

        if (newsCall != null) {
            newsCall.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    if (response.isSuccessful()) {
                        parseResponseSuccess(newsLivedata, response);
                    } else {
                        parseResponseError(newsLivedata);
                    }
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    parseResponseError(newsLivedata);
                }
            });
        }
        return newsLivedata;
    }

    private void parseResponseError(MutableLiveData<NewsResponse> newsLivedata) {
        NewsResponse newsResponse = new NewsResponse();
        newsResponse.setNews(null);
        Error error = new Error();
        //error.setCode(404);
        error.setInfo("Failed to get Techcrunch News..!!");
        ErrorModel errorModel = new ErrorModel();
        errorModel.setError(error);
        newsResponse.setErrorModel(errorModel);
        newsLivedata.postValue(newsResponse);
    }

    private void parseResponseSuccess(MutableLiveData<NewsResponse> newsLivedata,
                                      Response<News> response) {
        NewsResponse newsResponse = new NewsResponse();
        newsResponse.setNews(response.body());
        newsLivedata.postValue(newsResponse);
    }

    public MutableLiveData<ServerConfigDataResponse> getServerConfigData() {
        final MutableLiveData<ServerConfigDataResponse> serverConfigLivedata =
                new MutableLiveData<>();
        try {
            MockInterceptor interceptor =
                    new MockInterceptor(Behavior.UNORDERED);
            interceptor.addRule()
                    .url(BASE_URL + "/server/config")
                    .respond("{\n" +
                            "  \"newsDataSyncTime\": \"3600000\"\n" +
                            "}", MEDIATYPE_JSON).code(200);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            client.newCall(new Request.Builder().url(BASE_URL + "/server/config").build()).
                    enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            e.getCause();
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            Gson g = new Gson();
                            ServerConfigData obj = g.fromJson(response.body().string(), ServerConfigData.class);
                            if (response.isSuccessful()) {
                                ServerConfigDataResponse serverConfigResponse = new ServerConfigDataResponse();
                                serverConfigResponse.setConfigData(obj);
                                serverConfigLivedata.postValue(serverConfigResponse);
                            } else {
                                ServerConfigDataResponse serverConfigResponse = new ServerConfigDataResponse();
                                Error error = new Error();
                                error.setCode(404);
                                error.setInfo("Failed to Sync server config data");
                                ErrorModel errorModel = new ErrorModel();
                                errorModel.setError(error);
                                serverConfigResponse.setErrorModel(errorModel);
                                serverConfigLivedata.postValue(serverConfigResponse);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

      /*  final MutableLiveData<ServerConfigDataResponse> serverConfigLivedata =
                new MutableLiveData<>();

        Call<ServerConfigData> serverConfigDataCall = apiService.getServerConfigData();

        serverConfigDataCall.enqueue(new Callback<ServerConfigData>() {
            @Override
            public void onResponse(Call<ServerConfigData> call, Response<ServerConfigData> response) {

            }

            @Override
            public void onFailure(Call<ServerConfigData> call, Throwable t) {
                t.getCause();
            }
        });*/

        return serverConfigLivedata;
    }


}
