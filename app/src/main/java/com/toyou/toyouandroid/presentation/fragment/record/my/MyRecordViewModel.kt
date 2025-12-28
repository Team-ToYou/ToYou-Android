package com.toyou.toyouandroid.presentation.fragment.record.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.data.record.dto.DiaryCard
import com.toyou.toyouandroid.domain.record.IRecordRepository
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyRecordViewModel @Inject constructor(
    private val repository: IRecordRepository,
    private val tokenManager: TokenManager
) : MviViewModel<MyRecordUiState, MyRecordEvent, MyRecordAction>(
    initialState = MyRecordUiState()
) {

    private val _diaryCards = MutableLiveData<List<DiaryCard>>()
    val diaryCards: LiveData<List<DiaryCard>> get() = _diaryCards

    init {
        state.onEach { uiState ->
            _diaryCards.value = uiState.diaryCards
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: MyRecordAction) {
        when (action) {
            is MyRecordAction.LoadDiaryCards -> performLoadDiaryCards(action.year, action.month)
            is MyRecordAction.DeleteDiaryCard -> performDeleteDiaryCard(action.cardId)
            is MyRecordAction.PatchDiaryCard -> performPatchDiaryCard(action.cardId)
        }
    }

    private fun performLoadDiaryCards(year: Int, month: Int) {
        Timber.tag("MyRecordViewModel").d("loadDiaryCards called with year: $year, month: $month")

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val response = repository.getMyRecord(year, month)
                if (response.isSuccessful) {
                    val cardList = response.body()?.result?.cardList ?: emptyList()
                    updateState { copy(diaryCards = cardList, isLoading = false) }
                    Timber.tag("MyRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                    Timber.tag("MyRecordViewModel").d("API Success: Received $cardList")
                } else {
                    val errorMessage = response.message()
                    updateState { copy(isLoading = false) }
                    sendEvent(MyRecordEvent.ShowError(errorMessage))
                    Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")

                    tokenManager.refreshToken(
                        onSuccess = { performLoadDiaryCards(year, month) },
                        onFailure = { Timber.e("loadDiaryCards API call failed") }
                    )
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                updateState { copy(isLoading = false) }
                sendEvent(MyRecordEvent.ShowError(errorMessage))
                Timber.tag("MyRecordViewModel").d("Network Failure: $errorMessage")
            }
        }
    }

    private fun performDeleteDiaryCard(cardId: Int) {
        Timber.tag("MyRecordViewModel").d("$cardId")

        viewModelScope.launch {
            try {
                val response = repository.deleteDiaryCard(cardId)
                if (response.isSuccessful) {
                    Timber.tag("MyRecordViewModel").d("API Success: Received $response")
                    sendEvent(MyRecordEvent.DeleteSuccess)
                } else {
                    val errorMessage = response.message()
                    sendEvent(MyRecordEvent.ShowError(errorMessage))
                    Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")

                    tokenManager.refreshToken(
                        onSuccess = { performDeleteDiaryCard(cardId) },
                        onFailure = { Timber.e("deleteDiaryCard API call failed") }
                    )
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                sendEvent(MyRecordEvent.ShowError(errorMessage))
                Timber.tag("MyRecordViewModel").d("Network Failure: $errorMessage")
            }
        }
    }

    private fun performPatchDiaryCard(cardId: Int) {
        Timber.tag("MyRecordViewModel").d("$cardId")

        viewModelScope.launch {
            try {
                val response = repository.patchDiaryCard(cardId)
                if (response.isSuccessful) {
                    Timber.tag("MyRecordViewModel").d("API Success: Received $response")
                    sendEvent(MyRecordEvent.PatchSuccess)
                } else {
                    val errorMessage = response.message()
                    val errorBody = response.errorBody()?.string()
                    Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")
                    Timber.tag("MyRecordViewModel").d("Error Body: $errorBody")

                    sendEvent(MyRecordEvent.ShowError(errorMessage))

                    tokenManager.refreshToken(
                        onSuccess = { performPatchDiaryCard(cardId) },
                        onFailure = { Timber.e("patchDiaryCard API call failed") }
                    )
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                sendEvent(MyRecordEvent.ShowError(errorMessage))
                Timber.tag("MyRecordViewModel").d("Network Failure: $errorMessage")
            }
        }
    }

    fun loadDiaryCards(year: Int, month: Int) = onAction(MyRecordAction.LoadDiaryCards(year, month))
    fun deleteDiaryCard(cardId: Int) = onAction(MyRecordAction.DeleteDiaryCard(cardId))
    fun patchDiaryCard(cardId: Int) = onAction(MyRecordAction.PatchDiaryCard(cardId))
}
