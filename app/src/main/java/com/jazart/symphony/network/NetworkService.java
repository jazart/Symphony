package com.jazart.symphony.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jazart.symphony.di.App;

import javax.inject.Singleton;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class NetworkService {
    private static NetworkService sINSTANCE;
    private final Retrofit mService;
    private OkHttpClient mClient;

    private NetworkService() {
        mService = buildClient();
    }

    public static NetworkService get() {
        if (sINSTANCE == null) {
            sINSTANCE = new NetworkService();
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
                .client(mClient)
                .build();
    }

    private void buildCacheClient(App app) {
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(app.getCacheDir(), cacheSize);
        mClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }
}
