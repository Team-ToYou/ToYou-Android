package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.model.FriendListModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

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

    private val _isFriend = MutableLiveData<String>()
    val isFriend: LiveData<String> get() = _isFriend

    private val _searchName = MutableLiveData<String>()
    val searchName: LiveData<String> get() = _searchName

    private val _friendRequest = MutableLiveData<RequestFriend>()
    val friendRequest: LiveData<RequestFriend> get() = _friendRequest


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

    fun getSearchData(name: String) = viewModelScope.launch {
        try {
            val response = repository.getSearchData(name)
            if (response.isSuccess) {
                // 성공 시에만 result에 접근하도록 수정
                response.result?.let { result ->
                    _isFriend.value = result.status
                    _searchName.value = result.name
                    Log.d("search API 성공", _isFriend.value.toString())
                } ?: run {
                    // result가 null일 경우 처리
                    Log.e("search API 실패", "결과가 null입니다.")
                    _isFriend.value = "결과를 가져오지 못했습니다."
                }
            } else {
                // 실패 시 기본값 설정
                Log.e("search API 실패", "API 호출 실패: ${response.message}")
                _isFriend.value = "해당 사용자를 찾을 수 없습니다."
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("search API 실패", "서버 응답 메시지: $errorBody")

            when {
                e.code() == 400 && errorBody?.contains("USER400") == true -> {
                    _isFriend.value = "찾으시는 닉네임이 존재하지 않아요. 다시 입력해주세요"
                }

                e.code() == 400 && errorBody?.contains("USER401") == true -> {
                    _isFriend.value = "스스로에게 요청할 수 없습니다. 다시 입력해주세요"
                }

                else -> {
                    _isFriend.value = "알 수 없는 에러가 발생했습니다."
                }
            }
        } catch (e: Exception) {
            Log.e("search API 실패", "예외 발생: ${e.message}")
            _isFriend.value = "예상치 못한 오류가 발생했습니다."
            e.printStackTrace()
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

    fun removeOptions() {
        _questionDto.value?.options = null
    }

    fun removeContent() {
        _questionDto.value?.content = ""
    }

    fun isAnonymous(isChecked: Boolean) {
        if (isChecked) _questionDto.value?.anonymous = true
        else _questionDto.value?.anonymous = false
    }

    fun sendQuestion() {
        viewModelScope.launch {
            _questionDto.value?.let { currentQuestionDto ->
                repository.postQuestionData(currentQuestionDto)
            } ?: run {
                Log.e("api 실패!", "널")
            }
            Log.d("api 성공!", "성공")
        }
    }

    fun sendFriendRequest(name: String) {
        _friendRequest.value = RequestFriend(name = name)
        viewModelScope.launch {
            _friendRequest.value?.let { name ->
                repository.postRequest(name)
            } ?: run {
                Log.e("api 실패!", "널")
            }
            Log.d("api 성공!", "성공")
        }

    }
}

