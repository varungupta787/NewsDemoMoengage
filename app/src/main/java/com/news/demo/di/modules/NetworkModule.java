package com.news.demo.di.modules;


import com.news.demo.data.Urls;
import com.news.demo.data.network.ApiService;
import com.news.demo.di.scope.ApplicationScope;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mock.Behavior;
import okhttp3.mock.MockInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.news.demo.data.Urls.BASE_URL;
import static okhttp3.mock.ClasspathResources.resource;
import static okhttp3.mock.MediaTypes.MEDIATYPE_JSON;

@Module
public class NetworkModule {

    public final String BASE_URL = Urls.DOMAIN;

    @Provides
    @ApplicationScope
    public ApiService getApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @ApplicationScope
    public Retrofit getRetrofit(GsonConverterFactory factory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder().
                baseUrl(BASE_URL).
                client(okHttpClient).
                addConverterFactory(factory).
                build();
    }

    @Provides
    @ApplicationScope
    public GsonConverterFactory getGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    @ApplicationScope
    public OkHttpClient getOkHttpClient(HttpLoggingInterceptor interceptor) {
        /*MockInterceptor interceptor =
                new MockInterceptor(Behavior.UNORDERED);
        try {
            interceptor.addRule()
                    .pathEnds("/server/config")
                    .respond("{newsDataSyncTime:3600000}", MEDIATYPE_JSON).code(200);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();


            client.newCall(new Request.Builder()
                    .url(BASE_URL + "/server/config")
                    .get()
                    .build())
                    .execute();

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return new OkHttpClient.Builder().
                addNetworkInterceptor(interceptor).
                connectTimeout(120,
                        TimeUnit.SECONDS).
                build();
    }

    @Provides
    @ApplicationScope
    public HttpLoggingInterceptor getInterceptor() {
        HttpLoggingInterceptor interceptor =
                new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    /*@Provides
    @ApplicationScope
    public MockInterceptor getInterceptor() {
        MockInterceptor interceptor =
                new MockInterceptor(Behavior.UNORDERED);
        interceptor.addRule()
                .pathEnds("/server/config")
                .respond("{newsDataSyncTime:3600000}", MEDIATYPE_JSON).code(200);
        return interceptor;
    }*/
}
