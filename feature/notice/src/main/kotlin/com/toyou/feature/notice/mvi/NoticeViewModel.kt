package com.toyou.feature.notice.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.AlarmType
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.repository.INoticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val repository: INoticeRepository,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<NoticeUiState, NoticeEvent, NoticeAction>(
    initialState = NoticeUiState()
) {

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
                when (val result = repository.getNotices()) {
                    is DomainResult.Success -> {
                        val alarmList = result.data
                        Timber.d("AlarmResponse: $alarmList")
                        val items = alarmList.alarms.mapNotNull { alarm ->
                            when (alarm.alarmType) {
                                AlarmType.FRIEND_REQUEST -> NoticeItem.NoticeFriendRequestItem(
                                    alarm.nickname,
                                    alarm.alarmId
                                )
                                AlarmType.REQUEST_ACCEPTED -> NoticeItem.NoticeFriendRequestAcceptedItem(
                                    alarm.nickname,
                                    alarm.alarmId
                                )
                                AlarmType.NEW_QUESTION -> NoticeItem.NoticeCardCheckItem(
                                    alarm.nickname,
                                    alarm.alarmId
                                )
                                else -> null
                            }
                        }.reversed()

                        updateState {
                            copy(
                                generalNotices = items,
                                hasNotifications = items.isNotEmpty(),
                                isLoading = false
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        updateState { copy(isLoading = false) }
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performFetchNotices() },
                            onFailure = { Timber.e("Failed to refresh token and get notices") },
                            tag = "NoticeViewModel"
                        )
                    }
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
                when (val result = repository.getFriendsRequestNotices()) {
                    is DomainResult.Success -> {
                        val friendsRequestList = result.data
                        Timber.d("FriendsRequestResponse: $friendsRequestList")
                        val items = friendsRequestList.requests.map { friendRequest ->
                            NoticeItem.NoticeFriendRequestItem(
                                nickname = friendRequest.nickname,
                                alarmId = friendRequest.userId
                            )
                        }.reversed()

                        updateState {
                            copy(
                                friendRequestNotices = items,
                                hasNotifications = items.isNotEmpty(),
                                isLoading = false
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        updateState { copy(isLoading = false) }
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performFetchFriendsRequestNotices() },
                            onFailure = { Timber.e("Failed to refresh token and get notices") },
                            tag = "NoticeViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendEvent(NoticeEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun performDeleteNotice(alarmId: Int, position: Int, retryCount: Int = 0) {
        viewModelScope.launch {
            try {
                when (val result = repository.deleteNotice(alarmId)) {
                    is DomainResult.Success -> {
                        sendEvent(NoticeEvent.NoticeDeleted(alarmId, position))
                    }
                    is DomainResult.Error -> {
                        errorHandler.handleErrorWithRetry(
                            maxRetries = 5,
                            currentRetry = retryCount,
                            error = result,
                            onRetry = { newRetryCount ->
                                performDeleteNotice(alarmId, position, newRetryCount)
                            },
                            onMaxRetriesExceeded = {
                                sendEvent(NoticeEvent.NoticeDeleteFailed(alarmId, position))
                            },
                            tag = "NoticeViewModel"
                        )
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
