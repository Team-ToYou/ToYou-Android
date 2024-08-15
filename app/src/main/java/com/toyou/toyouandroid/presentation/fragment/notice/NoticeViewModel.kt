package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.model.NoticeItem
import com.toyou.toyouandroid.presentation.fragment.notice.network.AlarmResponse
import com.toyou.toyouandroid.presentation.fragment.notice.network.NoticeRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeViewModel(private val repository: NoticeRepository) : ViewModel() {

    private val _noticeItems = MutableLiveData<List<NoticeItem>>()
    val noticeItems: LiveData<List<NoticeItem>> get() = _noticeItems

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
                                "FRIEND_REQUEST" -> NoticeItem.NoticeFriendRequestItem(alarm.nickname)
                                "REQUEST_ACCEPTED" -> NoticeItem.NoticeFriendRequestAcceptedItem(alarm.nickname)
                                "NEW_QUESTION" -> NoticeItem.NoticeCardCheckItem(alarm.nickname)
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
}