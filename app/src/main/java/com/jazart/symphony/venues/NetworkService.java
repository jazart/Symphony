package com.jazart.symphony.venues;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService INSTANCE;
    private Retrofit mService;
    private OkHttpClient mClient;

    private NetworkService() {
        mService = buildClient();
    }

    public static NetworkService get() {
        if (INSTANCE == null) {
            return new NetworkService();
        }

        return INSTANCE;
    }

    public Retrofit getService() {
        return mService;
    }

    private Retrofit buildClient() {
        return new Retrofit.Builder()
                .baseUrl(FoursquareConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
