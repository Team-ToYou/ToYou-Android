package com.toyou.feature.social.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FriendListModel
import com.toyou.core.domain.model.FriendsList
import com.toyou.core.domain.model.PushNotification
import com.toyou.core.domain.repository.IFCMRepository
import com.toyou.core.domain.repository.ISocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val repository: ISocialRepository,
    private val errorHandler: ApiErrorHandler,
    private val fcmRepository: IFCMRepository
) : MviViewModel<SocialUiState, SocialEvent, SocialAction>(
    initialState = SocialUiState()
) {
    private var retryCount = 0
    private val maxRetryCount = 1
    private var retrieveTokens: List<String>? = null

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
            when (val result = repository.getFriendsData()) {
                is DomainResult.Success -> {
                    Timber.d("API 호출 성공")
                    val friendModels = mapToFriendModels(result.data)
                    updateState { copy(friends = friendModels, isLoading = false) }
                }
                is DomainResult.Error -> {
                    updateState { copy(isLoading = false) }
                    errorHandler.handleError(
                        error = result,
                        onRetry = { performLoadFriends() },
                        onFailure = {
                            sendEvent(SocialEvent.TokenExpired)
                        },
                        tag = "SocialViewModel"
                    )
                }
            }
        }
    }

    private fun performSearchFriend(name: String) {
        viewModelScope.launch {
            when (val result = repository.getSearchData(name)) {
                is DomainResult.Success -> {
                    val searchResult = result.data
                    updateState {
                        copy(
                            isFriend = searchResult.status.name,
                            searchName = searchResult.name,
                            searchFriendId = searchResult.userId
                        )
                    }
                    Timber.tag("search API 성공").d(searchResult.status.name)
                    retryCount = 0
                }
                is DomainResult.Error -> {
                    Timber.tag("search API 실패").d("API 호출 실패: ${result.message}")
                    val status = when {
                        result.message.contains("USER400") -> "400"
                        result.message.contains("USER401") -> "401"
                        else -> "400"
                    }
                    updateState { copy(isFriend = status) }
                    errorHandler.handleErrorWithRetry(
                        maxRetries = maxRetryCount,
                        currentRetry = retryCount,
                        error = result,
                        onRetry = { newRetryCount ->
                            retryCount = newRetryCount
                            performSearchFriend(name)
                        },
                        onMaxRetriesExceeded = {
                            retryCount = 0
                        },
                        tag = "SocialViewModel"
                    )
                }
            }
        }
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
                questionForm = questionForm.copy(targetId = targetId),
                selectedEmotion = emotion,
                selectedEmotionMent = ment ?: ""
            )
        }
    }

    private fun performSetTypeFriend(type: String) {
        updateState {
            copy(questionForm = questionForm.copy(type = type))
        }
        Timber.tag("타겟2").d(currentState.questionForm.toString())
    }

    private fun performUpdateQuestionOptions(options: List<String>) {
        updateState {
            copy(questionForm = questionForm.copy(options = options))
        }
        Timber.tag("SocialViewModel").d("옵션 업데이트: ${currentState.questionForm}")
    }

    private fun performUpdateOption() {
        currentState.questionForm.options?.let { options ->
            updateState { copy(optionList = options) }
        }
    }

    private fun performRemoveOptions() {
        updateState {
            copy(questionForm = questionForm.copy(options = null))
        }
    }

    private fun performRemoveContent() {
        updateState {
            copy(questionForm = questionForm.copy(content = ""))
        }
    }

    private fun performSetAnonymous(isAnonymous: Boolean) {
        updateState {
            copy(questionForm = questionForm.copy(anonymous = isAnonymous))
        }
    }

    private fun performSendQuestion(myName: String) {
        viewModelScope.launch {
            val questionRequest = currentState.questionForm.toQuestionRequest()
            when (val result = repository.postQuestionData(questionRequest)) {
                is DomainResult.Success -> {
                    Timber.tag("SocialViewModel").d("질문 전송 성공")
                    retrieveTokenFromServer(currentState.questionForm.targetId)

                    val senderName = if (currentState.questionForm.anonymous) "익명" else myName
                    retrieveTokens?.let { tokens ->
                        tokens.forEach { token ->
                            postFCM(senderName, token, 3)
                        }
                    }
                    sendEvent(SocialEvent.QuestionSent)
                }
                is DomainResult.Error -> {
                    errorHandler.handleError(
                        error = result,
                        onRetry = { performSendQuestion(myName) },
                        onFailure = { Timber.e("sendQuestion API Call Failed") },
                        tag = "SocialViewModel"
                    )
                }
            }
        }
    }

    private fun performSendFriendRequest(friendId: Long, myName: String) {
        viewModelScope.launch {
            when (val result = repository.postRequest(friendId)) {
                is DomainResult.Success -> {
                    Timber.tag("SocialViewModel").d("친구 요청 전송 성공")
                    retrieveTokenFromServer(friendId)
                    retrieveTokens?.let { tokens ->
                        tokens.forEach { token ->
                            postFCM(myName, token, 1)
                        }
                    }
                    sendEvent(SocialEvent.FriendRequestCompleted)
                }
                is DomainResult.Error -> {
                    sendEvent(SocialEvent.ShowError(result.message))
                    errorHandler.handleError(
                        error = result,
                        onRetry = { performSendFriendRequest(friendId, myName) },
                        onFailure = { Timber.e("sendFriendRequest Failed") },
                        tag = "SocialViewModel"
                    )
                }
            }
        }
    }

    private fun performDeleteFriend(friendName: String?, friendId: Long?) {
        val targetId = when {
            friendId != null && friendId > 0L -> friendId
            !friendName.isNullOrBlank() -> currentState.friends.find { it.name == friendName }?.id ?: 0L
            else -> 0L
        }

        viewModelScope.launch {
            when (val result = repository.deleteFriendData(targetId)) {
                is DomainResult.Success -> {
                    Timber.tag("SocialViewModel").d("친구 삭제 성공")
                    sendEvent(SocialEvent.FriendRequestRemoved)
                    retryCount = 0
                }
                is DomainResult.Error -> {
                    Timber.tag("SocialViewModel").d("친구 삭제 실패: ${result.message}")
                    errorHandler.handleErrorWithRetry(
                        maxRetries = maxRetryCount,
                        currentRetry = retryCount,
                        error = result,
                        onRetry = { newRetryCount ->
                            retryCount = newRetryCount
                            performDeleteFriend(friendName, friendId)
                        },
                        onMaxRetriesExceeded = {
                            retryCount = 0
                        },
                        tag = "SocialViewModel"
                    )
                }
            }
        }
    }

    private fun performApproveNotice(name: String, myName: String, alarmId: Int, position: Int) {
        viewModelScope.launch {
            when (val result = repository.patchApproveFriend(alarmId.toLong())) {
                is DomainResult.Success -> {
                    sendEvent(SocialEvent.ApprovalSuccess(alarmId, position))
                    retrieveTokenFromServer(0L)
                    retrieveTokens?.let { tokens ->
                        tokens.forEach { token ->
                            postFCM(myName, token, 2)
                        }
                    } ?: run {
                        Timber.e("Token retrieval failed")
                    }
                }
                is DomainResult.Error -> {
                    sendEvent(SocialEvent.ApprovalFailed(alarmId, position))
                    errorHandler.handleError(
                        error = result,
                        onRetry = { performPatchApprove(alarmId.toLong(), myName) },
                        onFailure = { Timber.e("patchApprove API call failed") },
                        tag = "SocialViewModel"
                    )
                }
            }
        }
    }

    private fun performPatchApprove(friendId: Long, myName: String) {
        viewModelScope.launch {
            when (val result = repository.patchApproveFriend(friendId)) {
                is DomainResult.Success -> {
                    sendEvent(SocialEvent.FriendRequestCanceled)
                    retrieveTokenFromServer(friendId)
                    retrieveTokens?.let { tokens ->
                        tokens.forEach { token ->
                            postFCM(myName, token, 2)
                        }
                    }
                }
                is DomainResult.Error -> {
                    errorHandler.handleError(
                        error = result,
                        onRetry = { performPatchApprove(friendId, myName) },
                        onFailure = { Timber.e("patchApprove API call failed") },
                        tag = "SocialViewModel"
                    )
                }
            }
        }
    }

    private fun postFCM(name: String, token: String, type: Int, retryCount: Int = 0) {
        val maxRetries = 5
        val notification = when (type) {
            1 -> PushNotification(token = token, title = "친구 요청", body = "${name}님이 친구 요청을 보냈습니다.")
            2 -> PushNotification(token = token, title = "친구 수락", body = "${name}님이 친구 요청을 수락했습니다.")
            3 -> PushNotification(token = token, title = "질문 전송", body = "${name}님이 질문을 보냈습니다. 확인보세요!")
            else -> null
        } ?: return

        viewModelScope.launch {
            when (val result = fcmRepository.postFCM(notification)) {
                is DomainResult.Success -> {
                    Timber.tag("socialViewModel postFCM").d("성공")
                }
                is DomainResult.Error -> {
                    Timber.tag("socialViewModel postFCM").e("Failed: ${result.message}")
                    if (retryCount < maxRetries) {
                        Timber.tag("socialViewModel postFCM").d("Retrying... (${retryCount + 1}/$maxRetries)")
                        postFCM(name, token, type, retryCount + 1)
                    } else {
                        Timber.tag("socialViewModel postFCM").e("Max retry attempts reached.")
                    }
                }
            }
        }
    }

    private suspend fun retrieveTokenFromServer(id: Long) {
        retrieveTokens = null
        val result = withContext(Dispatchers.IO) {
            fcmRepository.getToken(id)
        }

        when (result) {
            is DomainResult.Success -> {
                retrieveTokens = result.data.tokens
                Timber.tag("Token Retrieval").d(retrieveTokens.toString())
                retryCount = 0
            }
            is DomainResult.Error -> {
                Timber.tag("Token Retrieval").d("토큰 조회 실패: ${result.message}")
                errorHandler.handleErrorWithRetry(
                    maxRetries = maxRetryCount,
                    currentRetry = retryCount,
                    error = result,
                    onRetry = { newRetryCount ->
                        retryCount = newRetryCount
                        viewModelScope.launch { retrieveTokenFromServer(id) }
                    },
                    onMaxRetriesExceeded = {
                        retryCount = 0
                    },
                    tag = "SocialViewModel-TokenRetrieval"
                )
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
                questionForm = QuestionFormState(),
                selectedEmotion = null,
                selectedEmotionMent = "",
                optionList = emptyList()
            )
        }
    }

    private fun mapToFriendModels(friendsList: FriendsList): List<FriendListModel> {
        return friendsList.friends.map { friend ->
            FriendListModel(
                id = friend.id,
                name = friend.nickname,
                message = friend.message ?: "",
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
