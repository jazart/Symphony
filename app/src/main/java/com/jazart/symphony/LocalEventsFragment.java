package com.jazart.symphony;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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

import com.jazart.symphony.model.venues.Venue;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalEventsFragment extends Fragment {
    @BindView(R.id.my_songs)
    RecyclerView mRecyclerView;
    @BindView(R.id.post_load_progress)
    ProgressBar mPostLoadProgress;



    private VenueAdapter mAdapter;
    private VenueViewModel mVenueViewModel;

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

        ViewModelProviders.of(this).get(VenueViewModel.class)
                .getVenues().observe(this, new Observer<List<Venue>>() {
                    @Override
                    public void onChanged(@Nullable List<Venue> venues) {
                        mPostLoadProgress.setVisibility(View.GONE);
                        mAdapter = new VenueAdapter();
                        mAdapter.setVenueList(venues);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        mRecyclerView.setAdapter(mAdapter);
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
