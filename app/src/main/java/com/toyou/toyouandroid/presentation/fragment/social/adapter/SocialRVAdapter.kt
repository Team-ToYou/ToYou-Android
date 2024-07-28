package com.toyou.toyouandroid.presentation.fragment.social.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.FriendListModel

class SocialRVAdapter(private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<SocialRVAdapter.SocialViewHolder>() {

    private var friendList : List<FriendListModel> = emptyList()

    fun setFriendData(friends : List<FriendListModel>){
        this.friendList = friends
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SocialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_rv, parent, false)
        return SocialViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: SocialViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    class SocialViewHolder(itemView: View, onItemClick : (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val friendName : TextView = itemView.findViewById(R.id.friendName_tv)
        private val friendMessage : TextView = itemView.findViewById(R.id.friendMessage_tv)
        private val friendDetailBtn : ImageButton = itemView.findViewById(R.id.friend_btn)

        init {
            friendDetailBtn.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(friend : FriendListModel){
            friendName.text = friend.name
            friendMessage.text = friend.message
        }
    }


}

