package com.jazart.symphony.featured

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.jazart.symphony.R
import com.jazart.symphony.common.Status
import com.jazart.symphony.di.Injectable
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.di.app
import entities.Song
import kotlinx.android.synthetic.main.feature_music_fragment.*
import javax.inject.Inject

class FeaturedMusicFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, Injectable {
    @Inject lateinit var factory: SimpleViewModelFactory
    private lateinit var musicAdapter: MusicAdapter
    private val songsViewModel by viewModels<SongViewModel> {
        factory
    }
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feature_music_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inject()
        setupAdapter()
        featured_songs_toolbar.inflateMenu(R.menu.featured_music_menu)
        featured_songs_toolbar.setOnMenuItemClickListener{item ->
            if(item.itemId == R.id.profileIcon){
                findNavController().navigate(R.id.profileFragment)
                return@setOnMenuItemClickListener true
            }
            return@setOnMenuItemClickListener false
        }
        loadSongs()
        setupSnackbarBehavior()
        swipeRefreshLayout.setOnRefreshListener(this)
        setupSwipeListener()
    }

    private fun setupAdapter() {
        musicAdapter = MusicAdapter(object : DiffUtil.ItemCallback<Song>() {
            override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
            override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id
        }, requireContext())
        featured_songs.adapter = musicAdapter
        featured_songs.isNestedScrollingEnabled = true
        featured_songs.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSwipeListener() {
        val swipeController = SwipeController(requireContext(), songsViewModel, songsViewModel.songs, musicAdapter)
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(featured_songs)
    }

    private fun loadSongs() {
        songsViewModel.loadUserSongs()
        songsViewModel.snackbar.observe(viewLifecycleOwner, Observer {
            view?.let { Snackbar.make(it, "Song does not belong to you", Snackbar.LENGTH_SHORT).show() }
        })
        songsViewModel.songs.observe(viewLifecycleOwner, Observer { songs ->
            hideProgress()
            musicAdapter.submitList(songs ?: listOf())
            swipeRefreshLayout.isRefreshing = false
        })
    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing = false
        songsViewModel.loadUserSongs()
    }

    private fun hideProgress() {
        music_load_progress.visibility = View.GONE
    }

    private fun setupSnackbarBehavior() {
        songsViewModel.snackbar.observe(viewLifecycleOwner, Observer { event ->
            requireActivity().currentFocus?.let {
                when (event.consume()) {
                    is Status.Success -> Snackbar.make(it, getString(R.string.deleted_song), Snackbar.LENGTH_SHORT)
                    else -> Snackbar.make(it, getString(R.string.hidden_song), Snackbar.LENGTH_SHORT)
                }.show()
            }
        })
    }

    private fun inject() {
        app().component.inject(this)
    }
}
