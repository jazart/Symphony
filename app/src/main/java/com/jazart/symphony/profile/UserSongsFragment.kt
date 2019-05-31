package com.jazart.symphony.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jazart.symphony.R
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.di.app
import com.jazart.symphony.featured.MusicAdapter
import com.jazart.symphony.featured.SongViewModel
import com.jazart.symphony.model.Song
import kotlinx.android.synthetic.main.feature_music_fragment.*
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_posts.swipeRefreshLayout
import javax.inject.Inject

class UserSongsFragment : Fragment()  {
    @Inject lateinit var factory: SimpleViewModelFactory
    private val viewModel: SongViewModel by viewModels {factory}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        app().component.inject(this)
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUserSongs()
        val adapter =  MusicAdapter(object : DiffUtil.ItemCallback<Song>() {
            override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
            override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id
        }, requireContext())
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        swipeRefreshLayout.setOnRefreshListener { refreshSongs() }
        viewModel.loadUserSongs()
        viewModel.userSongs.observe(viewLifecycleOwner, Observer { songs ->
            adapter.submitList(songs)
            post_load_progress.visibility = View.GONE
        })
    }

    private fun refreshSongs() {
        viewModel.refreshUserSongs()
        swipeRefreshLayout.isRefreshing = false
    }

}