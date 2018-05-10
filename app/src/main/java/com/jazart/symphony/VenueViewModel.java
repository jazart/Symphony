package com.jazart.symphony;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;

import com.jazart.symphony.location.LocationHelper;
import com.jazart.symphony.model.venues.Item;
import com.jazart.symphony.model.venues.Venue;
import com.jazart.symphony.model.venues.VenuePicsResponse;
import com.jazart.symphony.model.venues.VenueResponse;
import com.jazart.symphony.repository.FoursquareApiClient;
import com.jazart.symphony.repository.FoursquareConstants;
import com.jazart.symphony.repository.NetworkService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Viewmodel class used for loading information from the foursquare api and
 * wrappinng the data in livedata objects for a more reactive interface.
 */
public class VenueViewModel extends ViewModel implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "VenueViewModel";
    private Retrofit mService;
    private MutableLiveData<Item> mVenuePicLiveData;
    private MutableLiveData<List<Venue>> mVenueLiveData;

    public VenueViewModel() {
        mService = NetworkService.get().getService();
    }

    private void loadImages() {
        if(mVenueLiveData.getValue() != null) {
            for (int i = 0; i < mVenueLiveData.getValue().size(); i++) {
                Venue venue = mVenueLiveData.getValue().get(i);
                loadVenuePics(venue.getId(), i);
            }
        }

    }

    LiveData<List<Venue>> getVenues() {
        loadVenues();
        if(mVenueLiveData == null) {
            mVenueLiveData = new MutableLiveData<>();
        }

        return mVenueLiveData;
    }

    private void loadVenuePics(String venueId, final int index) {
        FoursquareApiClient client = mService.create(FoursquareApiClient.class);
        client.getVenuePics(venueId, getOptions()).enqueue(new Callback<VenuePicsResponse>() {
            @Override
            public void onResponse(Call<VenuePicsResponse> call, Response<VenuePicsResponse> response) {
                if(response.isSuccessful() && response.body().getResponse().getPhotos().getCount() > 0) {
                    VenuePicsResponse picsResponse = response.body();
                    Item item = picsResponse.getResponse().getPhotos().getItems().get(0);
                    Venue venue = mVenueLiveData.getValue().get(index);
                    venue.setImageUri(buildUri(item));

                } else if(response.message() != null && response.errorBody() != null) {
                    try {
                        Log.d(TAG, response.message() + response.errorBody().string());
                        Log.d(TAG, response.raw().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<VenuePicsResponse> call, Throwable t) {
                Log.d(TAG, t.getMessage());

            }
        });

    }

    private void loadVenues() {
        Retrofit client = NetworkService.get().getService();

        FoursquareApiClient apiClient = client.create(FoursquareApiClient.class);
        Map<String, String> options = new HashMap<>();
        options.put("client_id", FoursquareConstants.client_id);
        options.put("client_secret", FoursquareConstants.CLIENT_SECRET);
        options.put("ll", LocationHelper.getInstance().getUserLocation());
        options.put("intent", "checkin");
        options.put("radius", "10000");
        options.put("limit", "10");
        options.put("categoryId", "4bf58dd8d48988d1ac941735,4bf58dd8d48988d1e5931735");
        options.put("v", "20180502");
        apiClient.getVenues(options).enqueue(new Callback<VenueResponse>() {
            @Override
            public void onResponse(Call<VenueResponse> call, Response<VenueResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mVenueLiveData.setValue(response.body().getResponse().getVenues());
                    loadImages();

                }
            }

            @Override
            public void onFailure(Call<VenueResponse> call, Throwable t) {
            }
        });
    }

    private Uri buildUri(Item photo) {
        CharSequence uri = TextUtils.concat(photo.getPrefix(), ""
                + photo.getWidth()
                +"" +
                photo.getHeight(), photo.getSuffix());
        return Uri.parse(uri.toString());
    }

    private Map<String, String> getOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("client_id", FoursquareConstants.client_id);
        options.put("client_secret", FoursquareConstants.CLIENT_SECRET);
        options.put("limit", "1");
        options.put("v", "20180502");
        return options;
    }

    @Override
    public void onRefresh() {

    }
}
