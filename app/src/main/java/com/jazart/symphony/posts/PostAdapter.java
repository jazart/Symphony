package com.jazart.symphony.posts;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private List<UserPost> mPosts;


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void setPosts(List<UserPost> posts) {
        mPosts = posts;
    }
    public class PostHolder extends RecyclerView.ViewHolder {

        public PostHolder(View itemView) {
            super(itemView);
        }

        public void bind(UserPost post) {

        }
    }
}
