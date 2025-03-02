package com.toyou.toyouandroid.presentation.fragment.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class RVMarginItemDecoration (private val spaceSize: Int, private val side : Boolean) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) != 0) {
                left = spaceSize
                right = spaceSize

            }


               // left = spaceSize
                //right = spaceSize

            //bottom = spaceSize
        }
    }
}