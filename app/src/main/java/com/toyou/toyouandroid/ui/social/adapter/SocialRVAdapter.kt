package com.toyou.toyouandroid.ui.social.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.FriendListModel

class SocialRVAdapter : RecyclerView.Adapter<SocialRVAdapter.SocialViewHolder>() {

    private var friendList : List<FriendListModel> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SocialRVAdapter.SocialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_rv, parent, false)
        return SocialViewHolder(view)
    }

    override fun onBindViewHolder(holder: SocialRVAdapter.SocialViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    class SocialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val friendName : TextView = itemView.findViewById(R.id.friendName_tv)
        private val friendMessage : TextView = itemView.findViewById(R.id.friendMessage_tv)

        fun bind(friend : FriendListModel){
            friendName.text = friend.name
            friendMessage.text = friend.message
        }
    }


}
