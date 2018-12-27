package com.jazart.symphony.featured

import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.jazart.symphony.MainActivity
import com.jazart.symphony.R
import com.jazart.symphony.Result
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.di.app
import com.jazart.symphony.model.Song
import kotlinx.android.synthetic.main.feature_music_fragment.*


class FeaturedMusicFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var songsViewModel: SongViewModel
    private lateinit var songsLiveData: LiveData<List<Song>>

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = LayoutInflater.from(context).inflate(R.layout.feature_music_fragment, container, false)
        inject()

        songsViewModel = ViewModelProviders.of(this, SimpleViewModelFactory {
            SongViewModel(app())
        }).get(SongViewModel::class.java)
        songsLiveData = songsViewModel.songs

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        showHideFabMenu()
        featured_songs_toolbar.inflateMenu(R.menu.featured_music_menu)
        loadSongs()

        setupSnackbarBehavior()
        swipeRefreshLayout.setOnRefreshListener(this)

        setupSwipeListener()
    }

    private fun setupSwipeListener() {
        val swipeController = SwipeController(requireContext(),songsViewModel, songsLiveData, musicAdapter)
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(featured_songs)
    }

    private fun loadSongs() {
        songsLiveData.observe(viewLifecycleOwner, Observer { songs ->
            hideProgress()
            musicAdapter.setSongs(songs)
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

    private fun showHideFabMenu() {
        val activity = requireActivity() as MainActivity

        featured_songs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !activity.mFabMenu.isShown)
                    activity.mFabMenu.showMenu(true)
                else if (dy > 0 && activity.mFabMenu.isShown) activity.mFabMenu.hideMenu(true)
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun setupAdapter() {
        featured_songs.isNestedScrollingEnabled = true
        musicAdapter = MusicAdapter(context)
        featured_songs.layoutManager = LinearLayoutManager(context)
        featured_songs.adapter = musicAdapter
    }

    private fun setupSnackbarBehavior() {
        songsViewModel.snackbar.observe(viewLifecycleOwner, Observer { event ->
            when (event) {
                is Result.Success -> Snackbar.make(requireActivity().currentFocus!!, getString(R.string.deleted_song), Snackbar.LENGTH_SHORT)
                else -> Snackbar.make(requireActivity().currentFocus!!, getString(R.string.hidden_song), Snackbar.LENGTH_SHORT)
            }.show()
        })
    }

    private fun inject() {
        app().component.inject(this)
    }
}