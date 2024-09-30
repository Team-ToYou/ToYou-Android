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
    private val _exposure = MutableLiveData<Boolean>()

    val answer = MutableLiveData<String>()
    private val _cardId = MutableLiveData<Int>().apply { value = 0 }
    val cardId: LiveData<Int> get() = _cardId
    private val _isAllAnswersFilled = MutableLiveData(false)
    val isAllAnswersFilled: LiveData<Boolean> get() = _isAllAnswersFilled

    private var inputStatus: MutableList<Boolean> = mutableListOf()
    private var inputLongStatus : MutableList<Boolean> = mutableListOf()
    private var inputChooseStatus : MutableList<Boolean> = mutableListOf()

    private val _countSelection = MutableLiveData<Int>()
    val countSelection : LiveData<Int>get() = _countSelection
    var toastShow : Boolean = false

    init {
        _exposure.value = true
    }

    private fun initializeCountSelection() {
        _countSelection.value = _previewCards.value?.size ?: 0
    }

    fun setCardCount(count: Int, count2 : Int, count3 : Int) {
        inputStatus = MutableList(count){false}
        inputLongStatus = MutableList(count2){false}
        inputChooseStatus = MutableList(count3){false}
    }

    fun updateCardInputStatus(index: Int, isFilled: Boolean) {
        inputStatus[index] = isFilled
        checkIfAllAnswersFilled() // 입력 상태가 변경될 때마다 확인
    }

    fun updateCardInputStatusLong(index: Int, isFilled: Boolean) {
        inputLongStatus[index] = isFilled
        checkIfAllAnswersFilled()
    }
    fun updateCardInputStatusChoose(index: Int, isFilled: Boolean) {
        inputChooseStatus[index] = isFilled
        checkIfAllAnswersFilled()
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
                if (previewCards.value == null)
                    questionsDto?.let {
                        mapToModels(it)
                        Log.d("get", questionsDto.toString())
                    }
                else
                    questionsDto?.let { mapToPatchModels(it) }
            } else {
                // 오류 처리
            }
        } catch (e: Exception) {
        }
    }

    fun getCardDetail(id : Long) = viewModelScope.launch {
        try {
            val response = homeRepository.getCardDetail(id)
            if (response.isSuccess) {
                val detailCard = response.result
                val previewCardList = mutableListOf<PreviewCardModel>()
                _exposure.value = detailCard.exposure

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

            } else {
                // 오류 처리
                Log.e("CardViewModel", "detail API 호출 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("CardViewModel", "detail 예외 발생: ${e.message}")
        }
    }

    private fun mapToPatchModels(questionsDto: QuestionsDto) {
        val cardModels = mutableListOf<CardModel>()
        val chooseModels = mutableListOf<ChooseModel>()
        val cardShortModel = mutableListOf<CardShortModel>()
        initializeCountSelection()

        for (question in questionsDto.questions) {
            when (question.type) {
                "OPTIONAL" -> {
                    // 선택된 상태인지 확인 (previewCards에서 동일한 id가 있는지 확인)
                    val isSelected = _previewCards.value?.any { it.id == question.id && it.type == 2 } ?: false
                            || _previewCards.value?.any { it.id == question.id && it.type == 3 } ?: false

                    chooseModels.add(
                        ChooseModel(
                            message = question.content,
                            fromWho = question.questioner,
                            options = question.options ?: emptyList(),
                            type = question.options.size,
                            id = question.id,
                            isButtonSelected = isSelected,
                        )
                    )
                }
                "LONG_ANSWER" -> {
                    // 선택된 상태인지 확인 (previewCards에서 동일한 id가 있는지 확인)
                    val isSelected = _previewCards.value?.any { it.id == question.id && it.type == 1 } ?: false

                    cardModels.add(
                        CardModel(
                            message = question.content,
                            fromWho = question.questioner,
                            questionType = 1,
                            id = question.id,
                            isButtonSelected = isSelected // 선택 여부 설정
                        )
                    )
                }
                else -> {
                    // 선택된 상태인지 확인 (previewCards에서 동일한 id가 있는지 확인)
                    val isSelected = _previewCards.value?.any { it.id == question.id && it.type == 0 } ?: false

                    cardShortModel.add(
                        CardShortModel(
                            message = question.content,
                            fromWho = question.questioner,
                            questionType = 0,
                            id = question.id,
                            isButtonSelected = isSelected // 선택 여부 설정
                        )
                    )
                }
            }
        }

        _cards.value = cardModels
        _chooseCards.value = chooseModels
        _shortCards.value = cardShortModel

    }


    // 응답 데이터 매핑
    private fun mapToModels(questionsDto: QuestionsDto) {
        val cardModels = mutableListOf<CardModel>()
        val chooseModels = mutableListOf<ChooseModel>()
        val cardShortModel = mutableListOf<CardShortModel>()
        initializeCountSelection()

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

    fun isLockSelected() {
        _exposure.value = _exposure.value?.not() ?: true
        Log.d("lock", _exposure.value.toString())
        //toastShow = true
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
        val selectedCardIds = _cards.value?.filter { it.isButtonSelected }?.map { it.id }?.toSet() ?: emptySet()
        val selectedShortCardIds = _shortCards.value?.filter { it.isButtonSelected }?.map { it.id }?.toSet() ?: emptySet()
        val selectedChooseCardIds = _chooseCards.value?.filter { it.isButtonSelected }?.map { it.id }?.toSet() ?: emptySet()

        // 기존 미리보기 목록 중 선택된 ID가 포함된 카드만 필터링하여 새로운 목록으로 설정
        val existingCards = _previewCards.value?.filter { it.id in selectedCardIds || it.id in selectedShortCardIds || it.id in selectedChooseCardIds }?.toMutableList() ?: mutableListOf()

        val existingIds = existingCards.map { it.id }.toSet()

        val selectedNewCards = _cards.value?.filter { it.isButtonSelected && it.id !in existingIds } ?: emptyList()
        val selectedNewShortCards = _shortCards.value?.filter { it.isButtonSelected && it.id !in existingIds } ?: emptyList()
        val selectedNewChooseCards = _chooseCards.value?.filter { it.isButtonSelected && it.id !in existingIds } ?: emptyList()

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
        val newCardsCount = selectedNewCards.size + existingCards.filter { it.type == 1 }.size
        val newShortCardsCount = selectedNewShortCards.size + existingCards.filter { it.type == 0 }.size
        val newChooseCardsCount = selectedNewChooseCards.size + existingCards.filter { it.type == 2 || it.type ==3 }.size

        Log.d("카드 선택 개수", "New Cards: $newCardsCount, Short Cards: $newShortCardsCount, Choose Cards: $newChooseCardsCount")

        setCardCount(newShortCardsCount, newCardsCount, newChooseCardsCount)

        // 새로운 카드를 기존 목록에 추가
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
        //_previewCards.value = emptyList()
        //_previewChoose.value = emptyList()
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

        Log.d("선택5", countSelection.value.toString())

    }

    fun resetSelect(){
        _countSelection.value = 0
    }

}