package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.model.FriendListModel
import com.toyou.toyouandroid.model.QuestionTypeModel
import kotlinx.coroutines.launch

class SocialViewModel : ViewModel() {
    private val repository = SocialRepository()
    private val _friends = MutableLiveData<List<FriendListModel>>()
    val friends : LiveData<List<FriendListModel>> get () = _friends
    private val _clickedPosition = MutableLiveData<Map<Int, Boolean>>()
    val clickedPosition : LiveData<Map<Int, Boolean>> get() = _clickedPosition

    private val _selectedChar = MutableLiveData<Int>()
    val selectedChar : LiveData<Int> get() = _selectedChar
    private val _nextBtnEnabled = MutableLiveData<Boolean>()
    val nextBtnEnabled : LiveData<Boolean> get() = _nextBtnEnabled

    private val _plusBoxVisibility = MutableLiveData<List<Boolean>>()
    val plusBoxVisibility : MutableLiveData<List<Boolean>> get() = _plusBoxVisibility

    init {
        loadInitQuestionType()
        _selectedChar.value = -1
        _nextBtnEnabled.value = false
    }

    fun getFriendsData() = viewModelScope.launch {
        try {
            val response = repository.getFriendsData()
            if (response.isSuccess){
                val friendsDto = response.result
                friendsDto?.let { mapToFriendModels(it) }
            } else{
                Log.e("CardViewModel", "API 호출 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("CardViewModel", "예외 발생: ${e.message}")
        }
    }

    private fun mapToFriendModels(friendsDto: FriendsDto){
        val friendListModel = mutableListOf<FriendListModel>()

        for (friend in friendsDto.friends){
            friendListModel.add(
                FriendListModel(
                    name = friend.nickname,
                    message = friend.ment ?: "",
                    emotion = emotionType(friend.emotion)
                )
            )
        }
        _friends.value = friendListModel
    }

    private fun emotionType(type: String?): Int? {
        return when (type) {
            "HAPPY" -> 1
            "EXCITED" -> 2
            "NORMAL" -> 3
            "NERVOUS" -> 4
            "ANGRY" -> 5
            else -> null
        }
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

    fun onCharSelected(position : Int){
        _selectedChar.value = if (_selectedChar.value == position) -1 else position
        _nextBtnEnabled.value = _selectedChar.value != -1
    }

    fun togglePlusBoxVisibility() {
        val visibility = _plusBoxVisibility.value ?: listOf(false, false, false)
        val newVisibility = visibility.toMutableList()

        //조건에 맞는게 나타나면 그 뒤로 실행x
        when {
            !newVisibility[0] -> newVisibility[0] = true
            !newVisibility[1] -> newVisibility[1] = true
            !newVisibility[2] -> newVisibility[2] = true
        }
        _plusBoxVisibility.value = newVisibility
    }

    fun hidePlusBox(index: Int) {
        val visibility = _plusBoxVisibility.value ?: listOf(false, false, false)
        val newVisibility = visibility.toMutableList()
        newVisibility[index] = false
        _plusBoxVisibility.value = newVisibility
    }

}