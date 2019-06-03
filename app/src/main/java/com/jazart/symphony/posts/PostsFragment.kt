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
import com.bumptech.glide.Glide
import com.jazart.symphony.R
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.di.app
import com.jazart.symphony.posts.adapters.PostAdapter
import com.jazart.symphony.profile.ProfileFragmentDirections
import entities.Post
import kotlinx.android.synthetic.main.fragment_posts.*
import javax.inject.Inject

/**
 * Class displays a list of posts from the current user as well as local users in the area
 * Pulls information from the location helper class to display the data
 */

class PostsFragment : Fragment() {

    @Inject lateinit var factory: SimpleViewModelFactory
    private var postAdapter: PostAdapter? = null
    private val postsViewModel: PostsViewModel by viewModels { factory }
    private var pageType: PostPage = PostPage.PUBLIC

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        app().component.inject(this)
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postAdapter = PostAdapter(requireContext(), Glide.with(this)) { post, viewId ->
            when (viewId) {
                R.id.post_card ->
                    if(pageType == PostPage.PUBLIC) {
                        findNavController().navigate(PostsFragmentDirections.actionPostsFragmentToPostDetailFragment(post))
                    } else {
                        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPostDetailFragment(post))
                    }
                R.id.delete_post_iv -> postsViewModel.deletePost(post.id)

                R.id.post_profile_pic -> {
                    findNavController().navigate(PostsFragmentDirections.actionPostsFragmentToProfileFragment(post.id))
                }
            }
        }
        setupUi()
    }

    private fun setupUi() {
        postsViewModel.load()
        recycler_view.adapter = postAdapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        swipeRefreshLayout.setOnRefreshListener { loadPosts() }
        pageType = arguments?.getSerializable(PAGE_TYPE) as PostPage? ?: pageType
        if (pageType == PostPage.PUBLIC) {
            postsViewModel.nearbyPostsLiveData
                    .observe(viewLifecycleOwner, Observer { posts ->
                        displayPosts(posts)
                    })
        } else {
            postsViewModel.userPostsLiveData
                    .observe(viewLifecycleOwner, Observer { posts ->
                        displayPosts(posts)
                    })
        }
    }

    private fun displayPosts(posts: List<Post>?) {
        showProgressBar(true)
        postAdapter?.posts = posts
        postAdapter?.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
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

    companion object {
        const val PAGE_TYPE = "page type"
        fun newInstance(pageType: PostPage): Fragment = PostsFragment().apply {
            arguments = Bundle().apply { putSerializable(PAGE_TYPE, pageType) }
        }
    }

}

enum class PostPage {
    PUBLIC,
    PRIVATE
}