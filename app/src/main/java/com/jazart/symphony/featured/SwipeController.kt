package com.jazart.symphony.featured

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jazart.symphony.R
import entities.Song

class SwipeController(val context: Context,
                      private val songsViewModel: SongViewModel,
                      private val mSongsLiveData: LiveData<List<Song>>,
                      private val musicAdapter: MusicAdapter)
    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT)
{

    override fun onMove(@NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, @NonNull viewHolder1: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, i: Int) {
        val pos = viewHolder.adapterPosition
        mSongsLiveData.value?.get(pos)?.let { song ->
            songsViewModel.removeSongFromStorage(song)
        }
    }

    override fun onChildDraw(@NonNull c: Canvas, @NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        val bg = ColorDrawable(Color.RED)
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_32dp)
        icon?.let {
            val v = viewHolder.itemView
            val backgroundCornerOffset = 5
            val iconMargin = (v.height - icon.intrinsicHeight) / 2
            val iconTop = v.top + (v.height - icon.intrinsicHeight) / 2
            val iconBottom = iconTop + icon.intrinsicHeight

            var iconLeft = 0
            var iconRight = 0
            if (dX > 0) {
                bg.setBounds(0, v.top,
                        v.left + dX.toInt() + backgroundCornerOffset,
                        v.bottom)
                iconLeft = iconMargin
                iconRight = iconMargin + icon.intrinsicWidth
            } else {
                bg.setBounds(0, 0, 0, 0)
            }
            bg.draw(c)
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            icon.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
