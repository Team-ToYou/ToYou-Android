package com.toyou.toyouandroid.presentation.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.data.emotion.dto.DiaryCard
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.data.emotion.dto.YesterdayFriendsResponse
import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.calendar.getCurrentDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class HomeViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _homeEmotion = MutableLiveData<Int>()
    val homeEmotion: LiveData<Int> get() = _homeEmotion

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    private val _homeDateBackground = MutableLiveData<Int>()
    val homeDateBackground: LiveData<Int> get() = _homeDateBackground

    private val _homeBackground = MutableLiveData<Int>()
    val homeBackground: LiveData<Int> get() = _homeBackground

    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> get() = _currentDate

    private val _diaryCards = MutableLiveData<List<DiaryCard>?>()
    val diaryCards: LiveData<List<DiaryCard>?> get() = _diaryCards

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    private val apiService: EmotionService = AuthNetworkModule.getClient().create(EmotionService::class.java)

    init {
        _currentDate.value = getCurrentDate()
    }

    fun updateHomeEmotion(emotion: Int, text: String, date: Int, background: Int) {
        _homeEmotion.value = emotion
        _text.value = text
        _homeDateBackground.value = date
        _homeBackground.value = background
    }

    fun resetState() {
        _homeEmotion.value = R.drawable.home_emotion_none
        _text.value = "멘트"
        _homeDateBackground.value = R.color.g00
        _homeBackground.value = R.drawable.background_white
    }

    private val _cardDetails = MutableLiveData<List<CardDetail>>()
    val cardDetails: LiveData<List<CardDetail>> get() = _cardDetails

    fun loadYesterdayDiaryCards() {
        _isLoading.value = true

        apiService.getYesterdayFriendCard().enqueue(object : Callback<YesterdayFriendsResponse> {
            override fun onResponse(call: Call<YesterdayFriendsResponse>, response: Response<YesterdayFriendsResponse>) {
                if (response.isSuccessful) {
                    val diaryResponse = response.body()
                    Timber.d("API 응답 데이터: $diaryResponse")

                    if (diaryResponse?.isSuccess == true) {
                        val cards = diaryResponse.result.yesterday
                        Timber.d("받아온 카드 목록: $cards")

                        if (cards.isEmpty()) {
                            Timber.d("카드 목록이 비어 있습니다.")
                            _isEmpty.value = true
                        } else {
                            _diaryCards.value = cards
//                            getCardDetail(cards.map { it.cardId })
                            _isEmpty.value = false
                        }
                    } else {
                        tokenManager.refreshToken(
                            onSuccess = { loadYesterdayDiaryCards() },
                            onFailure = { Timber.e("loadYesterdayDiaryCards API call failed") }
                        )
                        Timber.e("API 호출 실패 - 응답이 성공하지 않음. 메시지: ${diaryResponse?.message}")
                    }
                } else {
                    tokenManager.refreshToken(
                        onSuccess = { loadYesterdayDiaryCards() },
                        onFailure = { Timber.e("loadYesterdayDiaryCards API call failed") }
                    )
                    Timber.e("API 호출 실패 - 코드: ${response.code()}, 메시지: ${response.message()}")
                }

                _isLoading.value = false
            }

            override fun onFailure(call: Call<YesterdayFriendsResponse>, t: Throwable) {
                Timber.e(t, "API 호출 실패 - 네트워크 오류")
                _isLoading.value = false
                _isEmpty.value = true // 실패 시 빈 상태로 간주
            }
        })
    }

//    fun getCardDetail(ids: List<Int>) {
//        if (ids.isEmpty()) return  // 리스트가 비어 있을 경우 함수 종료
//
//        val detailsList = mutableListOf<CardDetail>()
//
//        ids.forEach { cardId ->
//            viewModelScope.launch {
//                try {
//                    apiService.getDiaryCardDetail(cardId).enqueue(object : Callback<CardDetail> {
//                        override fun onResponse(call: Call<CardDetail>, response: Response<CardDetail>) {
//                            if (response.isSuccessful) {
//                                response.body()?.let { detailCard ->
//                                    val previewCardList = mutableListOf<PreviewCardModel>()
//
//                                    detailCard.questions?.forEach { question ->
//                                        val previewCard = when (question.type) {
//                                            "OPTIONAL" -> PreviewCardModel(
//                                                question = question.content,
//                                                fromWho = question.questioner,
//                                                options = question.options,
//                                                type = question.options?.size ?: 0,
//                                                answer = question.answer,
//                                                id = question.id
//                                            )
//
//                                            "SHORT_ANSWER" -> PreviewCardModel(
//                                                question = question.content,
//                                                fromWho = question.questioner,
//                                                options = question.options,
//                                                type = 0,
//                                                answer = question.answer,
//                                                id = question.id
//                                            )
//
//                                            else -> PreviewCardModel(
//                                                question = question.content,
//                                                fromWho = question.questioner,
//                                                options = question.options,
//                                                type = 1,
//                                                answer = question.answer,
//                                                id = question.id
//                                            )
//                                        }
//                                        previewCardList.add(previewCard)
//                                    }
//
//                                    detailsList.add(detailCard)
//                                    _cardDetails.value = detailsList.toList() // 매번 최신 리스트로 반영
//                                    Timber.tag("HomeViewModel").d("$detailsList")
//                                }
//                            } else {
//                                Timber.tag("HomeViewModel").d("detail API 호출 실패: ${response.message()}")
//                            }
//                        }
//
//                        override fun onFailure(call: Call<CardDetail>, t: Throwable) {
//                            Timber.tag("HomeViewModel").d("detail API 호출 실패: ${t.message}")
//                        }
//                    })
//                } catch (e: Exception) {
//                    Timber.tag("HomeViewModel").d("detail 예외 발생: ${e.message}")
//                }
//            }
//        }
//    }
}
