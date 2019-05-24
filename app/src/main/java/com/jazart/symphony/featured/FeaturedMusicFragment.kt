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
import com.jazart.symphony.Result
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.di.app
import com.jazart.symphony.model.Song
import kotlinx.android.synthetic.main.feature_music_fragment.*

class FeaturedMusicFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var musicAdapter: MusicAdapter
    private val songsViewModel by viewModels<SongViewModel> {
        SimpleViewModelFactory { SongViewModel(app()) }
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.feature_music_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inject()
        setupAdapter()
        featured_songs_toolbar.inflateMenu(R.menu.featured_music_menu)
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
        featured_songs.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSwipeListener() {
        val swipeController = songsViewModel.songs?.let { SwipeController(requireContext(), songsViewModel, it, musicAdapter) }
        val itemTouchHelper = swipeController?.let { ItemTouchHelper(it) }
        itemTouchHelper?.attachToRecyclerView(featured_songs)
    }

    private fun loadSongs() {
        songsViewModel.load()
        songsViewModel.songs.observe(viewLifecycleOwner, Observer { songs ->
            hideProgress()
            musicAdapter.submitList(songs ?: listOf())
            swipeRefreshLayout.isRefreshing = false
        })
    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing = false
        songsViewModel.refreshContent()
    }

    private fun hideProgress() {
        music_load_progress.visibility = View.GONE
    }

    private fun setupSnackbarBehavior() {
        songsViewModel.snackbar.observe(viewLifecycleOwner, Observer { event ->
            requireActivity().currentFocus?.let {
                when (event) {
                    is Result.Success -> Snackbar.make(it, getString(R.string.deleted_song), Snackbar.LENGTH_SHORT)
                    else -> Snackbar.make(it, getString(R.string.hidden_song), Snackbar.LENGTH_SHORT)
                }.show()
            }
        })
    }

    private fun inject() {
        app().component.inject(this)
    }
}


