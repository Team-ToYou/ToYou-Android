package com.toyou.feature.record.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.CardModel
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.PreviewCardModel
import com.toyou.core.domain.repository.IRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CardInfoViewModel @Inject constructor(
    private val recordRepository: IRecordRepository,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<CardInfoUiState, CardInfoEvent, CardInfoAction>(
    initialState = CardInfoUiState()
) {

    override fun handleAction(action: CardInfoAction) {
        when (action) {
            is CardInfoAction.GetCardDetail -> performGetCardDetail(action.id)
            is CardInfoAction.UpdateAnswer -> performUpdateAnswer(action.answer)
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
                        sendEvent(CardInfoEvent.CardDetailLoaded)
                    }
                    is DomainResult.Error -> {
                        updateState { copy(isLoading = false) }
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performGetCardDetail(id) },
                            onFailure = {
                                sendEvent(CardInfoEvent.CardDetailFailed)
                            },
                            tag = "CardInfoViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("CardViewModel").d("detail 예외 발생: ${e.message}")
                updateState { copy(isLoading = false) }
                sendEvent(CardInfoEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun performUpdateAnswer(answer: String) {
        updateState { copy(answer = answer) }
    }

    fun getCardDetail(id: Long) = onAction(CardInfoAction.GetCardDetail(id))

    fun getAnswerLength(answer: String): Int {
        return answer.length
    }
}
