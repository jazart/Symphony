package com.jazart.symphony.posts;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jazart.symphony.R;
import com.jazart.symphony.posts.adapters.CommentAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.jazart.symphony.posts.adapters.CommentAdapter;
/*
This class loads a detail view of a post with the post title and a list of comments.
Viewmodel used to pull and push data to the PostViewmodel class
 */


public class PostDetailFragment extends Fragment {

    private static final String ARG_POST = "com.jazart.symphony.userPost";


    private PostsViewModel mViewModel;
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
        mViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        ButterKnife.bind(this, view);


        if (getArguments() != null) {
            Gson gson = new Gson();
            UserPost post = gson.fromJson(getArguments().getString(ARG_POST),
                    UserPost.class);

            mViewModel.loadComments(post.getId());
            mViewModel.getComments().observe(this, new Observer<List<Comment>>() {
                @Override
                public void onChanged(@Nullable List<Comment> comments) {
                    mCommentAdapter.setComments(comments);
                    mCommentAdapter.notifyDataSetChanged();
                    mCommentsRecyclerview.setAdapter(mCommentAdapter);
                }
            });
            mCommentAdapter = new CommentAdapter(getContext());
            buildUi(post);
        }

        return view;
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
//                if(mCommentTil.getEditText().getText() == null) {
//                    // error toast
//                    break;
//                }
//                Comment comment = new Comment();
//                comment.setContent(mCommentEt.getText().toString());
//                comment.setAuthorName(mPost.getAuthorName());
//                comment.profilePic(Uri.parse(mPost.getProfilePic()));
//                mViewModel.addComment(comment, "33");
                //add comment to post detail vm;
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
    private void buildUi(UserPost post) {
        mPostBodyTv.setText(post.getBody());
        mPostTitle.setText(post.getTitle());


        mCommentsRecyclerview.setNestedScrollingEnabled(false);
        mCommentsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
