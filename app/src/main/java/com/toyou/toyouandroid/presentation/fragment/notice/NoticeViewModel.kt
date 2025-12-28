package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.domain.notice.INoticeRepository
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val repository: INoticeRepository,
    private val tokenManager: TokenManager
) : MviViewModel<NoticeUiState, NoticeEvent, NoticeAction>(
    initialState = NoticeUiState()
) {

    private val _noticeItems = MutableLiveData<List<NoticeItem>>(emptyList())
    val noticeItems: LiveData<List<NoticeItem>> get() = _noticeItems

    private val _generalNotices = MutableLiveData<List<NoticeItem>>(emptyList())
    val generalNotices: LiveData<List<NoticeItem>> get() = _generalNotices

    private val _friendRequestNotices = MutableLiveData<List<NoticeItem.NoticeFriendRequestItem>>(emptyList())
    val friendRequestNotices: LiveData<List<NoticeItem.NoticeFriendRequestItem>> get() = _friendRequestNotices

    private val _hasNotifications = MutableLiveData<Boolean>()
    val hasNotifications: LiveData<Boolean> get() = _hasNotifications

    init {
        state.onEach { uiState ->
            _noticeItems.value = uiState.noticeItems
            _generalNotices.value = uiState.generalNotices
            _friendRequestNotices.value = uiState.friendRequestNotices
            _hasNotifications.value = uiState.hasNotifications
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: NoticeAction) {
        when (action) {
            is NoticeAction.FetchNotices -> performFetchNotices()
            is NoticeAction.FetchFriendsRequestNotices -> performFetchFriendsRequestNotices()
            is NoticeAction.DeleteNotice -> performDeleteNotice(action.alarmId, action.position)
        }
    }

    private fun performFetchNotices() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val response = repository.getNotices()
                if (response.isSuccessful) {
                    val alarmResponse = response.body()
                    Timber.d("AlarmResponse: $alarmResponse")
                    val items = alarmResponse?.result?.alarmList?.mapNotNull { alarm ->
                        when (alarm.alarmType) {
                            "FRIEND_REQUEST" -> NoticeItem.NoticeFriendRequestItem(
                                alarm.nickname,
                                alarm.alarmId
                            )
                            "REQUEST_ACCEPTED" -> NoticeItem.NoticeFriendRequestAcceptedItem(
                                alarm.nickname,
                                alarm.alarmId
                            )
                            "NEW_QUESTION" -> NoticeItem.NoticeCardCheckItem(
                                alarm.nickname,
                                alarm.alarmId
                            )
                            else -> null
                        }
                    }?.reversed() ?: emptyList()

                    updateState {
                        copy(
                            generalNotices = items,
                            hasNotifications = items.isNotEmpty(),
                            isLoading = false
                        )
                    }
                } else {
                    updateState { copy(isLoading = false) }
                    tokenManager.refreshToken(
                        onSuccess = { performFetchNotices() },
                        onFailure = { Timber.e("Failed to refresh token and get notices") }
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendEvent(NoticeEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun performFetchFriendsRequestNotices() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val response = repository.getFriendsRequestNotices()
                if (response.isSuccessful) {
                    val friendsRequestResponse = response.body()
                    Timber.d("FriendsRequestResponse: $friendsRequestResponse")
                    val items = friendsRequestResponse?.result?.friendsRequestList?.map { friendRequest ->
                        NoticeItem.NoticeFriendRequestItem(
                            nickname = friendRequest.nickname,
                            alarmId = friendRequest.userId
                        )
                    }?.reversed() ?: emptyList()

                    updateState {
                        copy(
                            friendRequestNotices = items,
                            hasNotifications = items.isNotEmpty(),
                            isLoading = false
                        )
                    }
                } else {
                    updateState { copy(isLoading = false) }
                    tokenManager.refreshToken(
                        onSuccess = { performFetchFriendsRequestNotices() },
                        onFailure = { Timber.e("Failed to refresh token and get notices") }
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendEvent(NoticeEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun performDeleteNotice(alarmId: Int, position: Int, retryCount: Int = 0) {
        val maxRetries = 5

        viewModelScope.launch {
            try {
                val response = repository.deleteNotice(alarmId)
                if (response.isSuccessful) {
                    sendEvent(NoticeEvent.NoticeDeleted(alarmId, position))
                } else {
                    if (retryCount < maxRetries) {
                        tokenManager.refreshToken(
                            onSuccess = {
                                Timber.d("Retrying token refresh... ($retryCount/$maxRetries)")
                                performDeleteNotice(alarmId, position, retryCount + 1)
                            },
                            onFailure = {
                                Timber.e("Failed to refresh token after $retryCount retries")
                                sendEvent(NoticeEvent.NoticeDeleteFailed(alarmId, position))
                            }
                        )
                    } else {
                        Timber.e("Max token refresh retries reached ($maxRetries)")
                        sendEvent(NoticeEvent.NoticeDeleteFailed(alarmId, position))
                    }
                }
            } catch (e: Exception) {
                Timber.e("Delete notice API call failed: ${e.message}")
                sendEvent(NoticeEvent.NoticeDeleteFailed(alarmId, position))
            }
        }
    }

    fun fetchNotices() = onAction(NoticeAction.FetchNotices)
    fun fetchFriendsRequestNotices() = onAction(NoticeAction.FetchFriendsRequestNotices)
    fun deleteNotice(alarmId: Int, position: Int) = onAction(NoticeAction.DeleteNotice(alarmId, position))
}
