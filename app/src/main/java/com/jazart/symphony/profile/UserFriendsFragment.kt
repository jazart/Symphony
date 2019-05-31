package com.jazart.symphony.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jazart.symphony.R
import com.jazart.symphony.featured.MusicAdapter
import com.jazart.symphony.model.Song
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.PostPage
import com.jazart.symphony.posts.PostsFragmentDirections
import com.jazart.symphony.posts.adapters.PostAdapter
import kotlinx.android.synthetic.main.fragment_posts.*

class UserFriendsFragment :Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newU = User()
        newU.id = "random nonsense"
        newU.name = "Jimmy Neutron"
        val newU2 = User()
        newU.id = "random nonsense"
        newU.name = "Timmy Turner"
        val userAdapter = UserAdapter(requireContext(), Glide.with(this)) { user, viewId ->
            when (viewId) {

                R.id.post_profile_pic -> {
                    findNavController().navigate(PostsFragmentDirections.actionPostsFragmentToProfileFragment(user.id))
                }
            }
        }
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        swipeRefreshLayout.setOnRefreshListener { refreshSongs() }
        viewModel.loadUserSongs()
        viewModel.userSongs.observe(viewLifecycleOwner, Observer { songs ->
            adapter.submitList(songs)
            post_load_progress.visibility = View.GONE
        })
    }
}