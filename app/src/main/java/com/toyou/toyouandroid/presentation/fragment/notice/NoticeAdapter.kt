package com.toyou.toyouandroid.presentation.fragment.notice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.ItemNoticeCardCheckBinding
import com.toyou.toyouandroid.databinding.ItemNoticeFriendRequestAcceptedBinding
import com.toyou.toyouandroid.databinding.ItemNoticeFriendRequestBinding
import com.toyou.toyouandroid.model.NoticeItem

class NoticeAdapter(private val items: MutableList<NoticeItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_FRIEND_REQUEST = 1
        private const val TYPE_CARD_CHECK = 2
        private const val TYPE_FRIEND_REQUEST_ACCEPTED = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NoticeItem.NoticeFriendRequestItem -> TYPE_FRIEND_REQUEST
            is NoticeItem.NoticeFriendRequestAcceptedItem -> TYPE_FRIEND_REQUEST_ACCEPTED
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
            TYPE_FRIEND_REQUEST_ACCEPTED -> {
                val binding = DataBindingUtil.inflate<ItemNoticeFriendRequestAcceptedBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_notice_friend_request_accepted,
                    parent,
                    false
                )
                FriendRequestAcceptedViewHolder(binding)
            }
            else -> throw IllegalArgumentException("유효하지 않은 NoticeAdapter type입니다.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FriendRequestViewHolder -> holder.bind(items[position] as NoticeItem.NoticeFriendRequestItem)
            is FriendRequestAcceptedViewHolder -> holder.bind(items[position] as NoticeItem.NoticeFriendRequestAcceptedItem)
            is CardCheckViewHolder -> holder.bind(items[position] as NoticeItem.NoticeCardCheckItem)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class FriendRequestViewHolder(private val binding: ItemNoticeFriendRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeItem.NoticeFriendRequestItem) {
            binding.itemFriendRequest = item
            binding.noticeCardCheckDelete.setOnClickListener {
                removeItem(this.layoutPosition)
            }
            binding.executePendingBindings()
        }
    }

    inner class FriendRequestAcceptedViewHolder(private val binding: ItemNoticeFriendRequestAcceptedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeItem.NoticeFriendRequestAcceptedItem) {
            binding.itemFriendRequestAccepted = item
            binding.noticeCardCheckDelete.setOnClickListener {
                removeItem(this.layoutPosition)
            }

            val layoutParams = binding.root.layoutParams
            layoutParams.width = (binding.root.context.resources.displayMetrics.widthPixels * 5 / 6)
            binding.root.layoutParams = layoutParams

            binding.executePendingBindings()
        }
    }

    inner class CardCheckViewHolder(private val binding: ItemNoticeCardCheckBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeItem.NoticeCardCheckItem) {
            binding.itemCardCheck = item
            binding.noticeCardCheckDelete.setOnClickListener {
                removeItem(this.layoutPosition)
            }
            binding.executePendingBindings()
        }
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size) // 아이템을 제거한 이후의 아이템들에 대해 포지션 업데이트
        }
    }
}
