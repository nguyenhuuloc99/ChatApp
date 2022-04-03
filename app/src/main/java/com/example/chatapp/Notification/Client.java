package com.example.chatapp.Notification;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit retrofit=null;
    public static Retrofit getRetrofit(String url)
    {
        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return retrofit;
    }

}
