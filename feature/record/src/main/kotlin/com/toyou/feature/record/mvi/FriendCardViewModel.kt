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
class FriendCardViewModel @Inject constructor(
    private val recordRepository: IRecordRepository,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<FriendCardUiState, FriendCardEvent, FriendCardAction>(
    initialState = FriendCardUiState()
) {

    override fun handleAction(action: FriendCardAction) {
        when (action) {
            is FriendCardAction.GetCardDetail -> performGetCardDetail(action.id)
            is FriendCardAction.SetCardId -> performSetCardId(action.cardId)
            is FriendCardAction.UpdateAnswer -> performUpdateAnswer(action.answer)
            is FriendCardAction.ClearAllData -> performClearAllData()
            is FriendCardAction.ClearAll -> performClearAll()
        }
    }

    private fun performGetCardDetail(id: Long) {
        viewModelScope.launch {
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
                                previewCards = previewCardList
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performGetCardDetail(id) },
                            onFailure = { Timber.e("getCardDetail API call failed") },
                            tag = "FriendCardViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("CardViewModel").d("detail 예외 발생: ${e.message}")
            }
        }
    }

    private fun performSetCardId(cardId: Int) {
        updateState { copy(cardId = cardId) }
    }

    private fun performUpdateAnswer(answer: String) {
        updateState { copy(answer = answer) }
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

    fun setCardId(cardId: Int) = onAction(FriendCardAction.SetCardId(cardId))

    fun getCardDetail(id: Long) = onAction(FriendCardAction.GetCardDetail(id))

    fun getAnswerLength(answer: String): Int = answer.length

    fun clearAllData() = onAction(FriendCardAction.ClearAllData)

    fun clearAll() = onAction(FriendCardAction.ClearAll)
}
