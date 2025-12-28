package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.domain.social.repostitory.ISocialRepository
import com.toyou.toyouandroid.fcm.domain.IFCMRepository
import com.toyou.toyouandroid.fcm.dto.request.FCM
import com.toyou.toyouandroid.model.FriendListModel
import com.toyou.toyouandroid.presentation.fragment.notice.ApprovalResult
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val repository: ISocialRepository,
    private val tokenManager: TokenManager,
    private val fcmRepository: IFCMRepository
) : MviViewModel<SocialUiState, SocialEvent, SocialAction>(
    initialState = SocialUiState()
) {
    private var retryCount = 0
    private val maxRetryCount = 1
    private var retrieveTokens: List<String>? = null

    // ============================================================
    // Legacy LiveData for XML Data Binding Compatibility
    // These will be removed after Compose migration
    // ============================================================
    private val _friends = MutableLiveData<List<FriendListModel>>()
    val friends: LiveData<List<FriendListModel>> get() = _friends

    private val _selectedChar = MutableLiveData(-1)
    val selectedChar: LiveData<Int> get() = _selectedChar

    private val _nextBtnEnabled = MutableLiveData(false)
    val nextBtnEnabled: LiveData<Boolean> get() = _nextBtnEnabled

    private val _questionDto = MutableLiveData(QuestionDto(0, "", "", false, null))
    val questionDto: LiveData<QuestionDto> get() = _questionDto

    private val _selectedEmotion = MutableLiveData<Int?>()
    val selectedEmotion: LiveData<Int?> get() = _selectedEmotion

    private val _selectedEmotionMent = MutableLiveData<String>()
    val selectedEmotionMent: LiveData<String> get() = _selectedEmotionMent

    private val _optionList = MutableLiveData<List<String>>()
    val optionList: LiveData<List<String>> get() = _optionList

    private val _isFriend = MutableLiveData<String>()
    val isFriend: LiveData<String> get() = _isFriend

    private val _searchName = MutableLiveData<String>()
    val searchName: LiveData<String> get() = _searchName

    private val _searchFriendId = MutableLiveData<Long>()
    val searchFriendId: LiveData<Long> get() = _searchFriendId

    private val _friendRequestCompleted = MutableLiveData<Boolean>()
    val friendRequestCompleted: LiveData<Boolean> get() = _friendRequestCompleted

    private val _friendRequestCanceled = MutableLiveData<Boolean>()
    val friendRequestCanceled: LiveData<Boolean> get() = _friendRequestCanceled

    private val _friendRequestRemove = MutableLiveData<Boolean>()
    val friendRequestRemove: LiveData<Boolean> get() = _friendRequestRemove

    private val _approveSuccess = MutableLiveData<ApprovalResult?>()
    val approveSuccess: LiveData<ApprovalResult?> get() = _approveSuccess

    init {
        // Sync StateFlow to LiveData for backward compatibility
        state.onEach { uiState ->
            _friends.value = uiState.friends
            _selectedChar.value = uiState.selectedChar
            _nextBtnEnabled.value = uiState.nextBtnEnabled
            _questionDto.value = uiState.questionDto
            _selectedEmotion.value = uiState.selectedEmotion
            _selectedEmotionMent.value = uiState.selectedEmotionMent
            _optionList.value = uiState.optionList
            _isFriend.value = uiState.isFriend
            _searchName.value = uiState.searchName
            _searchFriendId.value = uiState.searchFriendId
        }.launchIn(viewModelScope)

        // Sync Events to LiveData
        event.onEach { event ->
            when (event) {
                is SocialEvent.FriendRequestCompleted -> _friendRequestCompleted.value = true
                is SocialEvent.FriendRequestCanceled -> _friendRequestCanceled.value = true
                is SocialEvent.FriendRequestRemoved -> _friendRequestRemove.value = true
                is SocialEvent.ApprovalSuccess -> _approveSuccess.value = ApprovalResult(true, event.alarmId, event.position)
                is SocialEvent.ApprovalFailed -> _approveSuccess.value = ApprovalResult(false, event.alarmId, event.position)
                else -> { /* handled elsewhere */ }
            }
        }.launchIn(viewModelScope)
    }

    // Legacy reset functions for backward compatibility
    fun resetFriendRequest() { _friendRequestCompleted.value = false }
    fun resetFriendRequestCanceled() { _friendRequestCanceled.value = false }
    fun resetFriendRequestRemove() { _friendRequestRemove.value = false }
    fun resetApproveSuccess() { _approveSuccess.value = ApprovalResult(false, -1, -1) }
    // ============================================================

    override fun handleAction(action: SocialAction) {
        when (action) {
            is SocialAction.LoadFriends -> performLoadFriends()
            is SocialAction.SearchFriend -> performSearchFriend(action.name)
            is SocialAction.SelectCharacter -> performSelectCharacter(action.position)
            is SocialAction.SetTargetFriend -> performSetTargetFriend(action.friendName, action.emotion, action.ment)
            is SocialAction.SetTypeFriend -> performSetTypeFriend(action.type)
            is SocialAction.UpdateQuestionOptions -> performUpdateQuestionOptions(action.options)
            is SocialAction.UpdateOption -> performUpdateOption()
            is SocialAction.RemoveOptions -> performRemoveOptions()
            is SocialAction.RemoveContent -> performRemoveContent()
            is SocialAction.SetAnonymous -> performSetAnonymous(action.isAnonymous)
            is SocialAction.SendQuestion -> performSendQuestion(action.myName)
            is SocialAction.SendFriendRequest -> performSendFriendRequest(action.friendId, action.myName)
            is SocialAction.DeleteFriend -> performDeleteFriend(action.friendName, action.friendId)
            is SocialAction.ApproveNotice -> performApproveNotice(action.name, action.myName, action.alarmId, action.position)
            is SocialAction.PatchApprove -> performPatchApprove(action.friendId, action.myName)
            is SocialAction.ResetFriendRequest -> { /* No-op, handled via events */ }
            is SocialAction.ResetFriendRequestCanceled -> { /* No-op, handled via events */ }
            is SocialAction.ResetFriendRequestRemove -> { /* No-op, handled via events */ }
            is SocialAction.ResetFriendState -> performResetFriendState()
            is SocialAction.ResetQuestionData -> performResetQuestionData()
            is SocialAction.ResetApproveSuccess -> { /* No-op, handled via events */ }
        }
    }

    private fun performLoadFriends() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val response = repository.getFriendsData()
                if (response.isSuccess) {
                    Timber.d("API 호출 성공: ${response.message}")
                    val friendModels = mapToFriendModels(response.result)
                    updateState { copy(friends = friendModels, isLoading = false) }
                } else {
                    Timber.tag("SocialViewModel").e("API 호출 실패: ${response.message}")
                    updateState { copy(isLoading = false) }
                    tokenManager.refreshToken(
                        onSuccess = { performLoadFriends() },
                        onFailure = {
                            Timber.e("getFriendsData API call failed")
                            sendEvent(SocialEvent.TokenExpired)
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("SocialViewModel").e("예외 발생: ${e.message}")
                updateState { copy(isLoading = false) }
                tokenManager.refreshToken(
                    onSuccess = { performLoadFriends() },
                    onFailure = {
                        Timber.e("getFriendsData API call failed")
                        sendEvent(SocialEvent.ShowError(e.message ?: "Unknown error"))
                    }
                )
            }
        }
    }

    private fun performSearchFriend(name: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSearchData(name)
                if (response.isSuccess) {
                    response.result.let { result ->
                        updateState {
                            copy(
                                isFriend = result.status,
                                searchName = result.name,
                                searchFriendId = result.userId
                            )
                        }
                        Timber.tag("search API 성공").d(result.status)
                    }
                    retryCount = 0
                } else {
                    Timber.tag("search API 실패").d("API 호출 실패: ${response.message}")
                    if (retryCount < maxRetryCount) {
                        retryCount++
                        tokenManager.refreshToken(
                            onSuccess = { performSearchFriend(name) },
                            onFailure = { Timber.e("getSearchData API call failed") }
                        )
                    } else {
                        Timber.e("최대 재시도 도달, 추가 호출 중단")
                    }
                }
            } catch (e: HttpException) {
                handleSearchError(e, name)
            } catch (e: Exception) {
                Timber.tag("search API 실패").e("예외 발생: ${e.message}")
                updateState { copy(isFriend = "예상치 못한 오류가 발생했습니다.") }
                if (retryCount < maxRetryCount) {
                    retryCount++
                    tokenManager.refreshToken(
                        onSuccess = { performSearchFriend(name) },
                        onFailure = { Timber.e("getSearchData API call failed") }
                    )
                } else {
                    Timber.e("최대 재시도 도달, 추가 호출 중단")
                    retryCount = 0
                }
            }
        }
    }

    private fun handleSearchError(e: HttpException, name: String) {
        val errorBody = e.response()?.errorBody()?.string()
        Timber.tag("search API 실패").e("서버 응답 메시지: $errorBody")

        if (retryCount < maxRetryCount) {
            retryCount++
            tokenManager.refreshToken(
                onSuccess = { performSearchFriend(name) },
                onFailure = { Timber.e("getSearchData API call failed") }
            )
        } else {
            Timber.e("최대 재시도 도달, 추가 호출 중단")
        }

        val status = when {
            errorBody?.contains("USER400") == true -> "400"
            errorBody?.contains("USER401") == true -> "401"
            else -> "400"
        }
        updateState { copy(isFriend = status) }
    }

    private fun performSelectCharacter(position: Int) {
        val newSelected = if (currentState.selectedChar == position) -1 else position
        updateState {
            copy(
                selectedChar = newSelected,
                nextBtnEnabled = newSelected != -1
            )
        }
    }

    private fun performSetTargetFriend(friendName: String, emotion: Int?, ment: String?) {
        val targetId = currentState.friends.find { it.name == friendName }?.id ?: 0L
        updateState {
            copy(
                questionDto = questionDto.copy(targetId = targetId),
                selectedEmotion = emotion,
                selectedEmotionMent = ment ?: ""
            )
        }
    }

    private fun performSetTypeFriend(type: String) {
        updateState {
            copy(questionDto = questionDto.copy(type = type))
        }
        Timber.tag("타겟2").d(currentState.questionDto.toString())
    }

    private fun performUpdateQuestionOptions(options: List<String>) {
        updateState {
            copy(questionDto = questionDto.copy(options = options))
        }
        Timber.tag("SocialViewModel").d("옵션 업데이트: ${currentState.questionDto}")
    }

    private fun performUpdateOption() {
        currentState.questionDto.options?.let { options ->
            updateState { copy(optionList = options) }
        }
    }

    private fun performRemoveOptions() {
        updateState {
            copy(questionDto = questionDto.copy(options = null))
        }
    }

    private fun performRemoveContent() {
        updateState {
            copy(questionDto = questionDto.copy(content = ""))
        }
    }

    private fun performSetAnonymous(isAnonymous: Boolean) {
        updateState {
            copy(questionDto = questionDto.copy(anonymous = isAnonymous))
        }
    }

    private fun performSendQuestion(myName: String) {
        viewModelScope.launch {
            try {
                val response = repository.postQuestionData(currentState.questionDto)
                if (response.isSuccess) {
                    Timber.tag("SocialViewModel").d("질문 전송 성공")
                    retrieveTokenFromServer(currentState.questionDto.targetId)

                    val senderName = if (currentState.questionDto.anonymous) "익명" else myName
                    retrieveTokens?.let { tokens ->
                        tokens.forEach { token ->
                            postFCM(senderName, token, 3)
                        }
                    }
                    sendEvent(SocialEvent.QuestionSent)
                } else {
                    Timber.tag("SocialViewModel").d("질문 전송 실패")
                    tokenManager.refreshToken(
                        onSuccess = { performSendQuestion(myName) },
                        onFailure = { Timber.e("sendQuestion API Call Failed") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("SocialViewModel").e(e.message.toString())
                tokenManager.refreshToken(
                    onSuccess = { performSendQuestion(myName) },
                    onFailure = { Timber.e("sendQuestion API Call Failed") }
                )
            }
        }
    }

    private fun performSendFriendRequest(friendId: Long, myName: String) {
        val request = RequestFriend(userId = friendId)
        viewModelScope.launch {
            try {
                val response = repository.postRequest(request)
                if (response.isSuccess) {
                    Timber.tag("SocialViewModel").d("친구 요청 전송 성공")
                    retrieveTokenFromServer(friendId)
                    retrieveTokens?.let { tokens ->
                        tokens.forEach { token ->
                            postFCM(myName, token, 1)
                        }
                    }
                    sendEvent(SocialEvent.FriendRequestCompleted)
                } else {
                    Timber.tag("SocialViewModel").d("친구 요청 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { performSendFriendRequest(friendId, myName) },
                        onFailure = { Timber.e("sendFriendRequest Failed") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("api 실패!").e(e.message.toString())
                sendEvent(SocialEvent.ShowError(e.message ?: "친구 요청 실패"))
                tokenManager.refreshToken(
                    onSuccess = { performSendFriendRequest(friendId, myName) },
                    onFailure = { Timber.e("sendFriendRequest Failed") }
                )
            }
        }
    }

    private fun performDeleteFriend(friendName: String?, friendId: Long?) {
        val targetId = when {
            friendId != null && friendId > 0L -> friendId
            !friendName.isNullOrBlank() -> currentState.friends.find { it.name == friendName }?.id ?: 0L
            else -> 0L
        }

        val request = RequestFriend(userId = targetId)
        viewModelScope.launch {
            try {
                val response = repository.deleteFriendData(request)
                if (response.isSuccess) {
                    Timber.tag("SocialViewModel").d("친구 삭제 성공")
                    sendEvent(SocialEvent.FriendRequestRemoved)
                    retryCount = 0
                } else {
                    Timber.tag("SocialViewModel").d("친구 삭제 실패: ${response.message}")
                    if (retryCount < maxRetryCount) {
                        retryCount++
                        tokenManager.refreshToken(
                            onSuccess = { performDeleteFriend(friendName, friendId) },
                            onFailure = { Timber.e("deleteFriend API Call Failed") }
                        )
                    } else {
                        Timber.e("최대 재시도 도달, 추가 호출 중단")
                    }
                }
            } catch (e: Exception) {
                Timber.e("Exception occurred: ${e.message}")
                if (retryCount < maxRetryCount) {
                    retryCount++
                    tokenManager.refreshToken(
                        onSuccess = { performDeleteFriend(friendName, friendId) },
                        onFailure = { Timber.e("deleteFriend API Call Failed") }
                    )
                } else {
                    Timber.e("최대 재시도 도달, 추가 호출 중단")
                    retryCount = 0
                }
            }
        }
    }

    private fun performApproveNotice(name: String, myName: String, alarmId: Int, position: Int) {
        val request = RequestFriend(userId = alarmId.toLong())
        viewModelScope.launch {
            try {
                val response = repository.patchApproveFriend(request)
                if (response.isSuccess) {
                    sendEvent(SocialEvent.ApprovalSuccess(alarmId, position))
                } else {
                    sendEvent(SocialEvent.ApprovalFailed(alarmId, position))
                    tokenManager.refreshToken(
                        onSuccess = { performPatchApprove(alarmId.toLong(), myName) },
                        onFailure = { Timber.e("patchApprove API call failed") }
                    )
                }

                retrieveTokenFromServer(0L)
                retrieveTokens?.let { tokens ->
                    tokens.forEach { token ->
                        postFCM(myName, token, 2)
                    }
                } ?: run {
                    Timber.e("Token retrieval failed")
                    sendEvent(SocialEvent.ApprovalFailed(alarmId, position))
                }
            } catch (e: Exception) {
                Timber.e("Exception occurred: ${e.message}")
                sendEvent(SocialEvent.ApprovalFailed(alarmId, position))
            }
        }
    }

    private fun performPatchApprove(friendId: Long, myName: String) {
        val request = RequestFriend(friendId)
        viewModelScope.launch {
            try {
                val response = repository.patchApproveFriend(request)
                if (response.isSuccess) {
                    sendEvent(SocialEvent.FriendRequestCanceled)
                } else {
                    Timber.tag("SocialViewModel").d("API 호출 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { performPatchApprove(friendId, myName) },
                        onFailure = { Timber.e("patchApprove API call failed") }
                    )
                }

                retrieveTokenFromServer(friendId)
                retrieveTokens?.let { tokens ->
                    tokens.forEach { token ->
                        postFCM(myName, token, 2)
                    }
                }
            } catch (e: Exception) {
                Timber.e("Exception occurred: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { performPatchApprove(friendId, myName) },
                    onFailure = { Timber.e("patchApprove API call failed") }
                )
            }
        }
    }

    private fun postFCM(name: String, token: String, type: Int, retryCount: Int = 0) {
        val maxRetries = 5
        val fcm = when (type) {
            1 -> FCM(token = token, title = "친구 요청", body = "${name}님이 친구 요청을 보냈습니다.")
            2 -> FCM(token = token, title = "친구 수락", body = "${name}님이 친구 요청을 수락했습니다.")
            3 -> FCM(token = token, title = "질문 전송", body = "${name}님이 질문을 보냈습니다. 확인보세요!")
            else -> null
        } ?: return

        viewModelScope.launch {
            try {
                fcmRepository.postFCM(fcm)
                Timber.tag("socialViewModel postFCM").d("성공")
            } catch (e: Exception) {
                Timber.tag("socialViewModel postFCM").e("Exception occurred: ${e.message}")
                if (retryCount < maxRetries) {
                    Timber.tag("socialViewModel postFCM").d("Retrying... (${retryCount + 1}/$maxRetries)")
                    postFCM(name, token, type, retryCount + 1)
                } else {
                    Timber.tag("socialViewModel postFCM").e("Max retry attempts reached.")
                }
            }
        }
    }

    private suspend fun retrieveTokenFromServer(id: Long) {
        retrieveTokens = null
        try {
            val response = withContext(Dispatchers.IO) {
                fcmRepository.getToken(id)
            }

            if (response.isSuccess) {
                retrieveTokens = response.result.tokens
                Timber.tag("Token Retrieval").d(retrieveTokens.toString())
                retryCount = 0
            } else {
                Timber.tag("Token Retrieval").d("토큰 조회 실패: ${response.message}")
                if (retryCount < maxRetryCount) {
                    retryCount++
                    tokenManager.refreshToken(
                        onSuccess = { viewModelScope.launch { retrieveTokenFromServer(id) } },
                        onFailure = { Timber.e("Token Retrieval failed - Refresh token failed") }
                    )
                } else {
                    Timber.tag("Token Retrieval").e("최대 재시도 도달, 추가 호출 중단")
                }
            }
        } catch (e: Exception) {
            Timber.tag("Token Retrieval").e("Exception occurred: ${e.message}")
            if (retryCount < maxRetryCount) {
                retryCount++
                tokenManager.refreshToken(
                    onSuccess = { viewModelScope.launch { retrieveTokenFromServer(id) } },
                    onFailure = { Timber.e("Token Retrieval failed - Refresh token failed") }
                )
            } else {
                Timber.tag("Token Retrieval").e("최대 재시도 도달, 추가 호출 중단")
            }
        }
    }

    private fun performResetFriendState() {
        Timber.tag("destroy").d(currentState.isFriend)
        updateState { copy(isFriend = "no") }
    }

    private fun performResetQuestionData() {
        updateState {
            copy(
                selectedChar = -1,
                nextBtnEnabled = false,
                questionDto = QuestionDto(0, "", "", false, null),
                selectedEmotion = null,
                selectedEmotionMent = "",
                optionList = emptyList()
            )
        }
    }

    private fun mapToFriendModels(friendsDto: FriendsDto): List<FriendListModel> {
        return friendsDto.friends.map { friend ->
            FriendListModel(
                id = friend.userId,
                name = friend.nickname,
                message = friend.ment ?: "",
                emotion = emotionType(friend.emotion)
            )
        }
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

    // ============================================================
    // Legacy public functions for gradual migration
    // These wrap MVI actions for backward compatibility
    // ============================================================
    fun getAnswerLength(answer: String): Int = answer.length

    fun getFriendsData() = onAction(SocialAction.LoadFriends)
    fun getSearchData(name: String) = onAction(SocialAction.SearchFriend(name))
    fun onCharSelected(position: Int) = onAction(SocialAction.SelectCharacter(position))
    fun setTargetFriend(friendName: String, emotion: Int?, ment: String?) =
        onAction(SocialAction.SetTargetFriend(friendName, emotion, ment))
    fun setTypeFriend(type: String) = onAction(SocialAction.SetTypeFriend(type))
    fun updateQuestionOptions(newOptions: List<String>) = onAction(SocialAction.UpdateQuestionOptions(newOptions))
    fun updateOption() = onAction(SocialAction.UpdateOption)
    fun removeOptions() = onAction(SocialAction.RemoveOptions)
    fun removeContent() = onAction(SocialAction.RemoveContent)
    fun isAnonymous(isChecked: Boolean) = onAction(SocialAction.SetAnonymous(isChecked))
    fun sendQuestion(myName: String) = onAction(SocialAction.SendQuestion(myName))
    fun sendFriendRequest(friendId: Long, myName: String) = onAction(SocialAction.SendFriendRequest(friendId, myName))
    fun deleteFriend(friendName: String?, friendId: Long?) = onAction(SocialAction.DeleteFriend(friendName, friendId))
    fun patchApproveNotice(name: String, myName: String, alarmId: Int, position: Int) =
        onAction(SocialAction.ApproveNotice(name, myName, alarmId, position))
    fun patchApprove(friendId: Long, myName: String) = onAction(SocialAction.PatchApprove(friendId, myName))
    fun resetFriendState() = onAction(SocialAction.ResetFriendState)
    fun resetQuestionData() = onAction(SocialAction.ResetQuestionData)
}
