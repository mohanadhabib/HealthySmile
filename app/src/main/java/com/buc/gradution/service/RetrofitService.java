package com.buc.gradution.service;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RetrofitService {
    private static Retrofit retrofitForScan;
    private static Retrofit retrofitForChatBot;
    private static OkHttpClient getOkHttpClient(){
        return new OkHttpClient()
                .newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1,TimeUnit.MINUTES)
                .writeTimeout(1,TimeUnit.MINUTES)
                .build();
    }
    public static Retrofit getRetrofit(String baseUrl){
        if(retrofitForScan == null){
            retrofitForScan = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitForScan;
    }
    public static Retrofit getLongTimeOutRetrofit(String baseUrl){
        if(retrofitForChatBot == null){
            retrofitForChatBot = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(RetrofitService.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitForChatBot;
    }
}
