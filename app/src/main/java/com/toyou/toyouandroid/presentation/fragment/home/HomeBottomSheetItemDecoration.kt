package com.toyou.toyouandroid.presentation.fragment.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HomeBottomSheetItemDecoration(private val horizontalSpaceHeight: Int, private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = verticalSpaceHeight
        outRect.right = horizontalSpaceHeight
        outRect.left = horizontalSpaceHeight
    }
}