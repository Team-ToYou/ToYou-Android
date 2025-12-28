package com.toyou.toyouandroid.presentation.fragment.record.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.domain.record.IRecordRepository
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.CardShortModel
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.model.PreviewChooseModel
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendCardViewModel @Inject constructor(
    private val recordRepository: IRecordRepository,
    private val tokenManager: TokenManager
) : MviViewModel<FriendCardUiState, FriendCardEvent, FriendCardAction>(
    initialState = FriendCardUiState()
) {

    private val _cards = MutableLiveData<List<CardModel>>()
    val cards: LiveData<List<CardModel>> get() = _cards

    private val _shortCards = MutableLiveData<List<CardShortModel>>()
    val shortCards: LiveData<List<CardShortModel>> get() = _shortCards

    private val _previewCards = MutableLiveData<List<PreviewCardModel>>()
    val previewCards: LiveData<List<PreviewCardModel>> get() = _previewCards

    private val _chooseCards = MutableLiveData<List<ChooseModel>>()
    val chooseCards: LiveData<List<ChooseModel>> get() = _chooseCards

    private val _previewChoose = MutableLiveData<List<PreviewChooseModel>>()
    val previewChoose: LiveData<List<PreviewChooseModel>> get() = _previewChoose

    private val _exposure = MutableLiveData<Boolean>()
    val exposure: LiveData<Boolean> get() = _exposure

    val answer = MutableLiveData<String>()

    private val _cardId = MutableLiveData<Int>().apply { value = 0 }
    val cardId: LiveData<Int> get() = _cardId

    private val _isAllAnswersFilled = MutableLiveData(false)
    val isAllAnswersFilled: LiveData<Boolean> get() = _isAllAnswersFilled

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _emotion = MutableLiveData<String>()
    val emotion: LiveData<String> get() = _emotion

    private val _receiver = MutableLiveData<String>()
    val receiver: LiveData<String> get() = _receiver

    init {
        state.onEach { uiState ->
            _cards.value = uiState.cards
            _shortCards.value = uiState.shortCards
            _previewCards.value = uiState.previewCards
            _chooseCards.value = uiState.chooseCards
            _previewChoose.value = uiState.previewChoose
            _exposure.value = uiState.exposure
            answer.value = uiState.answer
            _cardId.value = uiState.cardId
            _isAllAnswersFilled.value = uiState.isAllAnswersFilled
            _date.value = uiState.date
            _emotion.value = uiState.emotion
            _receiver.value = uiState.receiver
        }.launchIn(viewModelScope)
    }

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
                            previewCards = previewCardList
                        )
                    }
                } else {
                    Timber.tag("CardViewModel").d("detail API 호출 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { performGetCardDetail(id) },
                        onFailure = { Timber.e("getCardDetail API call failed") }
                    )
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
