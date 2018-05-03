package com.jazart.symphony;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jazart.symphony.venues.FoursquareApiClient;
import com.jazart.symphony.venues.FoursquareConstants;
import com.jazart.symphony.venues.NetworkService;
import com.jazart.symphony.venues.VenueResponse;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LocalEventsFragment extends Fragment {
    @BindView(R.id.my_songs)
    RecyclerView mRecyclerView;
    @BindView(R.id.post_load_progress)
    ProgressBar mPostLoadProgress;


    private VenueAdapter mAdapter;

    public LocalEventsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_music_fragment, container, false);

        ButterKnife.bind(this, view);
        Retrofit client = NetworkService.get().getService();

        FoursquareApiClient apiClient = client.create(FoursquareApiClient.class);
        Map<String, String> options = new HashMap<>();
        options.put("client_id", FoursquareConstants.client_id);
        options.put("client_secret", FoursquareConstants.CLIENT_SECRET);
        options.put("ll", "32.5,-84.9");
        options.put("intent", "checkin");
        options.put("radius", "10000");
        options.put("limit", "10");
        options.put("categoryId", "4bf58dd8d48988d1ac941735,4bf58dd8d48988d1e5931735");
        options.put("v", "20180502");
        apiClient.getVenues(options).enqueue(new Callback<VenueResponse>() {
            @Override
            public void onResponse(Call<VenueResponse> call, Response<VenueResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mPostLoadProgress.setVisibility(View.GONE);
                    mAdapter = new VenueAdapter();
                    mAdapter.setVenueList(response.body().getResponse().getVenues());
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Call<VenueResponse> call, Throwable t) {
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
