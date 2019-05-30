package com.jazart.symphony.profile

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.jazart.symphony.R
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.Post
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_friend.view.*
import kotlinx.android.synthetic.main.list_item_post.view.*

class UserAdapter (context: Context,
                   private val glide: RequestManager,
                   private val clickListener: (User, Int) -> Unit) : RecyclerView.Adapter<UserAdapter.UserHolder>() {
    var users: List<User>? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val v = inflater.inflate(R.layout.list_friend, parent, false)
        return UserHolder(v)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        users?.get(position)?.let { user->
            holder.bind(user)
        }
    }

    override fun getItemCount(): Int {
        return users?.size ?: 0
    }

    inner class UserHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        private lateinit var user: User
        private val clickHandler = View.OnClickListener { view ->
            clickListener(user, view.id)
        }

        fun bind(user: User) {
            this.user = user
            itemView.friend_name.text = user.name
            itemView.post_profile_pic.setOnClickListener(clickHandler)
            glide.load(Uri.parse("google.com"))
                    .apply(RequestOptions()
                            .placeholder(containerView.context.resources.getDrawable(R.drawable.ic_account_circle_black_24dp, null))
                            .circleCrop())
                    .into(itemView.post_profile_pic)
                    .onLoadFailed(containerView.context.resources.getDrawable(R.drawable.ic_account_circle_black_24dp, null))
        }
        }
    }