package com.jazart.symphony.posts.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jazart.symphony.R;
import com.jazart.symphony.posts.PostDetailFragment;
import com.jazart.symphony.posts.UserPost;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private List<UserPost> mPosts;
    private LayoutInflater mInflater;

    public PostAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.list_item_post, parent, false);
        return new PostHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.bind(mPosts.get(position));
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void setPosts(List<UserPost> posts) {
        mPosts = posts;
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.post_title)
        TextView mTitleTv;

        @BindView(R.id.post_author)
        TextView mAuthorTv;

        @BindView(R.id.post_body)
        TextView mPostBodyTv;

        @BindView(R.id.post_profile_pic)
        ImageView mProfilePic;


        PostHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


        //binds view widgets with post data
        void bind(final UserPost post) {
            mTitleTv.setText(post.getTitle());
            mPostBodyTv.setText(post.getBody());
            if (post.getProfilePic() != null) {
                Glide.with(mInflater.getContext())
                        .load(Uri.parse(post.getProfilePic()))
                        .apply(new RequestOptions()
                                .circleCrop())
                        .into(mProfilePic);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = ((AppCompatActivity) mInflater.getContext())
                            .getSupportFragmentManager();
                    fm.beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.frag_container, PostDetailFragment.newInstance(post))
                            .commit();

                }
            });
        }
    }
}
