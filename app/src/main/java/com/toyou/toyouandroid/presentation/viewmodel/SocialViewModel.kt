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
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.dto.request.FCM
import com.toyou.toyouandroid.model.FriendListModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SocialViewModel : ViewModel() {
    private val repository = SocialRepository()
    private val fcmRepository = FCMRepository()
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
    private val _retrieveToken = MutableLiveData<List<String>>()
    val retrieveToken : LiveData<List<String>> get() = _retrieveToken
    private val _fcm = MutableLiveData<FCM>()
    val fcm : LiveData<FCM> get() = _fcm


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
                response.result?.let { result ->
                    _isFriend.value = result.status
                    _searchName.value = result.name
                    Log.d("search API 성공", _isFriend.value.toString())
                } ?: run {
                    Log.e("search API 실패", "결과가 null입니다.")
                    _isFriend.value = "결과를 가져오지 못했습니다."
                }
            } else {
                Log.e("search API 실패", "API 호출 실패: ${response.message}")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("search API 실패", "서버 응답 메시지: $errorBody")

            when {
                errorBody?.contains("USER400") == true -> {
                    _isFriend.value = "400"
                }
                errorBody?.contains("USER401") == true -> {
                    _isFriend.value = "401"
                }

                else -> {
                    _isFriend.value = "400"
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
        retrieveTokenFromServer(questionDto.value!!.target)
        _retrieveToken.value?.let { tokens ->
            for (token in tokens) {
                postFCM(questionDto.value!!.target, token, 3)
            }
            resetToken()

        }
        resetQuestionData()
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
        retrieveTokenFromServer(name)
        _retrieveToken.value?.let { tokens ->
            for (token in tokens) {
                postFCM(name, token, 1)
            }
            resetToken()

        }

    }

    fun deleteFriend(name: String){
        _friendRequest.value = RequestFriend(name = name)
        viewModelScope.launch {
            _friendRequest.value?.let { name ->
                repository.deleteFriendData(name)
            } ?: run {
                Log.e("api 실패!", "널")
            }
            Log.d("api 성공!", "성공")
        }
    }

    fun postFCM(name: String, token : String, type : Int){
        when(type){
            1 -> _fcm.value = FCM(token = token, title = "친구 요청", body = "${name}님이 친구 요청을 보냈습니다.")
            2 -> _fcm.value = FCM(token = token, title = "친구 요청 승인", body = "${name}님이 친구 요청을 승인했습니다.")
            3 -> _fcm.value = FCM(token = token, title = "질문 생성", body = "${name}님이 질문을 생성했습니다.")
        }
        viewModelScope.launch {
            _fcm.value?.let {fcm ->
                fcmRepository.postFCM(fcm)
            } ?: run {
                Log.e("fcm api 실패!", "널")
            }
            Log.d("fcm api 성공!", "성공")
        }

    }

    fun patchApprove(name: String){
        _friendRequest.value = RequestFriend(name = name)
        viewModelScope.launch {
            _friendRequest.value?.let { name ->
                repository.patchApproveFriend(name)
            } ?: run {
                Log.e("api 실패!", "널")
            }
            Log.d("api 성공!", "성공")
        }
        retrieveTokenFromServer(name)
        _retrieveToken.value?.let { tokens ->
            for (token in tokens) {
                postFCM(name, token, 1)
            }
            resetToken()
        }
    }

    fun retrieveTokenFromServer(name : String) = viewModelScope.launch {
            try {
                val response = fcmRepository.getToken(name)
                if (response.isSuccess) {
                    _retrieveToken.value = response.result.tokens
                    // 서버에서 받은 토큰을 사용해 로직을 처리
                    Log.d("Token Retrieval", retrieveToken.value.toString())
                } else {
                    Log.e("Token Retrieval", "토큰 조회 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("Token Retrieval", "토큰 조회 중 오류 발생: ${e.message}")

            }
        }

    private fun resetQuestionData() {
        _selectedChar.value = -1
        _nextBtnEnabled.value = false
        _questionDto.value = QuestionDto("", "", "", false, null) // 초기화
        _selectedEmotion.value = 0
        _selectedEmotionMent.value = ""
        _optionList.value = emptyList()
    }

    private fun resetToken(){
        _retrieveToken.value = emptyList()
    }


}

