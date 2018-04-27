package com.jazart.symphony.posts;


import android.content.Context;
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

public class NewPostFragment extends android.support.v4.app.Fragment {
    @BindView(R.id.new_post_title_til)
    TextInputLayout mTitleTil;

    @BindView(R.id.new_post_body_til)
    TextInputLayout mBodyTil;

    @BindView(R.id.new_post_title)
    TextInputEditText mTitle;

    @BindView(R.id.new_post_body)
    TextInputEditText mBody;

    private Post mPost;

    @OnClick(R.id.button)
    public void submit() {
        UserPost post = new UserPost.Builder()
                .title(mTitle.getText().toString())
                .body(mBody.getText().toString())
                .build();
        mPost.onUserPost(post);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mPost = (Post) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        getActivity().setTheme(R.style.Theme_AppCompat_NoActionBar);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null) {
//            getActivity().setTheme(R.style.AppTheme);
        }
    }


    public interface Post {
        void onUserPost(@NonNull UserPost post);
    }
}
