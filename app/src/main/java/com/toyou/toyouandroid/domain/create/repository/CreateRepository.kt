package com.toyou.toyouandroid.domain.create.repository

import com.toyou.toyouandroid.data.create.service.CreateService
import com.toyou.toyouandroid.data.create.dto.request.Answer
import com.toyou.toyouandroid.data.create.dto.request.AnswerDto
import com.toyou.toyouandroid.data.create.dto.response.AnswerPost
import com.toyou.toyouandroid.data.create.dto.response.HomeDto
import com.toyou.toyouandroid.data.create.dto.response.QuestionsDto
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.network.BaseResponse
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateRepositoryImpl @Inject constructor(
    private val createService: CreateService
) : ICreateRepository {

    override suspend fun getAllData(): BaseResponse<QuestionsDto> {
        return createService.getQuestions()
    }

    override suspend fun getHomeEntryData(): BaseResponse<HomeDto> {
        return createService.getHomeEntry()
    }

    override suspend fun patchCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
        cardId: Int,
    ): BaseResponse<Unit> {
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)
        return try {
            val response = createService.patchCard(cardId, answerDto)
            if (response.isSuccess) {
                Timber.tag("카드 수정 성공!").d(response.message)
            } else {
                Timber.tag("카드 수정 실패!").d(response.message)
            }
            response
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("카드 수정 실패!").d("Exception: ${e.message}")
            return createService.patchCard(cardId, answerDto)
        }
    }

    override suspend fun postCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ): BaseResponse<AnswerPost> {
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)

        return try {
            val response = createService.postCard(request = answerDto)

            if (response.isSuccess) {
                Timber.tag("post 성공").d(response.message)
            } else {
                Timber.tag("post 실패").d(response.message)
            }
            response
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("post 실패").d("Exception: ${e.message}")
            return createService.postCard(answerDto)
        }
    }

    private fun convertPreviewCardModelsToAnswerDto(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
    ): AnswerDto {
        val answers = previewCardModels.map { card ->
            Answer(
                id = card.id,
                answer = card.answer
            )
        }

        return AnswerDto(
            answers = answers,
            exposure = exposure
        )
    }
}
