package com.toyou.toyouandroid.presentation.fragment.notice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.ItemNoticeCardCheckBinding
import com.toyou.toyouandroid.databinding.ItemNoticeFriendRequestBinding
import com.toyou.toyouandroid.model.NoticeItem

class NoticeAdapter(private val items: List<NoticeItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_FRIEND_REQUEST = 1
        private const val TYPE_CARD_CHECK = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NoticeItem.NoticeFriendRequestItem -> TYPE_FRIEND_REQUEST
            is NoticeItem.NoticeCardCheckItem -> TYPE_CARD_CHECK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FRIEND_REQUEST -> {
                val binding = DataBindingUtil.inflate<ItemNoticeFriendRequestBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_notice_friend_request,
                    parent,
                    false
                )
                FriendRequestViewHolder(binding)
            }
            TYPE_CARD_CHECK -> {
                val binding = DataBindingUtil.inflate<ItemNoticeCardCheckBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_notice_card_check,
                    parent,
                    false
                )
                CardCheckViewHolder(binding)
            }
            else -> throw IllegalArgumentException("유효하지 않은 NoticeAdapter type입니다.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FriendRequestViewHolder -> holder.bind(items[position] as NoticeItem.NoticeFriendRequestItem)
            is CardCheckViewHolder -> holder.bind(items[position] as NoticeItem.NoticeCardCheckItem)
        }
    }

    override fun getItemCount(): Int = items.size

    class FriendRequestViewHolder(private val binding: ItemNoticeFriendRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeItem.NoticeFriendRequestItem) {
            binding.itemFriendRequest = item
            binding.executePendingBindings()
        }
    }

    class CardCheckViewHolder(private val binding: ItemNoticeCardCheckBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeItem.NoticeCardCheckItem) {
            binding.itemCardCheck = item
            binding.executePendingBindings()
        }
    }
}
