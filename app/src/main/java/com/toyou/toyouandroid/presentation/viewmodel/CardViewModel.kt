package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.create.dto.response.QuestionsDto

import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.CardShortModel
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.model.PreviewChooseModel
import com.toyou.toyouandroid.network.BaseResponse
import kotlinx.coroutines.launch
import java.time.LocalDate

class CardViewModel : ViewModel(){
    private val _cards = MutableLiveData<List<CardModel>>()
    val cards: LiveData<List<CardModel>> get() = _cards
    private val _shortCards = MutableLiveData<List<CardShortModel>>()
    val shortCards: LiveData<List<CardShortModel>> get() = _shortCards
    private val _previewCards = MutableLiveData<List<PreviewCardModel>>()
    val previewCards : LiveData<List<PreviewCardModel>> get() = _previewCards

    private val _answersMap = MutableLiveData<MutableMap<Int, String>>(mutableMapOf())
    val answersMap: LiveData<MutableMap<Int, String>> get() = _answersMap
    private val _chooseCards = MutableLiveData<List<ChooseModel>>()
    val chooseCards : LiveData<List<ChooseModel>> get() = _chooseCards
    private val _previewChoose = MutableLiveData<List<PreviewChooseModel>>()
    val previewChoose : LiveData<List<PreviewChooseModel>> get() = _previewChoose
    private val _isAnyEditTextFilled = MutableLiveData(false)
    val isAnyEditTextFilled: LiveData<Boolean> get() = _isAnyEditTextFilled

    private val repository = CreateRepository()

    val exposure : LiveData<Boolean> get() = _exposure
    private val _exposure = MutableLiveData(false)

    private val _result = MutableLiveData<BaseResponse<QuestionsDto>>()
    val answer = MutableLiveData<String>()
    val result: LiveData<BaseResponse<QuestionsDto>>
        get() = _result

    fun sendData(previewCardModels: List<PreviewCardModel>, exposure: Boolean) {
        viewModelScope.launch {
            repository.postCardData(previewCardModels, exposure)
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


   /* init {
        getAllData()
    }*/


    fun getAnswerLength(answer: String): Int {
        return answer.length
    }



    fun setEditTextFilled(isFilled: Boolean) {
        _isAnyEditTextFilled.value = isFilled
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

        val newCards = _cards.value?.filter { it.isButtonSelected }?.map {
            PreviewCardModel(answer = "", question = it.message, type = it.questionType, fromWho = it.fromWho, options = null, id = it.id)
        } ?: emptyList()

        val newShortCards = _shortCards.value?.filter { it.isButtonSelected }?.map {
            PreviewCardModel(answer = "", question = it.message, type = it.questionType, fromWho = it.fromWho, options = null, id = it.id)
        } ?: emptyList()

        val newChooseCards =_chooseCards.value?.filter { it.isButtonSelected }?.map {
            PreviewCardModel(question = it.message, fromWho = it.fromWho, options = it.options, type = it.type, answer = "", id = it.id)
        } ?: emptyList()

        existingCards.addAll(newCards)
        existingCards.addAll(newShortCards)
        existingCards.addAll(newChooseCards)

        _previewCards.value = existingCards.distinct()
        Log.d("미리보기", _previewCards.value.toString())
    }

    fun clearAllData() {
        _previewCards.value = emptyList()
        _previewChoose.value = emptyList()
    }

}