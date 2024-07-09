package com.toyou.toyouandroid.ui.social.adapter

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.model.FriendListModel

class SocialRVAdapter : RecyclerView.Adapter<SocialRVAdapter.SocialViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SocialRVAdapter.SocialViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: SocialRVAdapter.SocialViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class SocialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(friend : FriendListModel){

        }
    }


}