package com.jazart.symphony.posts

/*
 * Created by kendrickgholston on 4/15/18.
 */

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.jazart.symphony.R
import com.jazart.symphony.posts.adapters.PostAdapter
import kotlinx.android.synthetic.main.my_music_fragment.view.*

/**
 * Class displays a list of posts from the current user as well as local users in the area
 * Pulls information from the location helper class to display the data
 */

class PostsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private var mPostAdapter: PostAdapter? = null
    private lateinit var mPostsViewModel: PostsViewModel

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPostsViewModel = ViewModelProviders.of(this).get(PostsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = LayoutInflater.from(context).inflate(R.layout.my_music_fragment, container, false)
        setup(v)
        mPostAdapter = PostAdapter(requireContext()) { post, viewId ->
            when (viewId) {
                R.layout.list_item_post -> fragmentManager?.beginTransaction()?.addToBackStack(null)
                        ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        ?.replace(R.id.frag_container, PostDetailFragment.newInstance(post))
                        ?.commit()
                R.id.delete_post_iv -> mPostsViewModel.deletePost(post.id)
            }
        }

        v.recycler_view.adapter = mPostAdapter
        v.recycler_view.layoutManager = LinearLayoutManager(context)

        mPostsViewModel.userPostsLiveData
                .observe(this, Observer { posts ->
                    showProgressBar(true)
                    mPostAdapter?.mPosts = posts
                    mPostAdapter?.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                })
        return v
    }

    private fun setup(v: View) {
        swipeRefreshLayout = v.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        progressBar = v.post_load_progress
    }


    private fun loadPosts() {
        mPostsViewModel.update()
        mPostAdapter?.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun showProgressBar(isLoaded: Boolean) {
        if (isLoaded) {
            view?.post_load_progress?.visibility = View.GONE
            return
        }
        view?.post_load_progress?.visibility = View.VISIBLE
    }

    override fun onRefresh() {
        loadPosts()
    }
}
