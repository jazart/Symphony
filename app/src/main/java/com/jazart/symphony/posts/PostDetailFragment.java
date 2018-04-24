package com.jazart.symphony.posts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jazart.symphony.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PostDetailFragment extends Fragment {

    public static final String ARG_POST = "com.jazart.symphony.userPost";
    @BindView(R.id.post_detail_image)
    ImageView mPostDetailImage;
    @BindView(R.id.post_body_tv)
    TextView mPostBodyTv;
    @BindView(R.id.comments_recyclerview)
    RecyclerView mCommentsRecyclerview;

    public PostDetailFragment() {

    }

    public static PostDetailFragment newInstance(UserPost post) {
        PostDetailFragment detailFragment = new PostDetailFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String postJson = gson.toJson(post);
        bundle.putString(ARG_POST, postJson);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            UserPost post = gson.fromJson(getArguments().getString(ARG_POST),
                    UserPost.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
