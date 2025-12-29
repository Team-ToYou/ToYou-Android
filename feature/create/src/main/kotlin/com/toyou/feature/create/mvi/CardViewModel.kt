package com.toyou.feature.create.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.CardModel
import com.toyou.core.domain.model.CardShortModel
import com.toyou.core.domain.model.ChooseModel
import com.toyou.core.domain.model.CreateQuestionList
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.PreviewCardModel
import com.toyou.core.domain.model.PreviewChooseModel
import com.toyou.core.domain.repository.ICreateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val errorHandler: ApiErrorHandler,
    private val repository: ICreateRepository
) : MviViewModel<CardUiState, CardEvent, CardAction>(
    initialState = CardUiState()
) {
    private var inputStatus: MutableList<Boolean> = mutableListOf()
    private var inputLongStatus: MutableList<Boolean> = mutableListOf()
    private var inputChooseStatus: MutableList<Boolean> = mutableListOf()
    var toastShow: Boolean = false

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
    }

    private fun performSendData(previewCardModels: List<PreviewCardModel>, exposure: Boolean) {
        viewModelScope.launch {
            try {
                when (val result = repository.postCardData(previewCardModels, exposure)) {
                    is DomainResult.Success -> {
                        Timber.tag("sendData").d("카드 전송 성공")
                        updateState { copy(cardId = result.data.cardId) }
                        sendEvent(CardEvent.SendDataSuccess)
                    }
                    is DomainResult.Error -> {
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performSendData(previewCardModels, exposure) },
                            onFailure = { Timber.e("sendData API Call Failed") },
                            tag = "CardViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e("sendData exception: ${e.message}")
            }
        }
    }

    private fun performGetAllData() {
        viewModelScope.launch {
            try {
                when (val result = repository.getAllData()) {
                    is DomainResult.Success -> {
                        val questionList = result.data
                        if (currentState.previewCards.isEmpty()) {
                            performMapToModels(questionList)
                        } else {
                            performMapToPatchModels(questionList)
                        }
                    }
                    is DomainResult.Error -> {
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performGetAllData() },
                            onFailure = { },
                            tag = "CardViewModel"
                        )
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun performMapToPatchModels(questionList: CreateQuestionList) {
        val cardModels = mutableListOf<CardModel>()
        val chooseModels = mutableListOf<ChooseModel>()
        val cardShortModel = mutableListOf<CardShortModel>()
        performInitializeCountSelection()

        for (question in questionList.questions) {
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

    private fun performMapToModels(questionList: CreateQuestionList) {
        val cardModels = mutableListOf<CardModel>()
        val chooseModels = mutableListOf<ChooseModel>()
        val cardShortModel = mutableListOf<CardShortModel>()
        performInitializeCountSelection()

        for (question in questionList.questions) {
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
    }

    private fun performToggleLock() {
        updateState { copy(exposure = !exposure) }
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
                when (val result = repository.patchCardData(previewCardModels, exposure, id)) {
                    is DomainResult.Success -> {
                        sendEvent(CardEvent.PatchCardSuccess)
                    }
                    is DomainResult.Error -> {
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performPatchCard(previewCardModels, exposure, id) },
                            onFailure = { },
                            tag = "CardViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e("patchCard exception: ${e.message}")
            }
        }
    }

    private fun performCountSelect(selection: Boolean) {
        if (selection) {
            updateState { copy(countSelection = countSelection + 1) }
        } else {
            updateState { copy(countSelection = countSelection - 1) }
        }
    }

    private fun performResetSelect() {
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
