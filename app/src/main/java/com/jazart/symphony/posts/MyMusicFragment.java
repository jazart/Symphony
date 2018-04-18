package com.jazart.symphony.posts;

/*
 * Created by kendrickgholston on 4/15/18.
 */

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

import com.jazart.symphony.R;

import java.util.List;

public class MyMusicFragment extends Fragment {
    private PostAdapter mPostAdapter;
    private PostsViewModel mPostsViewModel;
    private List<UserPost> mPosts;



    public MyMusicFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostsViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);
        mPosts = mPostsViewModel.getUserPosts();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.my_music_fragment, container, false);
        mPostAdapter.setPosts(mPosts);
        RecyclerView recyclerView = v.findViewById(R.id.my_songs);
        recyclerView.setAdapter(mPostAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
