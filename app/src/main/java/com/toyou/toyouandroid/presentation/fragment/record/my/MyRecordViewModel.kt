package com.toyou.toyouandroid.presentation.fragment.record.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.presentation.fragment.record.network.DiaryCard
import com.toyou.toyouandroid.presentation.fragment.record.network.DiaryCardResponse
import com.toyou.toyouandroid.presentation.fragment.record.network.RecordRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MyRecordViewModel(private val repository: RecordRepository) : ViewModel() {

    private val _diaryCards = MutableLiveData<List<DiaryCard>>()
    val diaryCards: LiveData<List<DiaryCard>> get() = _diaryCards

    fun loadDiaryCards(year: Int, month: Int) {
        Timber.tag("MyRecordViewModel").d("loadDiaryCards called with year: $year, month: $month")

        viewModelScope.launch {
            val response = repository.getMyRecord(year, month)
            response.enqueue(object : Callback<DiaryCardResponse> {
                override fun onResponse(
                    call: Call<DiaryCardResponse>,
                    response: Response<DiaryCardResponse>
                ) {
                    if (response.isSuccessful) {
                        val cardList = response.body()?.result?.cardList ?: emptyList()
                        _diaryCards.value = cardList
                        Timber.tag("MyRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                        Timber.tag("MyRecordViewModel").d("API Success: Received $cardList")

                    } else {
                        // 오류 처리
                        val errorMessage = response.message()
                        handleError(errorMessage)
                        Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")

                    }
                }

                override fun onFailure(call: Call<DiaryCardResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    val errorMessage = t.message ?: "Unknown error"
                    handleError(errorMessage)
                    Timber.tag("MyRecordViewModel").d("Network Failure: $errorMessage")
                }
            })
        }
    }

    private fun handleError(message: String) {
        Timber.tag("MyRecordViewModel").d("Error: $message")
        // 오류 메시지 처리 로직 (예: 사용자에게 알림, UI 업데이트 등)
    }
}