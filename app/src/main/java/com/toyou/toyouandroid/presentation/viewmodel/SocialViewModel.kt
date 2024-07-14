package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.model.FriendListModel

class SocialViewModel : ViewModel() {
    private val _friends = MutableLiveData<List<FriendListModel>>()
    val friends : LiveData<List<FriendListModel>> get () = _friends

    init {
        loadFriendData()
    }

    fun loadFriendData(){
        val sampleData = listOf(
            FriendListModel("jjeong", "평범한 하루였어요"),
            FriendListModel("jjeong", "평범한 하루였어요"),
            FriendListModel("jjeong", "평범한 하루였어요"),
        )
        _friends.value = sampleData
    }

}