package com.jazart.symphony.featured;

import android.arch.lifecycle.ViewModelProviders;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jazart.symphony.BaseFragment;
import com.jazart.symphony.MusicAdapter;
import com.jazart.symphony.R;
import com.jazart.symphony.model.Song;
import com.jazart.symphony.posts.SongViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeaturedMusicFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private MusicAdapter mMusicAdapter;
    private SongViewModel mSongsViewModel;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefreshSongs;
    @BindView(R.id.music_load_progress)
    ProgressBar mSongLoading;
    @BindView(R.id.featured_songs)
    RecyclerView mRecyclerView;


    public FeaturedMusicFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongsViewModel = ViewModelProviders.of(this).get(SongViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.feature_music_fragment, container, false);
        ButterKnife.bind(this, v);
        mRefreshSongs.setOnRefreshListener(this);
        return v;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSongs();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadSongs() {
        mSongsViewModel.getUserSongs().addOnCompleteListener(new OnCompleteListener<List<Song>>() {
            @Override
            public void onComplete(@NonNull Task<List<Song>> task) {
                mMusicAdapter = new MusicAdapter(getContext());
                showProgressBar(true);
                mMusicAdapter.setSongs(task.getResult());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mMusicAdapter);
                mRefreshSongs.setRefreshing(false);
            }
        });
    }

    private void showProgressBar(boolean isLoaded) {
        if (isLoaded) {
            mSongLoading.setVisibility(View.GONE);
            return;
        }
        mSongLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        loadSongs();
    }
}

