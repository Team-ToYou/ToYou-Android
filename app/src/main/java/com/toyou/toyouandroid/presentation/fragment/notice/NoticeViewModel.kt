package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.model.NoticeItem
import com.toyou.toyouandroid.presentation.fragment.notice.network.AlarmDeleteResponse
import com.toyou.toyouandroid.presentation.fragment.notice.network.AlarmResponse
import com.toyou.toyouandroid.presentation.fragment.notice.network.NoticeRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeViewModel(private val repository: NoticeRepository) : ViewModel() {

    private val _noticeItems = MutableLiveData<List<NoticeItem>?>()
    val noticeItems: MutableLiveData<List<NoticeItem>?> get() = _noticeItems

    fun fetchNotices(userId: Int) {
        viewModelScope.launch {
            val response = repository.getNotices(userId)
            response.enqueue(object : Callback<AlarmResponse> {
                override fun onResponse(
                    call: Call<AlarmResponse>,
                    response: Response<AlarmResponse>
                ) {
                    if (response.isSuccessful) {
                        val alarmResponse = response.body()
                        val items = alarmResponse?.result?.alarmList?.map { alarm ->
                            when (alarm.alarmType) {
                                "FRIEND_REQUEST" -> NoticeItem.NoticeFriendRequestItem(alarm.nickname, alarm.alarmId)
                                "REQUEST_ACCEPTED" -> NoticeItem.NoticeFriendRequestAcceptedItem(alarm.nickname, alarm.alarmId)
                                "NEW_QUESTION" -> NoticeItem.NoticeCardCheckItem(alarm.nickname, alarm.alarmId)
                                else -> null
                            }
                        }?.filterNotNull() ?: emptyList()

                        _noticeItems.value = items
                    }
                }

                override fun onFailure(call: Call<AlarmResponse>, t: Throwable) {
                    // 에러 처리 로직
                }
            })
        }
    }

    fun deleteNotice(userId: Int, alarmId: Int, position: Int) {
        viewModelScope.launch {
            val response = repository.deleteNotice(alarmId, userId)
            response.enqueue(object : Callback<AlarmDeleteResponse> {
                override fun onResponse(
                    call: Call<AlarmDeleteResponse>,
                    response: Response<AlarmDeleteResponse>
                ) {
                    if (response.isSuccessful) {
                        val updatedList = _noticeItems.value?.toMutableList()?.apply {
                            removeAt(position)
                        }
                        _noticeItems.value = updatedList
                    }
                }

                override fun onFailure(call: Call<AlarmDeleteResponse>, t: Throwable) {
                    // 에러 처리 로직
                }
            })
        }
    }
}