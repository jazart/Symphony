package com.jazart.symphony.featured;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jazart.symphony.R;
import com.jazart.symphony.di.App;
import com.jazart.symphony.di.SimpleViewModelFactory;
import com.jazart.symphony.model.Song;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FeaturedMusicFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private MusicAdapter mMusicAdapter;
    private SongViewModel mSongsViewModel;
    @Inject
    SimpleViewModelFactory mViewModelFactory;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.feature_music_fragment, container, false);
        ButterKnife.bind(this, v);
        inject();

        mSongsViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(SongViewModel.class);
        mRefreshSongs.setOnRefreshListener(this);
        setupRecyclerView();

        Objects.requireNonNull(mSongsViewModel.getSongs()).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable List<Song> songs) {
                hideProgressBar();
                mMusicAdapter.setSongs(songs);
                mMusicAdapter.notifyDataSetChanged();
                mRefreshSongs.setRefreshing(false);
            }
        });
        return v;
    }

    @Override
    public void onRefresh() {
        loadSongs();
    }

    private void inject() {
        App app = (App) requireActivity().getApplication();
        app.component.inject(this);
    }

    private void setupRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        final RecyclerView.ItemDecoration decoration = new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL);
        mMusicAdapter = new MusicAdapter(getContext());
        mRecyclerView.setAdapter(mMusicAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(decoration);
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
        mRefreshSongs.setRefreshing(false);
        mSongsViewModel.update();
    }

    private void hideProgressBar() {
        mSongLoading.setVisibility(View.GONE);
    }

}

