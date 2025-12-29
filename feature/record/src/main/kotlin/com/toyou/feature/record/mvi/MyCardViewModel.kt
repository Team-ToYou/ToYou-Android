package com.toyou.feature.record.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.CardModel
import com.toyou.core.domain.model.CardShortModel
import com.toyou.core.domain.model.ChooseModel
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.PreviewCardModel
import com.toyou.core.domain.model.PreviewChooseModel
import com.toyou.core.domain.repository.IRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyCardViewModel @Inject constructor(
    private val recordRepository: IRecordRepository,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<MyCardUiState, MyCardEvent, MyCardAction>(MyCardUiState()) {

    override fun handleAction(action: MyCardAction) {
        when (action) {
            is MyCardAction.LoadCardDetail -> performGetCardDetail(action.id)
            is MyCardAction.SetCardId -> performSetCardId(action.cardId)
            is MyCardAction.SetAnswer -> performSetAnswer(action.answer)
            is MyCardAction.ToggleExposure -> performToggleExposure()
            is MyCardAction.ClearAllData -> performClearAllData()
            is MyCardAction.ClearAll -> performClearAll()
        }
    }

    private fun performGetCardDetail(id: Long) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                when (val result = recordRepository.getCardDetails(id)) {
                    is DomainResult.Success -> {
                        val detailCard = result.data
                        val previewCardList = mutableListOf<PreviewCardModel>()

                        detailCard.questions.forEach { question ->
                            val previewCard = when (question.type) {
                                "OPTIONAL" -> {
                                    PreviewCardModel(
                                        question = question.content,
                                        fromWho = question.questioner,
                                        options = question.options,
                                        type = question.options?.size ?: 2,
                                        answer = question.answer,
                                        id = question.id
                                    )
                                }
                                "SHORT_ANSWER" -> {
                                    PreviewCardModel(
                                        question = question.content,
                                        fromWho = question.questioner,
                                        options = question.options,
                                        type = 0,
                                        answer = question.answer,
                                        id = question.id
                                    )
                                }
                                else -> {
                                    PreviewCardModel(
                                        question = question.content,
                                        fromWho = question.questioner,
                                        options = question.options,
                                        type = 1,
                                        answer = question.answer,
                                        id = question.id
                                    )
                                }
                            }
                            previewCardList.add(previewCard)
                        }

                        updateState {
                            copy(
                                exposure = detailCard.exposure,
                                date = detailCard.date,
                                emotion = detailCard.emotion,
                                receiver = detailCard.receiver,
                                previewCards = previewCardList,
                                isLoading = false
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        updateState { copy(isLoading = false) }
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performGetCardDetail(id) },
                            onFailure = { Timber.e("Failed to refresh token and get card detail") },
                            tag = "MyCardViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("CardViewModel").d("detail 예외 발생: ${e.message}")
                updateState { copy(isLoading = false) }
                sendEvent(MyCardEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun performSetCardId(cardId: Int) {
        updateState { copy(cardId = cardId) }
    }

    private fun performSetAnswer(answer: String) {
        updateState { copy(answer = answer) }
    }

    private fun performToggleExposure() {
        updateState { copy(exposure = !exposure) }
        Timber.tag("isLockSelected").d(currentState.exposure.toString())
    }

    private fun performClearAllData() {
        updateState {
            copy(
                previewCards = emptyList(),
                previewChoose = emptyList(),
                exposure = false
            )
        }
    }

    private fun performClearAll() {
        updateState {
            copy(
                cards = emptyList(),
                chooseCards = emptyList(),
                shortCards = emptyList()
            )
        }
    }

    fun getAnswerLength(answer: String): Int {
        return answer.length
    }

    fun setCardId(cardId: Int) {
        onAction(MyCardAction.SetCardId(cardId))
    }

    fun getCardDetail(id: Long) {
        onAction(MyCardAction.LoadCardDetail(id))
    }

    fun isLockSelected() {
        onAction(MyCardAction.ToggleExposure)
    }

    fun clearAllData() {
        onAction(MyCardAction.ClearAllData)
    }

    fun clearAll() {
        onAction(MyCardAction.ClearAll)
    }
}
