package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.UserDatabase
import com.toyou.toyouandroid.data.UserEntity
import com.toyou.toyouandroid.data.create.dto.request.AnswerDto
import com.toyou.toyouandroid.data.create.dto.response.QuestionsDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend

import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.domain.home.repository.HomeRepository
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.CardShortModel
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.model.PreviewChooseModel
import com.toyou.toyouandroid.network.BaseResponse
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class CardViewModel(private val tokenStorage: TokenStorage) : ViewModel(){
    private val _cards = MutableLiveData<List<CardModel>>()
    val cards: LiveData<List<CardModel>> get() = _cards
    private val _shortCards = MutableLiveData<List<CardShortModel>>()
    val shortCards: LiveData<List<CardShortModel>> get() = _shortCards
    private val _previewCards = MutableLiveData<List<PreviewCardModel>>()
    val previewCards : LiveData<List<PreviewCardModel>> get() = _previewCards

    private val _chooseCards = MutableLiveData<List<ChooseModel>>()
    val chooseCards : LiveData<List<ChooseModel>> get() = _chooseCards
    private val _previewChoose = MutableLiveData<List<PreviewChooseModel>>()
    val previewChoose : LiveData<List<PreviewChooseModel>> get() = _previewChoose
    private val repository = CreateRepository(tokenStorage)
    private val homeRepository = HomeRepository(tokenStorage)

    val exposure : LiveData<Boolean> get() = _exposure
    private val _exposure = MutableLiveData(false)

    val answer = MutableLiveData<String>()
    private val _cardId = MutableLiveData<Int>().apply { value = 0 }
    val cardId: LiveData<Int> get() = _cardId
    private val _isAllAnswersFilled = MutableLiveData(false)
    val isAllAnswersFilled: LiveData<Boolean> get() = _isAllAnswersFilled

    private var inputStatus: MutableList<Boolean> = mutableListOf()
    private var inputLongStatus : MutableList<Boolean> = mutableListOf()
    private var inputChooseStatus : MutableList<Boolean> = mutableListOf()

    private val _countSelection = MutableLiveData<Int>(0)
    val countSelection : LiveData<Int>get() = _countSelection

    fun setCardCount(count: Int, count2 : Int, count3 : Int) {
        inputStatus = MutableList(count){false} // 카드 개수만큼 false로 초기화
        inputLongStatus = MutableList(count2){false} // 카드 개수만큼 false로 초기화
        inputChooseStatus = MutableList(count3){false}
    }

    fun updateCardInputStatus(index: Int, isFilled: Boolean) {
        inputStatus[index] = isFilled
        checkIfAllAnswersFilled() // 입력 상태가 변경될 때마다 확인
    }

    fun updateCardInputStatusLong(index: Int, isFilled: Boolean) {
        inputLongStatus[index] = isFilled
        checkIfAllAnswersFilled() // 입력 상태가 변경될 때마다 확인
    }
    fun updateCardInputStatusChoose(index: Int, isFilled: Boolean) {
        inputChooseStatus[index] = isFilled
        checkIfAllAnswersFilled() // 입력 상태가 변경될 때마다 확인
    }

    private fun checkIfAllAnswersFilled() {
        _isAllAnswersFilled.value = inputStatus.count { it } == inputStatus.size && inputLongStatus.count { it } == inputLongStatus.size && inputChooseStatus.count { it } == inputChooseStatus.size
    }



    fun sendData(previewCardModels: List<PreviewCardModel>, exposure: Boolean,) {
        viewModelScope.launch {
            _cardId.value = repository.postCardData(previewCardModels, exposure)
            Log.d("카드 아이디", _cardId.value.toString())
        }
    }


    fun getAllData() = viewModelScope.launch {
        try {
            val response = repository.getAllData()
            if (response.isSuccess) {
                val questionsDto = response.result
                questionsDto?.let { mapToModels(it) }
            } else {
                // 오류 처리
                Log.e("CardViewModel", "API 호출 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("CardViewModel", "예외 발생: ${e.message}")
        }
    }

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _emotion = MutableLiveData<String>()
    val emotion: LiveData<String> get() = _emotion

    private val _receiver = MutableLiveData<String>()
    val receiver: LiveData<String> get() = _receiver

    fun getCardDetail(id : Long) = viewModelScope.launch {
        try {
            val response = homeRepository.getCardDetail(id)
            if (response.isSuccess) {
                val detailCard = response.result
                val previewCardList = mutableListOf<PreviewCardModel>()
                _exposure.value = detailCard.exposure
                _date.value = detailCard.date
                _emotion.value = detailCard.emotion
                _receiver.value = detailCard.receiver

                detailCard?.questions?.let { questionList ->
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

                // ViewModel이나 LiveData에 리스트 전달
                Log.d("detail", response.result.toString())
            } else {
                // 오류 처리
                Log.e("CardViewModel", "detail API 호출 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("CardViewModel", "detail 예외 발생: ${e.message}")
        }
    }


    // 응답 데이터 매핑
    private fun mapToModels(questionsDto: QuestionsDto) {
        val cardModels = mutableListOf<CardModel>()
        val chooseModels = mutableListOf<ChooseModel>()
        val cardShortModel = mutableListOf<CardShortModel>()

        for (question in questionsDto.questions) {
            when (question.type) {
                "OPTIONAL" -> {
                    chooseModels.add(
                        ChooseModel(
                            message = question.content,
                            fromWho = question.questioner,
                            options = question.options ?: emptyList(),
                            type = question.options.size,
                            id = question.id

                        )
                    )
                }
                "LONG_ANSWER" -> {
                    cardModels.add(
                        CardModel(
                            message = question.content,
                            fromWho = question.questioner,
                            questionType = 1,
                            id = question.id

                        )
                    )
                }
                else ->{
                    cardShortModel.add(
                        CardShortModel(
                            message = question.content,
                            fromWho = question.questioner,
                            questionType = 0,
                            id = question.id

                        )
                    )
                }
            }
        }

        _cards.value = cardModels
        _chooseCards.value = chooseModels
        _shortCards.value = cardShortModel
    }


    fun getAnswerLength(answer: String): Int {
        return answer.length
    }

    fun isLockSelected(lock : ImageView){
        lock.isSelected = !lock.isSelected
        _exposure.value = lock.isSelected
    }

    fun updateButtonState(position : Int, isSelected : Boolean){
        _cards.value = _cards.value?.mapIndexed { index, card ->
            if (index == position) {
                card.copy(isButtonSelected = isSelected)
            } else {
                card
            }
        }

    }

    fun updateShortButtonState(position : Int, isSelected : Boolean){
        _shortCards.value = _shortCards.value?.mapIndexed { index, card ->
            if (index == position) {
                card.copy(isButtonSelected = isSelected)
            } else {
                card
            }
        }

    }

    fun updateChooseButton(position: Int, isSelected: Boolean){
        _chooseCards.value = _chooseCards.value?.mapIndexed { index, card ->
            if (index == position) {
                card.copy(isButtonSelected = isSelected)
            } else {
                card
            }
        }
    }

    fun updateAllPreviews() {
        val existingCards = _previewCards.value?.toMutableList() ?: mutableListOf()

        val selectedNewCards = _cards.value?.filter { it.isButtonSelected } ?: emptyList()
        val selectedNewShortCards = _shortCards.value?.filter { it.isButtonSelected } ?: emptyList()
        val selectedNewChooseCards = _chooseCards.value?.filter { it.isButtonSelected } ?: emptyList()

        // 선택된 카드를 PreviewCardModel로 변환
        val newCards = selectedNewCards.map {
            PreviewCardModel(answer = "", question = it.message, type = it.questionType, fromWho = it.fromWho, options = null, id = it.id)
        }

        val newShortCards = selectedNewShortCards.map {
            PreviewCardModel(answer = "", question = it.message, type = it.questionType, fromWho = it.fromWho, options = null, id = it.id)
        }

        val newChooseCards = selectedNewChooseCards.map {
            PreviewCardModel(question = it.message, fromWho = it.fromWho, options = it.options, type = it.type, answer = "", id = it.id)
        }

        // 선택된 카드의 개수 계산
        val newCardsCount = selectedNewCards.size
        val newShortCardsCount = selectedNewShortCards.size
        val newChooseCardsCount = selectedNewChooseCards.size

        Log.d("카드 선택 개수", "New Cards: $newCardsCount, Short Cards: $newShortCardsCount, Choose Cards: $newChooseCardsCount")


        setCardCount(newShortCardsCount, newCardsCount, newChooseCardsCount)

        existingCards.addAll(newCards)
        existingCards.addAll(newShortCards)
        existingCards.addAll(newChooseCards)

        _previewCards.value = existingCards.distinct()


        _cards.value = _cards.value?.map {
            it.copy(isButtonSelected = false)
        }
        _chooseCards.value = _chooseCards.value?.map {
            it.copy(isButtonSelected = false)
        }
        _shortCards.value = _shortCards.value?.map {
            it.copy(isButtonSelected = false)
        }

            Log.d("미리보기", previewCards.value.toString())
    }

    fun clearAllData() {
        _previewCards.value = emptyList()
        _previewChoose.value = emptyList()
    }

    fun clearAll(){
        _previewCards.value = emptyList()
        _previewChoose.value = emptyList()
        _cards.value = emptyList()
        _chooseCards.value = emptyList()
        _shortCards.value = emptyList()
    }
   fun patchCard(previewCardModels: List<PreviewCardModel>, exposure: Boolean, id : Int){
            viewModelScope.launch {
                repository.patchCardData(previewCardModels, exposure, id)
        }

    }

    fun countSelect(selection : Boolean){
        if (selection) {
            _countSelection.value?.let {
                _countSelection.value = it + 1
            }
        }
        else{
            _countSelection.value?.let {
                _countSelection.value = it - 1
            }
        }

    }

    fun resetSelect(){
        _countSelection.value = 0
    }

}