package com.jazart.symphony.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService sINSTANCE;
    private Retrofit mService;
    private OkHttpClient mClient;

    private NetworkService() {
        mService = buildClient();
    }

    public static NetworkService get() {
        if (sINSTANCE == null) {
            return new NetworkService();
        }

        return sINSTANCE;
    }

    public Retrofit getService() {
        return mService;
    }

    private Retrofit buildClient() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return new Retrofit.Builder()
                .baseUrl(FoursquareConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
