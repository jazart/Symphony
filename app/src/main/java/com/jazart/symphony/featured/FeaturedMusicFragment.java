package com.jazart.symphony.featured;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jazart.symphony.BaseFragment;
import com.jazart.symphony.MusicAdapter;
import com.jazart.symphony.R;

import butterknife.BindView;

public class FeaturedMusicFragment extends BaseFragment {
    private MusicAdapter mMusicAdapter;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefreshPosts;
    @BindView(R.id.post_load_progress)
    ProgressBar mPostLoading;
    @BindView(R.id.my_songs)
    RecyclerView mRecyclerView;


    public FeaturedMusicFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.feature_music_fragment, container, false);
         mRecyclerView = v.findViewById(R.id.featured_songs);
        mRecyclerView.setAdapter(mMusicAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
