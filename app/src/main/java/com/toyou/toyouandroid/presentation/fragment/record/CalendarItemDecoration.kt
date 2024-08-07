package com.toyou.toyouandroid.presentation.fragment.record

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CalendarItemDecoration(private val horizontalSpaceHeight: Int, private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = verticalSpaceHeight
        outRect.right = horizontalSpaceHeight
        outRect.left = horizontalSpaceHeight
    }
}