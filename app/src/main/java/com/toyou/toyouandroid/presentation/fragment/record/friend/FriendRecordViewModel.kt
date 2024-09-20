package com.toyou.toyouandroid.presentation.fragment.record.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.presentation.fragment.record.network.DiaryCardNum
import com.toyou.toyouandroid.presentation.fragment.record.network.DiaryCardNumResponse
import com.toyou.toyouandroid.presentation.fragment.record.network.DiaryCardPerDay
import com.toyou.toyouandroid.presentation.fragment.record.network.DiaryCardPerDayResponse
import com.toyou.toyouandroid.presentation.fragment.record.network.RecordRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class FriendRecordViewModel(private val repository: RecordRepository): ViewModel() {

    private val _diaryCardsNum = MutableLiveData<List<DiaryCardNum>>()
    val diaryCardsNum: LiveData<List<DiaryCardNum>> get() = _diaryCardsNum

    fun loadDiaryCardsNum(year: Int, month: Int) {
        Timber.tag("FriendRecordViewModel").d("loadDiaryCards called with year: $year, month: $month")

        viewModelScope.launch {
            val response = repository.getFriendRecordNum(year, month)
            response.enqueue(object : Callback<DiaryCardNumResponse> {
                override fun onResponse(
                    call: Call<DiaryCardNumResponse>,
                    response: Response<DiaryCardNumResponse>
                ) {
                    if (response.isSuccessful) {
                        val cardList = response.body()?.result?.cardList ?: emptyList()
                        _diaryCardsNum.value = cardList
                        Timber.tag("FriendRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                        Timber.tag("FriendRecordViewModel").d("API Success: Received $cardList")

                    } else {
                        // 오류 처리
                        val errorMessage = response.message()
                        handleError(errorMessage)
                        Timber.tag("FriendRecordViewModel").d("API Error: $errorMessage")

                    }
                }

                override fun onFailure(call: Call<DiaryCardNumResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    val errorMessage = t.message ?: "Unknown error"
                    handleError(errorMessage)
                    Timber.tag("FriendRecordViewModel").d("Network Failure: $errorMessage")
                }
            })
        }
    }

    private val _diaryCardPerDay = MutableLiveData<List<DiaryCardPerDay>>()
    val diaryCardPerDay: LiveData<List<DiaryCardPerDay>> get() = _diaryCardPerDay

    fun loadDiaryCardPerDay(year: Int, month: Int, day: Int) {
        Timber.tag("FriendRecordViewModel").d("loadDiaryCards called with year: $year, month: $month")

        viewModelScope.launch {
            val response = repository.getFriendRecordPerDay(year, month, day)
            response.enqueue(object : Callback<DiaryCardPerDayResponse> {
                override fun onResponse(
                    call: Call<DiaryCardPerDayResponse>,
                    response: Response<DiaryCardPerDayResponse>
                ) {
                    if (response.isSuccessful) {
                        val cardList = response.body()?.result?.cardList ?: emptyList()
                        _diaryCardPerDay.value = cardList
                        Timber.tag("FriendRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                        Timber.tag("FriendRecordViewModel").d("API Success: Received $cardList")

                    } else {
                        // 오류 처리
                        val errorMessage = response.message()
                        handleError(errorMessage)
                        Timber.tag("FriendRecordViewModel").d("API Error: $errorMessage")

                    }
                }

                override fun onFailure(call: Call<DiaryCardPerDayResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    val errorMessage = t.message ?: "Unknown error"
                    handleError(errorMessage)
                    Timber.tag("FriendRecordViewModel").d("Network Failure: $errorMessage")
                }
            })
        }
    }

    private fun handleError(message: String) {
        Timber.tag("MyRecordViewModel").d("Error: $message")
        // 오류 메시지 처리 로직 (예: 사용자에게 알림, UI 업데이트 등)
    }
}