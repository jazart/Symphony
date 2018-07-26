package com.jazart.symphony.venues;

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

import com.bumptech.glide.Glide;
import com.jazart.symphony.R;
import com.jazart.symphony.model.venues.Venue;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays local music and art events for the user to see whats going on in
 * their area. Gets its information fromt he venue viewmodel class
 */
public class LocalEventsFragment extends Fragment {
    @BindView(R.id.recycler_view)
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

        final View view = inflater.inflate(R.layout.my_music_fragment, container, false);

        ButterKnife.bind(this, view);
        mAdapter = new VenueAdapter(Glide.with(this));

        ViewModelProviders.of(this).get(VenueViewModel.class)
                .getVenues().observe(this, new Observer<List<Venue>>() {
            @Override
            public void onChanged(@Nullable List<Venue> venues) {
                mPostLoadProgress.setVisibility(View.GONE);
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
