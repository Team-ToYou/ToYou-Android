package com.toyou.toyouandroid.presentation.fragment.social.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.FriendListModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel

class SocialRVAdapter(private val viewModel: SocialViewModel, private val onItemClick: (Int) -> Unit,
                      private val showDeleteDialog: (Long) -> Unit
) : RecyclerView.Adapter<SocialRVAdapter.SocialViewHolder>() {

    private var friendList : List<FriendListModel> = emptyList()

    fun setFriendData(friends : List<FriendListModel>){
        this.friendList = friends
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SocialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_rv, parent, false)
        return SocialViewHolder(viewModel, view, onItemClick, showDeleteDialog)
    }

    override fun onBindViewHolder(holder: SocialViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    class SocialViewHolder(private val viewModel: SocialViewModel,itemView: View, onItemClick : (Int) -> Unit,
                           private val showDeleteDialog: (Long) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val friendName : TextView = itemView.findViewById(R.id.friendName_tv)
        private val friendMessage : TextView = itemView.findViewById(R.id.friendMessage_tv)
        private val friendDetailBtn : FrameLayout = itemView.findViewById(R.id.friend_btn)
        private val friendEmotionIcon: ImageView = itemView.findViewById(R.id.imageView)
        private var emotion : Int? = null
        private var ment : String? = null
        private val deleteBtn : FrameLayout = itemView.findViewById(R.id.trash_btn)
        private var friendId: Long = 0

        init {
            friendDetailBtn.setOnClickListener {
                viewModel.resetQuestionData()
                onItemClick(adapterPosition)
                val friend = friendId
                viewModel.setTargetFriend(friend, emotion, ment)
            }

            deleteBtn.setOnClickListener {
                friendId?.let { id ->
                    showDeleteDialog(id) // ID를 전달
                }
            }
        }

        fun bind(friend : FriendListModel){
            friendName.text = friend.name
            friendMessage.text = friend.message
            emotion = friend.emotion
            ment = friend.message

            val emotionIconRes = when (friend.emotion) {
                1 -> R.drawable.home_stamp_option_happy
                2 -> R.drawable.home_stamp_option_exciting
                3 -> R.drawable.friend_normal
                4 -> R.drawable.home_stamp_option_anxiety
                5->R.drawable.home_stamp_option_upset
                else -> R.drawable.social_char
            }
            friendEmotionIcon.setImageResource(emotionIconRes)

        }


    }




}

