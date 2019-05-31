package com.jazart.symphony.posts;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jazart.symphony.R;
import com.jazart.symphony.Result;
import com.jazart.symphony.Status;

import java.util.Objects;

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
    private Post post;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPostsViewModel.getAddPostResult().observe(getViewLifecycleOwner(), result -> {
            if (getView() == null) return;
            Boolean isSuccessful = result.consume();
            if (isSuccessful != null && isSuccessful) {
                NavHostFragment.findNavController(this).navigate(NewPostFragmentDirections.actionNewPostFragmentToPostDetailFragment(post));
                Snackbar.make(getView(), "Post added!", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(getView(), "Unable to add your post. Please try again.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.button)
    public void submit() {
        post = new Post.Builder()
                .title(Objects.requireNonNull(mTitle.getText()).toString())
                .body(Objects.requireNonNull(mBody.getText()).toString())
                .build();
        mPostsViewModel.addToDb(post);
    }

}
