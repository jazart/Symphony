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
import com.jazart.symphony.R
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.di.app
import com.jazart.symphony.featured.MusicAdapter
import com.jazart.symphony.featured.SongViewModel
import com.jazart.symphony.model.Song
import kotlinx.android.synthetic.main.feature_music_fragment.*
import kotlinx.android.synthetic.main.fragment_posts.*

class UserSongsFragment : Fragment() {
    private val viewModel: SongViewModel by viewModels { SimpleViewModelFactory { SongViewModel(app()) } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        viewModel.loadUserSongs()
        viewModel.userSongs.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
    }


}