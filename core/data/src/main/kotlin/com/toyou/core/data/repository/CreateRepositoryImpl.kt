package com.toyou.core.data.repository

import com.toyou.core.data.mapper.toDomain
import com.toyou.core.data.mapper.toDomainResult
import com.toyou.core.data.mapper.toDomainResultUnit
import com.toyou.core.domain.model.CardPostResult
import com.toyou.core.domain.model.CreateQuestionList
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.HomeInfo
import com.toyou.core.domain.model.PreviewCardModel
import com.toyou.core.domain.repository.ICreateRepository
import com.toyou.core.network.api.CreateService
import com.toyou.core.network.model.create.Answer
import com.toyou.core.network.model.create.AnswerRequest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateRepositoryImpl @Inject constructor(
    private val createService: CreateService
) : ICreateRepository {

    override suspend fun getAllData(): DomainResult<CreateQuestionList> {
        return createService.getQuestions().toDomainResult { it.toDomain() }
    }

    override suspend fun getHomeEntryData(): DomainResult<HomeInfo> {
        return createService.getHomeEntry().toDomainResult { it.toDomain() }
    }

    override suspend fun patchCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
        cardId: Int,
    ): DomainResult<Unit> {
        val answerRequest = convertPreviewCardModelsToAnswerRequest(previewCardModels, exposure)
        return try {
            val response = createService.patchCard(cardId, answerRequest)
            if (response.isSuccess) {
                Timber.tag("카드 수정 성공!").d(response.message)
            } else {
                Timber.tag("카드 수정 실패!").d(response.message)
            }
            response.toDomainResultUnit()
        } catch (e: Exception) {
            Timber.tag("카드 수정 실패!").d("Exception: ${e.message}")
            DomainResult.Error(message = e.message ?: "Unknown error")
        }
    }

    override suspend fun postCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ): DomainResult<CardPostResult> {
        val answerRequest = convertPreviewCardModelsToAnswerRequest(previewCardModels, exposure)

        return try {
            val response = createService.postCard(request = answerRequest)

            if (response.isSuccess) {
                Timber.tag("post 성공").d(response.message)
            } else {
                Timber.tag("post 실패").d(response.message)
            }
            response.toDomainResult { it.toDomain() }
        } catch (e: Exception) {
            Timber.tag("post 실패").d("Exception: ${e.message}")
            DomainResult.Error(message = e.message ?: "Unknown error")
        }
    }

    private fun convertPreviewCardModelsToAnswerRequest(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
    ): AnswerRequest {
        val answers = previewCardModels.map { card ->
            Answer(
                id = card.id,
                answer = card.answer
            )
        }

        return AnswerRequest(
            answers = answers,
            exposure = exposure
        )
    }
}
