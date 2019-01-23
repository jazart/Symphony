package com.jazart.symphony.posts;


import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jazart.symphony.R;

import java.util.Objects;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NewPostFragment extends Fragment {
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
        Post post = new Post.Builder()
                .title(Objects.requireNonNull(mTitle.getText()).toString())
                .body(Objects.requireNonNull(mBody.getText()).toString())
                .build();
        mPostsViewModel.addToDb(post);
    }

}
