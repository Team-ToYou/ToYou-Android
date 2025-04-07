package com.toyou.toyouandroid.presentation.fragment.record.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.record.dto.DeleteDiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCard
import com.toyou.toyouandroid.data.record.dto.DiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.PatchDiaryCardResponse
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MyRecordViewModel(
    private val repository: RecordRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

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

                        tokenManager.refreshToken(
                            onSuccess = { loadDiaryCards(year, month) },
                            onFailure = { Timber.e("loadDiaryCards API call failed") }
                        )
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

    fun deleteDiaryCard(cardId: Int) {
        Timber.tag("MyRecordViewModel").d("$cardId")

        viewModelScope.launch {
            val response = repository.deleteDiaryCard(cardId)
            response.enqueue(object : Callback<DeleteDiaryCardResponse> {
                override fun onResponse(
                    call: Call<DeleteDiaryCardResponse>,
                    response: Response<DeleteDiaryCardResponse>
                ) {
                    if (response.isSuccessful) {
                        Timber.tag("MyRecordViewModel").d("API Success: Received $response")
                    } else {
                        // 오류 처리
                        val errorMessage = response.message()
                        handleError(errorMessage)
                        Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")

                        tokenManager.refreshToken(
                            onSuccess = { deleteDiaryCard(cardId) },
                            onFailure = { Timber.e("deleteDiaryCard API call failed") }
                        )
                    }
                }

                override fun onFailure(call: Call<DeleteDiaryCardResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    val errorMessage = t.message ?: "Unknown error"
                    handleError(errorMessage)
                    Timber.tag("MyRecordViewModel").d("Network Failure: $errorMessage")
                }
            })
        }
    }

    fun patchDiaryCard(cardId: Int) {
        Timber.tag("MyRecordViewModel").d("$cardId")

        viewModelScope.launch {
            val response = repository.patchDiaryCard(cardId)
            response.enqueue(object : Callback<PatchDiaryCardResponse> {
                override fun onResponse(
                    call: Call<PatchDiaryCardResponse>,
                    response: Response<PatchDiaryCardResponse>
                ) {
                    if (response.isSuccessful) {
                        Timber.tag("MyRecordViewModel").d("API Success: Received $response")
                    } else {
                        // 오류 처리
                        val errorMessage = response.message()
                        val errorBody = response.errorBody()?.string() // 에러 본문을 읽기
                        Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")
                        Timber.tag("MyRecordViewModel").d("Error Body: $errorBody")

                        handleError(errorMessage)

                        tokenManager.refreshToken(
                            onSuccess = { patchDiaryCard(cardId) },
                            onFailure = { Timber.e("patchDiaryCard API call failed") }
                        )
                    }
                }

                override fun onFailure(call: Call<PatchDiaryCardResponse>, t: Throwable) {
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