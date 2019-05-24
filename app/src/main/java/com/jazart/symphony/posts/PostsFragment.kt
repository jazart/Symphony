package com.jazart.symphony.posts

/*
 * Created by kendrickgholston on 4/15/18.
 */

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jazart.symphony.R
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.posts.adapters.PostAdapter
import kotlinx.android.synthetic.main.fragment_posts.*

/**
 * Class displays a list of posts from the current user as well as local users in the area
 * Pulls information from the location helper class to display the data
 */

class PostsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var postAdapter: PostAdapter? = null
    private val postsViewModel: PostsViewModel by viewModels { SimpleViewModelFactory { PostsViewModel() } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postAdapter = PostAdapter(requireContext()) { post, viewId ->
            when (viewId) {
                R.id.post_card ->
                    findNavController().navigate(R.id.postDetailFragment)
                R.id.delete_post_iv -> postsViewModel.deletePost(post.id)
            }
        }

        recycler_view.adapter = postAdapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        swipeRefreshLayout.setOnRefreshListener(this)
        postsViewModel.userPostsLiveData
                .observe(viewLifecycleOwner, Observer { posts ->
                    showProgressBar(true)
                    postAdapter?.posts = posts
                    postAdapter?.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                })
    }

    override fun onRefresh() {
        loadPosts()
    }

    private fun loadPosts() {
        postsViewModel.load()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun showProgressBar(isLoaded: Boolean) {
        if (isLoaded) {
            post_load_progress.visibility = View.GONE
            return
        }
        post_load_progress.visibility = View.VISIBLE
    }
}