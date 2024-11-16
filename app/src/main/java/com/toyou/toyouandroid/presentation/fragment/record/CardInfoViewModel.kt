package com.toyou.toyouandroid.presentation.fragment.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.utils.TokenManager
import kotlinx.coroutines.launch
import timber.log.Timber

class CardInfoViewModel(
    private val recordRepository: RecordRepository,
    private val tokenManager: TokenManager
) : ViewModel(){
    private val _cards = MutableLiveData<List<CardModel>>()
    val cards: LiveData<List<CardModel>> get() = _cards
    private val _previewCards = MutableLiveData<List<PreviewCardModel>>()
    val previewCards : LiveData<List<PreviewCardModel>> get() = _previewCards

    val exposure : LiveData<Boolean> get() = _exposure
    private val _exposure = MutableLiveData<Boolean>()

    val answer = MutableLiveData<String>()
    private val _cardId = MutableLiveData<Int>().apply { value = 0 }
    val cardId: LiveData<Int> get() = _cardId

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _emotion = MutableLiveData<String>()
    val emotion: LiveData<String> get() = _emotion

    private val _receiver = MutableLiveData<String>()
    val receiver: LiveData<String> get() = _receiver

    fun getCardDetail(id : Long) {
        viewModelScope.launch {
            try {
                val response = recordRepository.getCardDetails(id)
                if (response.isSuccess) {
                    val detailCard = response.result
                    val previewCardList = mutableListOf<PreviewCardModel>()
                    _exposure.value = detailCard.exposure
                    _date.value = detailCard.date
                    _emotion.value = detailCard.emotion
                    _receiver.value = detailCard.receiver

                    detailCard.questions.let { questionList ->
                        questionList.forEach { question ->
                            val previewCard = when(question.type) {
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
                            _previewCards.value = previewCardList
                        }
                    }

                } else {
                    // 오류 처리
                    Timber.tag("CardViewModel").d("detail API 호출 실패: ${response.message}")

                    tokenManager.refreshToken(
                        onSuccess = { getCardDetail(id) }, // 토큰 갱신 후 다시 요청
                        onFailure = { Timber.e("Failed to refresh token and get card detail") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("CardViewModel").d("detail 예외 발생: ${e.message}")
            }
        }
    }

    fun getAnswerLength(answer: String): Int {
        return answer.length
    }
}