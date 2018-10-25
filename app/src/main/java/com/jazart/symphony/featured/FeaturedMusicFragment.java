package com.jazart.symphony.featured;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jazart.symphony.MainActivity;
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
    @BindView(R.id.featured_songs_toolbar)
    androidx.appcompat.widget.Toolbar mSongsToolBar;

    public FeaturedMusicFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.feature_music_fragment, container, false);
        ButterKnife.bind(this, v);
        inject();
        mRecyclerView.setNestedScrollingEnabled(true);

        showHideFabMenu();
        setHasOptionsMenu(true);
        mSongsToolBar.inflateMenu(R.menu.featured_music_menu);

        mSongsViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(SongViewModel.class);

        LiveData<List<Song>> mSongsLiveData = mSongsViewModel.getSongs();
        loadSongs(mSongsLiveData);

        mRefreshSongs.setOnRefreshListener(this);
        reloadSongs();

        setupSwipeListener(mSongsLiveData);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.featured_music_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupSwipeListener(final LiveData<List<Song>> mSongsLiveData) {
        ItemTouchHelper swipeController = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int pos = viewHolder.getAdapterPosition();
                Song song = Objects.requireNonNull(mSongsLiveData.getValue()).get(pos);
                mSongsViewModel.removeSongFromStorage(song);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                ColorDrawable bg = new ColorDrawable(Color.RED);
                Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_white_32dp);
                View v = viewHolder.itemView;
                int backgroundCornerOffset = 5;
                int iconMargin = (v.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = v.getTop() + (v.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();
                int iconLeft = 0;
                int iconRight = 0;
                if(dX > 0) {
                    bg.setBounds(0, v.getTop(),
                            v.getLeft() + ((int) dX) + backgroundCornerOffset,
                            v.getBottom());
                    iconLeft = iconMargin;
                    iconRight = iconMargin + icon.getIntrinsicWidth();
                } else {
                    bg.setBounds(0, 0, 0, 0);
                }
                bg.draw(c);
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                icon.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
        swipeController.attachToRecyclerView(mRecyclerView);
    }

    private void loadSongs(LiveData<List<Song>> mSongsLiveData) {
        mSongsLiveData.observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable List<Song> songs) {
                mMusicAdapter = new MusicAdapter(getContext());
                hideProgress();
                mMusicAdapter.setSongs(songs);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mMusicAdapter);
                mRefreshSongs.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        mSongsViewModel.refreshContent();
    }

    private void reloadSongs() {
        mRefreshSongs.setRefreshing(false);
        mSongsViewModel.update();
    }

    private void hideProgress() {
        mSongLoading.setVisibility(View.GONE);
    }

    private void showHideFabMenu() {
        final MainActivity activity = (MainActivity) requireActivity();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy < 0 && !activity.mFabMenu.isShown()) activity.mFabMenu.showMenu(true);
                else if(dy > 0 && activity.mFabMenu.isShown()) activity.mFabMenu.hideMenu(true);
            }
        });
    }

    private void inject() {
        App app = (App) requireActivity().getApplication();
        app.component.inject(this);
    }

}