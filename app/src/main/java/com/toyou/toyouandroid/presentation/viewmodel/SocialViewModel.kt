package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.model.FriendListModel
import com.toyou.toyouandroid.model.QuestionTypeModel

class SocialViewModel : ViewModel() {
    private val _friends = MutableLiveData<List<FriendListModel>>()
    val friends : LiveData<List<FriendListModel>> get () = _friends
    private val _clickedPosition = MutableLiveData<Map<Int, Boolean>>()
    val clickedPosition : LiveData<Map<Int, Boolean>> get() = _clickedPosition

    init {
        loadFriendData()
        loadInitQuestionType()
    }

    fun loadFriendData(){
        val sampleData = listOf(
            FriendListModel("jjeong", "평범한 하루였어요"),
            FriendListModel("jjeong", "평범한 하루였어요"),
            FriendListModel("jjeong", "평범한 하루였어요"),
        )
        _friends.value = sampleData
    }

    fun loadInitQuestionType(){
        val initialMap = mapOf(
            1 to false,
            2 to false,
            3 to false
        )
        _clickedPosition.value = initialMap
    }

    fun updateClickState(key: Int) {
        val currentMap = _clickedPosition.value?.toMutableMap() ?: mutableMapOf()
        currentMap[key] = !(currentMap[key] ?: false)


        _clickedPosition.value = currentMap
    }



}