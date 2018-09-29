package com.bajiuk.pet.recyclerview

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class MarginItemDecoration(
    private val vertical: Int,
    private val horizontal: Int = 0
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val newBottom = if (parent.findContainingViewHolder(view)?.layoutPosition == state.itemCount - 1)
            horizontal * 10 else horizontal
        outRect.set(horizontal, vertical, horizontal, newBottom)
    }
}