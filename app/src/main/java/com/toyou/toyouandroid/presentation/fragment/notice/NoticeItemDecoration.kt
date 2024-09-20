package com.toyou.toyouandroid.presentation.fragment.notice

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NoticeItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        // 마지막 아이템 이후에는 간격을 적용하지 않도록 처리
        if (position == RecyclerView.NO_POSITION || position == parent.adapter!!.itemCount - 1) {
            // 마지막 아이템 이후에는 간격 적용하지 않음
            outRect.bottom = 0
        } else {
            // 그 외의 아이템 사이에만 간격 적용
            outRect.bottom = verticalSpaceHeight
        }
    }
}
