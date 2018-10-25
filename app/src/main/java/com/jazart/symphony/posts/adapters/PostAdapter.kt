package com.jazart.symphony.posts.adapters

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jazart.symphony.R
import com.jazart.symphony.posts.UserPost
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_post.view.*

class PostAdapter(context: Context,
                  private val clickListener: (UserPost, Int) -> Unit) : RecyclerView.Adapter<PostAdapter.PostHolder>() {
    var mPosts: List<UserPost>? = null
    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val v = mInflater.inflate(R.layout.list_item_post, parent, false)
        return PostHolder(v)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        mPosts?.get(position)?.let { post ->
            holder.bind(post)
        }
    }

    override fun getItemCount(): Int {
        return mPosts?.size ?: 0
    }


    inner class PostHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        private lateinit var post: UserPost
        private val clickHandler = View.OnClickListener { view ->
            clickListener(post, view.id)
        }

        fun bind(post: UserPost) {
            this.post = post
            itemView.post_title.text = post.title
            itemView.post_body.text = post.body
            itemView.setOnClickListener(clickHandler)
            itemView.delete_post_iv.setOnClickListener(clickHandler)
            if (post.profilePic != null) {
                Glide.with(itemView.context)
                        .load(Uri.parse(post.profilePic))
                        .apply(RequestOptions()
                                .circleCrop())
                        .into(itemView.post_profile_pic)
            }
        }

    }
}
