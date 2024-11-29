package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.TableInfo
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.dto.request.FCM
import com.toyou.toyouandroid.model.FriendListModel
import com.toyou.toyouandroid.presentation.fragment.notice.ApprovalResult
import com.toyou.toyouandroid.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class SocialViewModel(
    private val repository: SocialRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val fcmRepository = FCMRepository(tokenManager)

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
    private val _friendRequestCompleted = MutableLiveData<Boolean>()
    val friendRequestCompleted: LiveData<Boolean> get() = _friendRequestCompleted
    private val _friendRequestCanceled = MutableLiveData<Boolean>()
    val friendRequestCanceled : LiveData<Boolean> get() = _friendRequestCanceled
    private val _friendRequestRemove = MutableLiveData<Boolean>()
    val friendRequestRemove : LiveData<Boolean> get() = _friendRequestRemove


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
    }

    fun setTypeFriend(type: String) {
        _questionDto.value?.let { currentQuestionDto ->
            _questionDto.value = currentQuestionDto.copy(type = type)
            Timber.tag("타겟2").d(_questionDto.value.toString())
        }
    }

    // 토큰 재발급 정상 호출 완료
    fun getFriendsData() {
        viewModelScope.launch {
            try {
                val response = repository.getFriendsData()
                if (response.isSuccess) {
                    Timber.d("API 호출 성공: ${response.message}")
                    val friendsDto = response.result
                    mapToFriendModels(friendsDto)
                } else {
                    Timber.tag("SocialViewModel").e("API 호출 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { getFriendsData() },
                        onFailure = { Timber.e("getFriendsData API call failed") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("SocialViewModel").e("예외 발생: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { getFriendsData() },
                    onFailure = { Timber.e("getFriendsData API call failed") }
                )
            }
        }
    }

    // 토큰 재발급 정상 호출 완료
    fun getSearchData(name: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSearchData(name)
                if (response.isSuccess) {
                    response.result.let { result ->
                        _isFriend.value = result.status
                        _searchName.value = result.name
                        Timber.tag("search API 성공").d(_isFriend.value.toString())
                    }
                } else {
                    Timber.tag("search API 실패").d("API 호출 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { getSearchData(name) },
                        onFailure = { Timber.e("getSearchData API call failed") }
                    )
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Timber.tag("search API 실패").e("서버 응답 메시지: $errorBody")

                tokenManager.refreshToken(
                    onSuccess = { getSearchData(name) },
                    onFailure = { Timber.e("getSearchData API call failed") }
                )

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
                Timber.tag("search API 실패").e("예외 발생: ${e.message}")
                _isFriend.value = "예상치 못한 오류가 발생했습니다."
                e.printStackTrace()
                tokenManager.refreshToken(
                    onSuccess = { getSearchData(name) },
                    onFailure = { Timber.e("getSearchData API call failed") }
                )
            }
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

    private fun loadInitQuestionType() {
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
            Timber.tag("SocialViewModel").d("옵션 업데이트: ${_questionDto.value}")
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

    // 토큰 재발급 정상 호출 완료
    fun sendQuestion(myName: String) {
        viewModelScope.launch {
            try {
                _questionDto.value?.let { currentQuestionDto ->
                    val response = repository.postQuestionData(currentQuestionDto)
                    if (response.isSuccess) {
                        Timber.tag("SocialViewModel").d("질문 전송 성공")

                        retrieveTokenFromServer(questionDto.value!!.target)
                        if (_questionDto.value!!.anonymous){
                            _retrieveToken.value?.let { tokens ->
                                for (token in tokens) {
                                    postFCM("익명", token, 3)
                                }
                            }
                        }else {
                            _retrieveToken.value?.let { tokens ->
                                for (token in tokens) {
                                    postFCM(myName, token, 3)
                                }
                            }
                        }
                    } else {
                        Timber.tag("SocialViewModel").d("질문 전송 실패")

                        tokenManager.refreshToken(
                            onSuccess = { sendQuestion(myName) },
                            onFailure = { Timber.e("sendQuestion API Call Failed")}
                        )
                    }
                } ?: run {
                    Timber.tag("SocialViewModel").d("questionDto null")
                }
            } catch (e: Exception) {
                Timber.tag("SocialViewModel").e(e.message.toString())
                tokenManager.refreshToken(
                    onSuccess = { sendQuestion(myName) },
                    onFailure = { Timber.e("sendQuestion API Call Failed")}
                )
            }
        }

    }

    // 토큰 재발급 정상 호출 완료
    fun sendFriendRequest(name: String, myName: String) {
        _friendRequest.value = RequestFriend(name = name)
        viewModelScope.launch {
            try {
                _friendRequest.value?.let { name ->
                    val response = repository.postRequest(name)
                    if (response.isSuccess) {
                        Timber.tag("SocialViewModel").d("친구 요청 전송 성공")

                        // 작업이 성공적으로 완료되었음을 표시
                        retrieveTokenFromServer(name.toString())
                        _retrieveToken.value?.let { tokens ->
                            for (token in tokens) {
                                postFCM(myName, token, 1)
                            }
                        }
                        _friendRequestCompleted.postValue(true)
                    } else {
                        Timber.tag("SocialViewModel").d("친구 삭제 실패: ${response.message}")
                        tokenManager.refreshToken(
                            onSuccess = { sendFriendRequest(name.toString(), myName)},
                            onFailure = { Timber.e("sendFriendRequest Failed")}
                        )
                    }
                } ?: run {
                    Timber.tag("SocialViewModel").e("friend Request null")
                }
            } catch (e: Exception) {
                // 오류 처리
                Timber.tag("api 실패!").e(e.message.toString())
                _friendRequestCompleted.postValue(false)
                tokenManager.refreshToken(
                    onSuccess = { sendFriendRequest(name, myName)},
                    onFailure = { Timber.e("sendFriendRequest Failed")}
                )
            }
        }
    }

    fun resetFriendRequest() {
        _friendRequestCompleted.value = false
    }
    fun resetFriendRequestCanceled() {
        _friendRequestCanceled.value = false
    }
    fun resetFriendRequestRemove() {
        _friendRequestRemove.value = false
    }

    fun resetFriendState(){
        Timber.tag("destroy").d(_isFriend.value.toString())
        _isFriend.value = "no"
    }

    // 토큰 재발급 정상 호출 완료
    fun deleteFriend(name: String){
        _friendRequest.value = RequestFriend(name = name)
        viewModelScope.launch {
            try {
                _friendRequest.value?.let { name ->
                    val response = repository.deleteFriendData(name)
                    if (response.isSuccess) {
                        Timber.tag("SocialViewModel").d("친구 삭제 성공")
                        _friendRequestRemove.postValue(true)
                    } else {
                        Timber.tag("SocialViewModel").d("친구 삭제 실패: ${response.message}")
                        tokenManager.refreshToken(
                            onSuccess = { deleteFriend(name.toString()) },
                            onFailure = { Timber.e("deleteFriend APO Call Failed")}
                        )
                    }
                } ?: run {
                    Timber.tag("SocialViewModel").e("friendRequest null")
                }
            } catch (e: Exception) {
                Timber.e("Exception occurred: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { deleteFriend(name) },
                    onFailure = { Timber.e("deleteFriend APO Call Failed")}
                )
            }
        }
    }

    fun postFCM(name: String, token : String, type : Int){
        when(type){
            1 -> _fcm.value = FCM(token = token, title = "친구 요청", body = "${name}님이 친구 요청을 보냈습니다.")
            2 -> _fcm.value = FCM(token = token, title = "친구 수락", body = "${name}님이 친구 요청을 수락했습니다.")
            3 -> _fcm.value = FCM(token = token, title = "질문 전송", body = "${name}님이 질문을 보냈습니다. 확인보세요!")
        }
        viewModelScope.launch {
            _fcm.value?.let {fcm ->
                fcmRepository.postFCM(fcm)
            } ?: run {
                Timber.tag("fcm api 실패!").e("널")
            }
            Timber.tag("fcm api 성공!").d("성공")
        }

    }

    private val _approveSuccess = MutableLiveData<ApprovalResult?>()
    val approveSuccess: LiveData<ApprovalResult?> get() = _approveSuccess

    fun resetApproveSuccess() {
        _approveSuccess.value = ApprovalResult(false, -1, -1) // 초기값으로 설정
    }

    fun patchApproveNotice(name: String, myName: String, alarmId: Int, position: Int) {
        _friendRequest.value = RequestFriend(name = name)
        viewModelScope.launch {
            try {
                _friendRequest.value?.let { request ->
                    val isApproved = repository.patchApproveFriend(request)

                    if (isApproved.isSuccess) {
                        _approveSuccess.postValue(ApprovalResult(true, alarmId, position))
                    } else {
                        _approveSuccess.postValue(ApprovalResult(false, alarmId, position))
                        tokenManager.refreshToken(
                            onSuccess = { patchApprove(name, myName) },
                            onFailure = { Timber.e("patchApprove API call failed") }
                        )
                    }
                } ?: run {
                    Timber.e("Friend request is null")
                    _approveSuccess.postValue(ApprovalResult(false, alarmId, position))
                }

                retrieveTokenFromServer(name)
                _retrieveToken.value?.let { tokens ->
                    for (token in tokens) {
                        postFCM(myName, token, 2)
                    }
                } ?: run {
                    Timber.e("Token retrieval failed")
                    _approveSuccess.postValue(ApprovalResult(false, alarmId, position))
                }
            } catch (e: Exception) {
                Timber.e("Exception occurred: ${e.message}")
                _approveSuccess.postValue(ApprovalResult(false, alarmId, position))
            }
        }
    }

    // 토큰 재발급 정상 호출 완료
    fun patchApprove(name: String, myName: String) {
        _friendRequest.value = RequestFriend(name = name)
        viewModelScope.launch {
            try {
                _friendRequest.value?.let { request ->
                    val response = repository.patchApproveFriend(request)
                    if (response.isSuccess) {
                        _friendRequestCanceled.postValue(true)
                    } else {
                        Timber.tag("SocialViewModel").d("API 호출 실패: ${response.message}")
                        tokenManager.refreshToken(
                            onSuccess = { patchApprove(name, myName) },
                            onFailure = { Timber.e("patchApprove API call failed") }
                        )
                    }
                } ?: run {
                    Timber.e("Friend request is null")
                }

                retrieveTokenFromServer(name)
                _retrieveToken.value?.let { tokens ->
                    for (token in tokens) {
                        postFCM(myName, token, 2)
                    }
                } ?: run {
                }
            } catch (e: Exception) {
                Timber.e("Exception occurred: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { patchApprove(name, myName) },
                    onFailure = { Timber.e("patchApprove API call failed") }
                )
            }
        }
    }

    private suspend fun retrieveTokenFromServer(name: String) {
        resetToken()
        try {
            // IO 스레드에서 네트워크 호출을 처리
            val response = withContext(Dispatchers.IO) {
                fcmRepository.getToken(name)
            }

            if (response.isSuccess) {
                _retrieveToken.value = response.result.tokens
                // 서버에서 받은 토큰을 사용해 로직을 처리
                Timber.tag("Token Retrieval").d(_retrieveToken.value.toString())
            } else {
                Timber.tag("Token Retrieval").d("토큰 조회 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Timber.tag("Token Retrieval").e(e.message.toString())
        }
    }

    fun resetQuestionData() {
        _selectedChar.value = -1
        _nextBtnEnabled.value = false
        _questionDto.value = QuestionDto("", "", "", false, null) // 초기화
        _selectedEmotion.value = 0
        _selectedEmotionMent.value = ""
        _optionList.value = emptyList()
        Timber.tag("fcm").d("호출")

    }

    private fun resetToken(){
        _retrieveToken.value = emptyList()
        Timber.tag("fcm").d("호출")
    }

}

