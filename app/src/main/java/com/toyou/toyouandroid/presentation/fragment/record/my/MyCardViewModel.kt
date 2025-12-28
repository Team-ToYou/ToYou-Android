package com.toyou.toyouandroid.presentation.fragment.record.my

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
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyCardViewModel @Inject constructor(
    private val recordRepository: IRecordRepository,
    private val tokenManager: TokenManager
) : MviViewModel<MyCardUiState, MyCardEvent, MyCardAction>(MyCardUiState()) {

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

    val _exposure = MutableLiveData<Boolean>()
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
        viewModelScope.launch {
            state.collect { newState ->
                _cards.value = newState.cards
                _shortCards.value = newState.shortCards
                _previewCards.value = newState.previewCards
                _chooseCards.value = newState.chooseCards
                _previewChoose.value = newState.previewChoose
                _exposure.value = newState.exposure
                answer.value = newState.answer
                _cardId.value = newState.cardId
                _date.value = newState.date
                _emotion.value = newState.emotion
                _receiver.value = newState.receiver
            }
        }
    }

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
                } else {
                    Timber.tag("CardViewModel").d("detail API 호출 실패: ${response.message}")
                    updateState { copy(isLoading = false) }
                    tokenManager.refreshToken(
                        onSuccess = { performGetCardDetail(id) },
                        onFailure = { Timber.e("Failed to refresh token and get card detail") }
                    )
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
