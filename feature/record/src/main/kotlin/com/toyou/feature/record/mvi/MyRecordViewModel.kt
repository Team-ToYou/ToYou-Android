package com.toyou.feature.record.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.DiaryCard
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.repository.IRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyRecordViewModel @Inject constructor(
    private val repository: IRecordRepository,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<MyRecordUiState, MyRecordEvent, MyRecordAction>(
    initialState = MyRecordUiState()
) {

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
                when (val result = repository.getMyRecord(year, month)) {
                    is DomainResult.Success -> {
                        val cardList = result.data.cards
                        updateState { copy(diaryCards = cardList, isLoading = false) }
                        Timber.tag("MyRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                        Timber.tag("MyRecordViewModel").d("API Success: Received $cardList")
                    }
                    is DomainResult.Error -> {
                        val errorMessage = result.message
                        updateState { copy(isLoading = false) }
                        sendEvent(MyRecordEvent.ShowError(errorMessage))
                        Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")

                        errorHandler.handleError(
                            error = result,
                            onRetry = { performLoadDiaryCards(year, month) },
                            onFailure = { Timber.e("loadDiaryCards API call failed") },
                            tag = "MyRecordViewModel"
                        )
                    }
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
                when (val result = repository.deleteDiaryCard(cardId)) {
                    is DomainResult.Success -> {
                        Timber.tag("MyRecordViewModel").d("API Success: Delete successful")
                        sendEvent(MyRecordEvent.DeleteSuccess)
                    }
                    is DomainResult.Error -> {
                        val errorMessage = result.message
                        sendEvent(MyRecordEvent.ShowError(errorMessage))
                        Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")

                        errorHandler.handleError(
                            error = result,
                            onRetry = { performDeleteDiaryCard(cardId) },
                            onFailure = { Timber.e("deleteDiaryCard API call failed") },
                            tag = "MyRecordViewModel"
                        )
                    }
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
                when (val result = repository.patchDiaryCard(cardId)) {
                    is DomainResult.Success -> {
                        Timber.tag("MyRecordViewModel").d("API Success: Patch successful")
                        sendEvent(MyRecordEvent.PatchSuccess)
                    }
                    is DomainResult.Error -> {
                        val errorMessage = result.message
                        Timber.tag("MyRecordViewModel").d("API Error: $errorMessage")

                        sendEvent(MyRecordEvent.ShowError(errorMessage))

                        errorHandler.handleError(
                            error = result,
                            onRetry = { performPatchDiaryCard(cardId) },
                            onFailure = { Timber.e("patchDiaryCard API call failed") },
                            tag = "MyRecordViewModel"
                        )
                    }
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
