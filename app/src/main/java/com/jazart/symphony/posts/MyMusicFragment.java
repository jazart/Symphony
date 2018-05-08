package com.jazart.symphony.posts;

/*
 * Created by kendrickgholston on 4/15/18.
 */

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jazart.symphony.R;
import com.jazart.symphony.location.LocationHelper;
import com.jazart.symphony.posts.adapters.PostAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyMusicFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private PostAdapter mPostAdapter;
    private PostsViewModel mPostsViewModel;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefreshPosts;
    @BindView(R.id.post_load_progress)
    ProgressBar mPostLoading;
    @BindView(R.id.my_songs)
    RecyclerView mRecyclerView;

    private LiveData<List<UserPost>> mNearby;
    public MyMusicFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostsViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);
        mNearby = LocationHelper.getInstance().getNearbyPosts();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.my_music_fragment, container, false);
        ButterKnife.bind(this, v);
        mRefreshPosts.setOnRefreshListener(this);
        mNearby.observe(this, new Observer<List<UserPost>>() {
            @Override
            public void onChanged(@Nullable List<UserPost> posts) {
                mPostAdapter = new PostAdapter(getActivity());
                showProgressBar(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mPostAdapter);
                mPostAdapter.setPosts(posts);
                mRefreshPosts.setRefreshing(false);
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void loadPosts() {
//        mPostsViewModel.getUserPosts().addOnCompleteListener(new OnCompleteListener<List<UserPost>>() {
//            @Override
//            public void onComplete(@NonNull Task<List<UserPost>> task) {
////                mPostAdapter = new PostAdapter(getActivity());
////                showProgressBar(true);
////                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
////                mRecyclerView.setAdapter(mPostAdapter);
////                mPostAdapter.setPosts(task.getResult());
////                mRefreshPosts.setRefreshing(false);
//
//            }
//        });
    }

    private void showProgressBar(boolean isLoaded) {
        if (isLoaded) {
            mPostLoading.setVisibility(View.GONE);
            return;
        }
        mPostLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        loadPosts();
    }
}
