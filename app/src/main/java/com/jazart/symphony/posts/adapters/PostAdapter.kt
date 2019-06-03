package com.jazart.symphony.posts.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.jazart.symphony.R
import entities.Post
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_post.view.*

class PostAdapter(context: Context,
                  private val glide: RequestManager,
                  private val clickListener: (Post, Int) -> Unit) : RecyclerView.Adapter<PostAdapter.PostHolder>() {
    var posts: List<Post>? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val v = inflater.inflate(R.layout.list_item_post, parent, false)
        return PostHolder(v)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        posts?.get(position)?.let { post ->
            holder.bind(post)
        }
    }

    override fun getItemCount(): Int {
        return posts?.size ?: 0
    }


    inner class PostHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        private lateinit var post: Post
        private val clickHandler = View.OnClickListener { view ->
            clickListener(post, view.id)
        }

        fun bind(post: Post) {
            this.post = post
            itemView.post_title.text = post.title
            itemView.post_body.text = post.body
            itemView.setOnClickListener(clickHandler)
            itemView.delete_post_iv.setOnClickListener(clickHandler)
            itemView.post_profile_pic.setOnClickListener(clickHandler)
            glide.load(post.profilePic)
                    .apply(RequestOptions()
                            .placeholder(containerView.context.resources.getDrawable(R.drawable.ic_account_circle_black_24dp, null))
                            .circleCrop())
                    .into(itemView.post_profile_pic)
                    .onLoadFailed(containerView.context.resources.getDrawable(R.drawable.ic_account_circle_black_24dp, null))
        }
    }
}
