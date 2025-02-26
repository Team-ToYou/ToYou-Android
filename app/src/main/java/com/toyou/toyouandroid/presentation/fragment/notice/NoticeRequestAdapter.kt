package com.toyou.toyouandroid.presentation.fragment.notice

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.ItemNoticeFriendRequestBinding
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import timber.log.Timber

class NoticeRequestAdapter(
    private val items: MutableList<NoticeItem.NoticeFriendRequestItem>,
    private val listener: NoticeAdapterListener,
    private val socialViewModel: SocialViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_FRIEND_REQUEST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            else -> TYPE_FRIEND_REQUEST
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
            else -> throw IllegalArgumentException("유효하지 않은 NoticeRequestAdapter type입니다.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FriendRequestViewHolder -> holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<NoticeItem.NoticeFriendRequestItem>) {
        Timber.d("Updating items. Adapter: $this, Items: ${System.identityHashCode(items)}")
        items.clear()
        items.addAll(newItems.toMutableList())
        notifyDataSetChanged()
    }

    inner class FriendRequestViewHolder(private val binding: ItemNoticeFriendRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeItem.NoticeFriendRequestItem) {
            binding.itemFriendRequest = item

            val nickname = if (item.nickname.length > 7) {
                TextUtils.ellipsize(item.nickname, binding.noticeBox.paint, 7 * binding.noticeBox.textSize, TextUtils.TruncateAt.END).toString()
            } else {
                item.nickname
            }
            val formattedMessage = binding.root.context.getString(R.string.notice_friend_request, nickname)
            binding.noticeBox.text = formattedMessage


            binding.noticeCardCheckDelete.setOnClickListener {
                socialViewModel.getFriendsData() //친구 목록 불러오는 api
                // 삭제 API 호출
                socialViewModel.deleteFriend(item.nickname, null)
                removeItem(this.layoutPosition)
            }

            binding.noticeFriendRequestBtn.setOnClickListener {
                // 친구 요청 수락 버튼 클릭 후 알림 메시지 제거
                Timber.d(item.nickname)
                listener.onFriendRequestApprove(item.nickname, item.alarmId, this.layoutPosition)
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
