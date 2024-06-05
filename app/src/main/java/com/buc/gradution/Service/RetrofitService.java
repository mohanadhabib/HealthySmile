package com.buc.gradution.Service;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RetrofitService {
    private static Retrofit retrofit;
    private static OkHttpClient getOkHttpClient(){
        return new OkHttpClient()
                .newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1,TimeUnit.MINUTES)
                .writeTimeout(1,TimeUnit.MINUTES)
                .build();
    }
    public static Retrofit getRetrofit(String baseUrl){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(RetrofitService.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
