package com.jazart.symphony.posts;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jazart.symphony.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
Fragment for creating a new post for the user. Uses a callback to the post activity to
send the resulting post object back to
 */

public class NewPostFragment extends android.support.v4.app.Fragment {
    @BindView(R.id.new_post_title_til)
    TextInputLayout mTitleTil;

    @BindView(R.id.new_post_body_til)
    TextInputLayout mBodyTil;

    @BindView(R.id.new_post_title)
    TextInputEditText mTitle;

    @BindView(R.id.new_post_body)
    TextInputEditText mBody;

    private PostsViewModel mPostsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostsViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.button)
    public void submit() {
        UserPost post = new UserPost.Builder()
                .title(mTitle.getText().toString())
                .body(mBody.getText().toString())
                .build();
        mPostsViewModel.addToDb(post);
    }

}
