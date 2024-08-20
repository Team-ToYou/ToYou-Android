package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.model.FriendListModel
import com.toyou.toyouandroid.model.PreviewCardModel
import kotlinx.coroutines.launch

class SocialViewModel : ViewModel() {
    private val repository = SocialRepository()
    private val _friends = MutableLiveData<List<FriendListModel>>()
    val friends: LiveData<List<FriendListModel>> get() = _friends
    private val _clickedPosition = MutableLiveData<Map<Int, Boolean>>()

    private val _selectedChar = MutableLiveData<Int>()
    val selectedChar: LiveData<Int> get() = _selectedChar
    private val _nextBtnEnabled = MutableLiveData<Boolean>()
    val nextBtnEnabled: LiveData<Boolean> get() = _nextBtnEnabled

    private val _questionDto = MutableLiveData<QuestionDto>()
    val questionDto: LiveData<QuestionDto> get() = _questionDto
    private val _selectedEmotion = MutableLiveData<Int>()
    val selectedEmotion: LiveData<Int> get() = _selectedEmotion
    private val _selectedEmotionMent = MutableLiveData<String>()
    val selectedEmotionMent: LiveData<String> get() = _selectedEmotionMent

    private val _optionList = MutableLiveData<List<String>>()
    val optionList: LiveData<List<String>> get() = _optionList

    init {
        loadInitQuestionType()
        _selectedChar.value = -1
        _nextBtnEnabled.value = false
        _questionDto.value = QuestionDto("", "", "", false, null) // 초기화
    }

    fun setTargetFriend(friendName: String, emotion: Int?, ment: String?) {
        val currentQuestionDto = _questionDto.value ?: QuestionDto("", "", "", false, null)
        _questionDto.value = currentQuestionDto.copy(target = friendName)
        _selectedEmotion!!.value = emotion
        _selectedEmotionMent!!.value = ment
        Log.d("타겟", _questionDto.value.toString())
        Log.d("타겟", _selectedEmotion.value.toString())
    }

    fun setTypeFriend(type: String) {
        _questionDto.value?.let { currentQuestionDto ->
            _questionDto.value = currentQuestionDto.copy(type = type)
            Log.d("타겟2", _questionDto.value.toString())
        }
    }

    fun getFriendsData() = viewModelScope.launch {
        try {
            val response = repository.getFriendsData()
            if (response.isSuccess) {
                Log.d("API 호출 성공", response.message)
                val friendsDto = response.result
                friendsDto?.let { mapToFriendModels(it) }
            } else {
                Log.e("CardViewModel", "API 호출 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("CardViewModel", "예외 발생: ${e.message}")
        }
    }

    private fun mapToFriendModels(friendsDto: FriendsDto) {
        val friendListModel = mutableListOf<FriendListModel>()

        for (friend in friendsDto.friends) {
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

    fun loadInitQuestionType() {
        val initialMap = mapOf(
            1 to false,
            2 to false,
            3 to false
        )
        _clickedPosition.value = initialMap
    }

    fun onCharSelected(position: Int) {
        _selectedChar.value = if (_selectedChar.value == position) -1 else position
        _nextBtnEnabled.value = _selectedChar.value != -1
    }

    fun getAnswerLength(answer: String): Int {
        return answer.length
    }

    fun updateQuestionOptions(newOptions: List<String>) {
        _questionDto.value?.let { currentQuestionDto ->
            _questionDto.value = currentQuestionDto.copy(options = newOptions)
            Log.d("옵션", _questionDto.value.toString())
        }
    }

    fun updateOption() {
        _optionList.value = _questionDto.value!!.options!!
    }

    fun removeOptions(){
        _questionDto.value?.options = null
    }

    fun removeContent(){
        _questionDto.value?.content = ""
    }

    fun isAnonymous(isChecked : Boolean){
        if (isChecked) _questionDto.value?.anonymous = true
        else _questionDto.value?.anonymous = false
    }

    fun sendQuestion() {
        viewModelScope.launch {
            // Access the value of questionDto safely with let
            _questionDto.value?.let { currentQuestionDto ->
                repository.postQuestionData(currentQuestionDto)
            } ?: run {
                Log.e("api 실패!", "널")
            }
            Log.d("api 성공!", "성공")
        }
    }
}
