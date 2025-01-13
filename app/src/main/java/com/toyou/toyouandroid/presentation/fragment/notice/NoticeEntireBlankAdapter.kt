package com.toyou.toyouandroid.presentation.fragment.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R

class NoticeEntireBlankAdapter : RecyclerView.Adapter<NoticeEntireBlankAdapter.BlankNoticeViewHolder>() {

    // 빈 아이템 리스트, 고정된 데이터라면 크기를 1로 유지
    private val items = listOf(Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlankNoticeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notice_entire_blank, parent, false)
        return BlankNoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlankNoticeViewHolder, position: Int) {
        // 빈 데이터인 경우 별다른 바인딩 로직이 없으므로 생략
    }

    override fun getItemCount(): Int {
        return items.size // 항상 1개 아이템만 표시
    }

    class BlankNoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
