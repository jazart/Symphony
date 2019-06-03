package com.jazart.symphony.posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.jazart.symphony.R;
import com.jazart.symphony.di.AppKt;
import com.jazart.symphony.di.SimpleViewModelFactory;
import com.jazart.symphony.posts.adapters.CommentAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entities.Post;

//import com.jazart.symphony.posts.adapters.CommentAdapter;
/*
This class loads a detail view of a post with the post title and a list of comments.
Viewmodel used to pull and push data to the PostViewmodel class
 */

public class PostDetailFragment extends Fragment {

    private static final String ARG_POST = "com.jazart.symphony.userPost";

    private PostsViewModel mViewModel;

    @Inject
    SimpleViewModelFactory mFactory;

    @BindView(R.id.post_detail_image)
    ImageView mPostDetailImage;

    @BindView(R.id.post_body_tv)
    TextView mPostBodyTv;

    @BindView(R.id.comments_recyclerview)
    RecyclerView mCommentsRecyclerview;

    @BindView(R.id.post_detail_title)
    TextView mPostTitle;

    @BindView(R.id.post_detail_edit_btn)
    ImageButton mPostDetailEditBtn;

    @BindView(R.id.post_detail_comment_btn)
    ImageButton mPostDetailCommentBtn;

    private CommentAdapter mCommentAdapter;

    @BindView(R.id.comment_et)
    TextInputEditText mCommentEt;

    @BindView(R.id.comment_til)
    TextInputLayout mCommentTil;

    @BindView(R.id.comment_send_btn)
    ImageButton mCommentSendBtn;

    private boolean isCommenting;
    private Post mPost;
    private final String POST_ID = "post id";

    public PostDetailFragment() {

    }

    public static PostDetailFragment newInstance(Post post) {
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
        AppKt.app(this).component.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mFactory).get(PostsViewModel.class);
        mPost = savedInstanceState != null ? (Post) savedInstanceState.getSerializable(POST_ID)
                : PostDetailFragmentArgs.fromBundle(getArguments()).getPost();
        buildUi(mPost);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(POST_ID, mPost);
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.post_detail_edit_btn, R.id.post_detail_comment_btn, R.id.comment_send_btn})
    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.post_detail_comment_btn:
                isCommenting = !isCommenting;
                showCommentViews(isCommenting);
                break;
            case R.id.post_detail_edit_btn:
                break;
            case R.id.comment_send_btn:
        }
    }

    private void showCommentViews(boolean isEditing) {
        if (isEditing) {
            mCommentTil.setVisibility(View.VISIBLE);
            mCommentSendBtn.setVisibility(View.VISIBLE);
            mPostDetailCommentBtn.setVisibility(View.GONE);
            mPostDetailEditBtn.setVisibility(View.GONE);
        } else {
            mCommentSendBtn.setVisibility(View.GONE);
            mCommentTil.setVisibility(View.GONE);
            mPostDetailCommentBtn.setVisibility(View.VISIBLE);
            mPostDetailEditBtn.setVisibility(View.VISIBLE);
        }
    }

    private void buildUi(Post post) {
        if (mPost.getId() != null) mViewModel.loadComments(mPost.getId());
        mViewModel.getComments().observe(this, comments -> {
            mCommentAdapter.setComments(comments);
            mCommentAdapter.notifyDataSetChanged();
            mCommentsRecyclerview.setAdapter(mCommentAdapter);
        });
        mCommentAdapter = new CommentAdapter(getContext());
        mPostBodyTv.setText(post.getBody());
        mPostTitle.setText(post.getTitle());
        mCommentsRecyclerview.setNestedScrollingEnabled(false);
        mCommentsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
