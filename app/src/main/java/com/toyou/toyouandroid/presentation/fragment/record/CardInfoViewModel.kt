package com.toyou.toyouandroid.presentation.fragment.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.domain.record.IRecordRepository
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CardInfoViewModel @Inject constructor(
    private val recordRepository: IRecordRepository,
    private val tokenManager: TokenManager
) : MviViewModel<CardInfoUiState, CardInfoEvent, CardInfoAction>(
    initialState = CardInfoUiState()
) {

    private val _cards = MutableLiveData<List<CardModel>>()
    val cards: LiveData<List<CardModel>> get() = _cards

    private val _previewCards = MutableLiveData<List<PreviewCardModel>>()
    val previewCards: LiveData<List<PreviewCardModel>> get() = _previewCards

    private val _exposure = MutableLiveData<Boolean>()
    val exposure: LiveData<Boolean> get() = _exposure

    val answer = MutableLiveData<String>()

    private val _cardId = MutableLiveData<Int>().apply { value = 0 }
    val cardId: LiveData<Int> get() = _cardId

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _emotion = MutableLiveData<String>()
    val emotion: LiveData<String> get() = _emotion

    private val _receiver = MutableLiveData<String>()
    val receiver: LiveData<String> get() = _receiver

    init {
        state.onEach { uiState ->
            _cards.value = uiState.cards
            _previewCards.value = uiState.previewCards
            _exposure.value = uiState.exposure
            answer.value = uiState.answer
            _cardId.value = uiState.cardId
            _date.value = uiState.date
            _emotion.value = uiState.emotion
            _receiver.value = uiState.receiver
        }.launchIn(viewModelScope)
    }

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
                val response = recordRepository.getCardDetails(id)
                if (response.isSuccess) {
                    val detailCard = response.result
                    val previewCardList = mutableListOf<PreviewCardModel>()

                    detailCard.questions.let { questionList ->
                        questionList.forEach { question ->
                            val previewCard = when (question.type) {
                                "OPTIONAL" -> {
                                    PreviewCardModel(
                                        question = question.content,
                                        fromWho = question.questioner,
                                        options = question.options,
                                        type = question.options!!.size,
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
                } else {
                    Timber.tag("CardViewModel").d("detail API 호출 실패: ${response.message}")
                    updateState { copy(isLoading = false) }
                    tokenManager.refreshToken(
                        onSuccess = { performGetCardDetail(id) },
                        onFailure = {
                            Timber.e("Failed to refresh token and get card detail")
                            sendEvent(CardInfoEvent.CardDetailFailed)
                        }
                    )
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
