package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.data.create.dto.response.QuestionsDto
import com.toyou.toyouandroid.domain.create.repository.ICreateRepository
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
class CardViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val repository: ICreateRepository
) : MviViewModel<CardUiState, CardEvent, CardAction>(
    initialState = CardUiState()
) {
    private var inputStatus: MutableList<Boolean> = mutableListOf()
    private var inputLongStatus: MutableList<Boolean> = mutableListOf()
    private var inputChooseStatus: MutableList<Boolean> = mutableListOf()
    var toastShow: Boolean = false

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

    private val _countSelection = MutableLiveData<Int>()
    val countSelection: LiveData<Int> get() = _countSelection

    private val _lockDisabled = MutableLiveData(false)
    val lockDisabled: LiveData<Boolean> get() = _lockDisabled

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
            _cardId.value = uiState.cardId
            _isAllAnswersFilled.value = uiState.isAllAnswersFilled
            _countSelection.value = uiState.countSelection
            _lockDisabled.value = uiState.lockDisabled
            _date.value = uiState.date
            _emotion.value = uiState.emotion
            _receiver.value = uiState.receiver
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: CardAction) {
        when (action) {
            is CardAction.SetCardId -> performSetCardId(action.cardId)
            is CardAction.DisableLock -> performDisableLock(action.lock)
            is CardAction.SetCardCount -> performSetCardCount(action.count, action.count2, action.count3)
            is CardAction.UpdateCardInputStatus -> performUpdateCardInputStatus(action.index, action.isFilled)
            is CardAction.UpdateCardInputStatusLong -> performUpdateCardInputStatusLong(action.index, action.isFilled)
            is CardAction.UpdateCardInputStatusChoose -> performUpdateCardInputStatusChoose(action.index, action.isFilled)
            is CardAction.SendData -> performSendData(action.previewCardModels, action.exposure)
            is CardAction.GetAllData -> performGetAllData()
            is CardAction.UpdateButtonState -> performUpdateButtonState(action.position, action.isSelected)
            is CardAction.UpdateShortButtonState -> performUpdateShortButtonState(action.position, action.isSelected)
            is CardAction.UpdateChooseButton -> performUpdateChooseButton(action.position, action.isSelected)
            is CardAction.UpdateAllPreviews -> performUpdateAllPreviews()
            is CardAction.ClearAllData -> performClearAllData()
            is CardAction.ClearAll -> performClearAll()
            is CardAction.PatchCard -> performPatchCard(action.previewCardModels, action.exposure, action.id)
            is CardAction.CountSelect -> performCountSelect(action.selection)
            is CardAction.ResetSelect -> performResetSelect()
            is CardAction.ToggleLock -> performToggleLock()
            is CardAction.UpdateAnswer -> performUpdateAnswer(action.answer)
        }
    }

    private fun performSetCardId(cardId: Int) {
        updateState { copy(cardId = cardId) }
    }

    private fun performDisableLock(lock: Boolean) {
        updateState { copy(lockDisabled = lock) }
    }

    private fun performSetCardCount(count: Int, count2: Int, count3: Int) {
        inputStatus = MutableList(count) { false }
        inputLongStatus = MutableList(count2) { false }
        inputChooseStatus = MutableList(count3) { false }
    }

    private fun performUpdateCardInputStatus(index: Int, isFilled: Boolean) {
        inputStatus[index] = isFilled
        performCheckIfAllAnswersFilled()
    }

    private fun performUpdateCardInputStatusLong(index: Int, isFilled: Boolean) {
        inputLongStatus[index] = isFilled
        performCheckIfAllAnswersFilled()
    }

    private fun performUpdateCardInputStatusChoose(index: Int, isFilled: Boolean) {
        inputChooseStatus[index] = isFilled
        performCheckIfAllAnswersFilled()
    }

    private fun performCheckIfAllAnswersFilled() {
        val allFilled = inputStatus.count { it } == inputStatus.size &&
                inputLongStatus.count { it } == inputLongStatus.size &&
                inputChooseStatus.count { it } == inputChooseStatus.size
        updateState { copy(isAllAnswersFilled = allFilled) }
        Timber.tag("선택9${currentState.isAllAnswersFilled}")
    }

    private fun performSendData(previewCardModels: List<PreviewCardModel>, exposure: Boolean) {
        viewModelScope.launch {
            try {
                val response = repository.postCardData(previewCardModels, exposure)

                if (response.isSuccess) {
                    Timber.tag("sendData").d("카드 전송 성공")
                    response.result.let { answerPost ->
                        updateState { copy(cardId = answerPost.id) }
                        Timber.tag("sendData").d("카드 ID: ${currentState.cardId}")
                    }
                    sendEvent(CardEvent.SendDataSuccess)
                } else {
                    Timber.tag("sendData").d("카드 전송 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { performSendData(previewCardModels, exposure) },
                        onFailure = { Timber.e("sendData API Call Failed") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("sendData").e("Exception: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { performSendData(previewCardModels, exposure) },
                    onFailure = { Timber.e("sendData API Call Failed") }
                )
            }
        }
    }

    private fun performGetAllData() {
        viewModelScope.launch {
            try {
                val response = repository.getAllData()
                if (response.isSuccess) {
                    val questionsDto = response.result
                    if (currentState.previewCards.isEmpty()) {
                        performMapToModels(questionsDto)
                        Timber.tag("get").d(questionsDto.toString())
                    } else {
                        performMapToPatchModels(questionsDto)
                    }
                } else {
                    tokenManager.refreshToken(
                        onSuccess = { performGetAllData() },
                        onFailure = { Timber.tag("CardViewModel").d("refresh error") }
                    )
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun performMapToPatchModels(questionsDto: QuestionsDto) {
        val cardModels = mutableListOf<CardModel>()
        val chooseModels = mutableListOf<ChooseModel>()
        val cardShortModel = mutableListOf<CardShortModel>()
        performInitializeCountSelection()

        for (question in questionsDto.questions) {
            when (question.type) {
                "OPTIONAL" -> {
                    val isSelected = currentState.previewCards.any { it.id == question.id && it.type == 2 }
                            || currentState.previewCards.any { it.id == question.id && it.type == 3 }

                    chooseModels.add(
                        ChooseModel(
                            message = question.content,
                            fromWho = question.questioner,
                            options = question.options,
                            type = question.options.size,
                            id = question.id,
                            isButtonSelected = isSelected,
                        )
                    )
                }
                "LONG_ANSWER" -> {
                    val isSelected = currentState.previewCards.any { it.id == question.id && it.type == 1 }

                    cardModels.add(
                        CardModel(
                            message = question.content,
                            fromWho = question.questioner,
                            questionType = 1,
                            id = question.id,
                            isButtonSelected = isSelected
                        )
                    )
                }
                else -> {
                    val isSelected = currentState.previewCards.any { it.id == question.id && it.type == 0 }

                    cardShortModel.add(
                        CardShortModel(
                            message = question.content,
                            fromWho = question.questioner,
                            questionType = 0,
                            id = question.id,
                            isButtonSelected = isSelected
                        )
                    )
                }
            }
        }

        updateState {
            copy(
                cards = cardModels,
                chooseCards = chooseModels,
                shortCards = cardShortModel
            )
        }
    }

    private fun performMapToModels(questionsDto: QuestionsDto) {
        val cardModels = mutableListOf<CardModel>()
        val chooseModels = mutableListOf<ChooseModel>()
        val cardShortModel = mutableListOf<CardShortModel>()
        performInitializeCountSelection()

        for (question in questionsDto.questions) {
            when (question.type) {
                "OPTIONAL" -> {
                    chooseModels.add(
                        ChooseModel(
                            message = question.content,
                            fromWho = question.questioner,
                            options = question.options,
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
                else -> {
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

        updateState {
            copy(
                cards = cardModels,
                chooseCards = chooseModels,
                shortCards = cardShortModel
            )
        }
    }

    private fun performInitializeCountSelection() {
        updateState { copy(countSelection = previewCards.size) }
        Timber.tag("선택9 초기화")
    }

    private fun performToggleLock() {
        updateState { copy(exposure = !exposure) }
        Timber.tag("lock").d(currentState.exposure.toString())
    }

    private fun performUpdateButtonState(position: Int, isSelected: Boolean) {
        updateState {
            copy(
                cards = cards.mapIndexed { index, card ->
                    if (index == position) card.copy(isButtonSelected = isSelected) else card
                }
            )
        }
    }

    private fun performUpdateShortButtonState(position: Int, isSelected: Boolean) {
        updateState {
            copy(
                shortCards = shortCards.mapIndexed { index, card ->
                    if (index == position) card.copy(isButtonSelected = isSelected) else card
                }
            )
        }
    }

    private fun performUpdateChooseButton(position: Int, isSelected: Boolean) {
        updateState {
            copy(
                chooseCards = chooseCards.mapIndexed { index, card ->
                    if (index == position) card.copy(isButtonSelected = isSelected) else card
                }
            )
        }
    }

    private fun performUpdateAllPreviews() {
        val selectedCardIds = currentState.cards.filter { it.isButtonSelected }.map { it.id }.toSet()
        val selectedShortCardIds = currentState.shortCards.filter { it.isButtonSelected }.map { it.id }.toSet()
        val selectedChooseCardIds = currentState.chooseCards.filter { it.isButtonSelected }.map { it.id }.toSet()

        val existingCards = currentState.previewCards.filter {
            it.id in selectedCardIds || it.id in selectedShortCardIds || it.id in selectedChooseCardIds
        }.toMutableList()

        val existingIds = existingCards.map { it.id }.toSet()

        val selectedNewCards = currentState.cards.filter { it.isButtonSelected && it.id !in existingIds }
        val selectedNewShortCards = currentState.shortCards.filter { it.isButtonSelected && it.id !in existingIds }
        val selectedNewChooseCards = currentState.chooseCards.filter { it.isButtonSelected && it.id !in existingIds }

        val newCards = selectedNewCards.map {
            PreviewCardModel(answer = "", question = it.message, type = it.questionType, fromWho = it.fromWho, options = null, id = it.id)
        }

        val newShortCards = selectedNewShortCards.map {
            PreviewCardModel(answer = "", question = it.message, type = it.questionType, fromWho = it.fromWho, options = null, id = it.id)
        }

        val newChooseCards = selectedNewChooseCards.map {
            PreviewCardModel(question = it.message, fromWho = it.fromWho, options = it.options, type = it.type, answer = "", id = it.id)
        }

        val newCardsCount = selectedNewCards.size + existingCards.filter { it.type == 1 }.size
        val newShortCardsCount = selectedNewShortCards.size + existingCards.filter { it.type == 0 }.size
        val newChooseCardsCount = selectedNewChooseCards.size + existingCards.filter { it.type == 2 || it.type == 3 }.size

        Timber.tag("카드 선택 개수").d("New Cards: $newCardsCount, Short Cards: $newShortCardsCount, Choose Cards: $newChooseCardsCount")

        performSetCardCount(newShortCardsCount, newCardsCount, newChooseCardsCount)

        existingCards.addAll(newCards)
        existingCards.addAll(newShortCards)
        existingCards.addAll(newChooseCards)

        updateState {
            copy(
                previewCards = existingCards.distinct(),
                cards = cards.map { it.copy(isButtonSelected = false) },
                chooseCards = chooseCards.map { it.copy(isButtonSelected = false) },
                shortCards = shortCards.map { it.copy(isButtonSelected = false) }
            )
        }

        Timber.tag("미리보기").d(currentState.previewCards.toString())
    }

    private fun performClearAllData() {
        updateState {
            copy(
                previewCards = emptyList(),
                previewChoose = emptyList(),
                isAllAnswersFilled = false
            )
        }
    }

    private fun performClearAll() {
        updateState {
            copy(
                cards = emptyList(),
                chooseCards = emptyList(),
                shortCards = emptyList(),
                isAllAnswersFilled = false
            )
        }
    }

    private fun performPatchCard(previewCardModels: List<PreviewCardModel>, exposure: Boolean, id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.patchCardData(previewCardModels, exposure, id)
                if (response.isSuccess) {
                    Timber.tag("patchCard").d("카드 수정 성공: ${response.message}")
                    sendEvent(CardEvent.PatchCardSuccess)
                } else {
                    Timber.tag("patchCard").d("카드 수정 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { performPatchCard(previewCardModels, exposure, id) },
                        onFailure = { Timber.e("patchCard API Call Failed") }
                    )
                }
            } catch (e: Exception) {
                Timber.e("patchCard 예외 발생: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { performPatchCard(previewCardModels, exposure, id) },
                    onFailure = { Timber.e("patchCard API Call Failed") }
                )
            }
        }
    }

    private fun performCountSelect(selection: Boolean) {
        if (selection) {
            updateState { copy(countSelection = countSelection + 1) }
        } else {
            updateState { copy(countSelection = countSelection - 1) }
        }
        Timber.tag("선택5").d(currentState.countSelection.toString())
    }

    private fun performResetSelect() {
        Timber.tag("initialize3${currentState.countSelection}")
        updateState { copy(countSelection = 0) }
    }

    private fun performUpdateAnswer(answer: String) {
        updateState { copy(answer = answer) }
    }

    fun getAnswerLength(answer: String): Int {
        return answer.length
    }

    fun setCardId(cardId: Int) = onAction(CardAction.SetCardId(cardId))
    fun disableLock(lock: Boolean) = onAction(CardAction.DisableLock(lock))
    fun setCardCount(count: Int, count2: Int, count3: Int) = onAction(CardAction.SetCardCount(count, count2, count3))
    fun updateCardInputStatus(index: Int, isFilled: Boolean) = onAction(CardAction.UpdateCardInputStatus(index, isFilled))
    fun updateCardInputStatusLong(index: Int, isFilled: Boolean) = onAction(CardAction.UpdateCardInputStatusLong(index, isFilled))
    fun updateCardInputStatusChoose(index: Int, isFilled: Boolean) = onAction(CardAction.UpdateCardInputStatusChoose(index, isFilled))
    fun sendData(previewCardModels: List<PreviewCardModel>, exposure: Boolean) = onAction(CardAction.SendData(previewCardModels, exposure))
    fun getAllData() = onAction(CardAction.GetAllData)
    fun updateButtonState(position: Int, isSelected: Boolean) = onAction(CardAction.UpdateButtonState(position, isSelected))
    fun updateShortButtonState(position: Int, isSelected: Boolean) = onAction(CardAction.UpdateShortButtonState(position, isSelected))
    fun updateChooseButton(position: Int, isSelected: Boolean) = onAction(CardAction.UpdateChooseButton(position, isSelected))
    fun updateAllPreviews() = onAction(CardAction.UpdateAllPreviews)
    fun clearAllData() = onAction(CardAction.ClearAllData)
    fun clearAll() = onAction(CardAction.ClearAll)
    fun patchCard(previewCardModels: List<PreviewCardModel>, exposure: Boolean, id: Int) = onAction(CardAction.PatchCard(previewCardModels, exposure, id))
    fun countSelect(selection: Boolean) = onAction(CardAction.CountSelect(selection))
    fun resetSelect() = onAction(CardAction.ResetSelect)
    fun isLockSelected() = onAction(CardAction.ToggleLock)
}
