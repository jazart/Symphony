package com.jazart.symphony.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import com.jazart.symphony.MainActivity
import com.jazart.symphony.R
import com.jazart.symphony.di.App
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.model.Song
import java.util.Objects

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife


class FeaturedMusicFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private var mMusicAdapter: MusicAdapter? = null
    private var mSongsViewModel: SongViewModel? = null
    @BindView(R.id.swipeRefreshLayout)
    internal var mRefreshSongs: SwipeRefreshLayout? = null
    @BindView(R.id.music_load_progress)
    internal var mSongLoading: ProgressBar? = null
    @BindView(R.id.featured_songs)
    internal var mRecyclerView: RecyclerView? = null
    @BindView(R.id.featured_songs_toolbar)
    internal var mSongsToolBar: androidx.appcompat.widget.Toolbar? = null

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val v = LayoutInflater.from(context).inflate(R.layout.feature_music_fragment, container, false)
        ButterKnife.bind(this, v)
        inject()
        mRecyclerView!!.isNestedScrollingEnabled = true

        showHideFabMenu()
        setHasOptionsMenu(true)
        mSongsToolBar!!.inflateMenu(R.menu.featured_music_menu)

        mSongsViewModel = ViewModelProviders.of(this, SimpleViewModelFactory())

        val mSongsLiveData = mSongsViewModel!!.songs
        loadSongs(mSongsLiveData)

        mRefreshSongs!!.setOnRefreshListener(this)
        reloadSongs()

        setupSwipeListener(mSongsLiveData)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.featured_music_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSwipeListener(mSongsLiveData: LiveData<List<Song>>) {
        val swipeController = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(@NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, @NonNull viewHolder1: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, i: Int) {
                val pos = viewHolder.adapterPosition
                val song = Objects.requireNonNull<List<Song>>(mSongsLiveData.value)[pos]
                mSongsViewModel!!.removeSongFromStorage(song)
            }

            override fun onChildDraw(@NonNull c: Canvas, @NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val bg = ColorDrawable(Color.RED)
                val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_white_32dp)
                val v = viewHolder.itemView
                val backgroundCornerOffset = 5
                val iconMargin = (v.height - icon.getIntrinsicHeight()) / 2
                val iconTop = v.top + (v.height - icon.getIntrinsicHeight()) / 2
                val iconBottom = iconTop + icon.getIntrinsicHeight()
                var iconLeft = 0
                var iconRight = 0
                if (dX > 0) {
                    bg.setBounds(0, v.top,
                            v.left + dX.toInt() + backgroundCornerOffset,
                            v.bottom)
                    iconLeft = iconMargin
                    iconRight = iconMargin + icon.getIntrinsicWidth()
                } else {
                    bg.setBounds(0, 0, 0, 0)
                }
                bg.draw(c)
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                icon.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        swipeController.attachToRecyclerView(mRecyclerView)
    }

    private fun loadSongs(mSongsLiveData: LiveData<List<Song>>) {
        mSongsLiveData.observe(this, Observer { songs ->
            mMusicAdapter = MusicAdapter(context)
            hideProgress()
            mMusicAdapter!!.setSongs(songs)
            mRecyclerView!!.layoutManager = LinearLayoutManager(context)
            mRecyclerView!!.adapter = mMusicAdapter
            mRefreshSongs!!.isRefreshing = false
        })
    }

    override fun onRefresh() {
        mSongsViewModel!!.refreshContent()
    }

    private fun reloadSongs() {
        mRefreshSongs!!.isRefreshing = false
        mSongsViewModel!!.update()
    }

    private fun hideProgress() {
        mSongLoading!!.visibility = View.GONE
    }

    private fun showHideFabMenu() {
        val activity = requireActivity() as MainActivity

        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy < 0 && !activity.mFabMenu.isShown)
                    activity.mFabMenu.showMenu(true)
                else if (dy > 0 && activity.mFabMenu.isShown) activity.mFabMenu.hideMenu(true)
            }
        })
    }

    private fun inject() {
        val app = requireActivity().application as App
        app.component.inject(this)
    }

}