package com.jazart.symphony

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class SwipeController : ItemTouchHelper.SimpleCallback(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION) {
    override fun onMove(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, pos: Int) {
    }

    override fun setDefaultDragDirs(defaultDragDirs: Int) {
        super.setDefaultDragDirs(ItemTouchHelper.ACTION_STATE_IDLE)
    }

    override fun setDefaultSwipeDirs(defaultSwipeDirs: Int) {
        super.setDefaultSwipeDirs(ItemTouchHelper.RIGHT)
    }
}
