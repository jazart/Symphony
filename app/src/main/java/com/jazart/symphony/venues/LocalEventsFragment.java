package com.jazart.symphony.venues;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.jazart.symphony.R;
import com.jazart.symphony.di.App;
import com.jazart.symphony.network.FoursquareRepo;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;

/**
 * Displays local music and art events for the user to see whats going on in
 * their area. Gets its information from he venue viewmodel class
 */
public class LocalEventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.post_load_progress)
    ProgressBar mPostLoadProgress;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    @Inject
    FoursquareRepo mRepo;

    public LocalEventsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.my_music_fragment, container, false);
        Retrofit retrofit = mRepo.getService();
        ButterKnife.bind(this, view);
        VenueAdapter adapter = new VenueAdapter(Glide.with(requireContext()));
//        ViewModelProviders.of(this).get(VenueViewModel.class)
//                .getVenues().observe(this, new Observer<List<Venue>>() {
//            @Override
//            public void onChanged(@Nullable List<Venue> venues) {
//                mPostLoadProgress.setVisibility(View.GONE);
//                mAdapter.setVenueList(venues);
//                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                mRecyclerView.setAdapter(mAdapter);
//            }
//        });
        return view;
    }


    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(false);
    }

    private void inject() {
        App app = (App) requireActivity().getApplication();
        app.getComponent().inject(this);
    }
}
