package com.jazart.symphony.posts.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jazart.symphony.R;
import com.jazart.symphony.posts.Comment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private List<Comment> mComments;
    private final LayoutInflater mInflater;

    public CommentAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_comment, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.bind(mComments.get(position));
    }


    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_comment_body)
        TextView mCommentTv;
        @BindView(R.id.list_item_commet_profile_pic)
        ImageView mProfilePic;

        CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Comment comment) {
            mCommentTv.setText(comment.getContent());
            Glide.with(itemView)
                    .load(comment.getProfilePic())
                    .apply(new RequestOptions().centerCrop())
                    .into(mProfilePic);
        }
    }
}
