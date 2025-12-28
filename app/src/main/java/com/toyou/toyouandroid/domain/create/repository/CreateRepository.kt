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
class CreateRepository @Inject constructor(private val createService: CreateService) {
    //private val client = AuthNetworkModule.getClient().create(CreateService::class.java)

    //suspend fun getAllData() = client.getQuestions()

    suspend fun getAllData() : BaseResponse<QuestionsDto>{
        return createService.getQuestions()
    }

    suspend fun getHomeEntryData() : BaseResponse<HomeDto>{
        return createService.getHomeEntry()
    }

    suspend fun patchCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
        cardId: Int,
    ): BaseResponse<Unit> {
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)
        return try {
            val response = createService.patchCard(cardId, answerDto)
            // 응답 처리
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

    suspend fun postCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ): BaseResponse<AnswerPost> {
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)

        return try {
            val response = createService.postCard(request = answerDto)

            // 응답 처리
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


    /*suspend fun getHomeEntryData() = client.getHomeEntry()


    suspend fun patchCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
        cardId: Int,
    ) {
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)
        try {
            val response = client.patchCard(card = cardId, request = answerDto)

            // 응답 처리
            if (response.isSuccess) {
                Timber.tag("카드 수정 성공!").d(response.message)
            } else {
                Timber.tag("카드 수정 실패!").d(response.message)
                tokenManager.refreshToken(
                    onSuccess = {
                        CoroutineScope(Dispatchers.IO).launch {
                            patchCardData(previewCardModels, exposure, cardId)
                        }
                    },
                    onFailure = { Timber.e("patchCardData API call failed") }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("카드 수정 실패!").d("Exception: ${e.message}")
        }
    }*/


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

    // 데이터 전송 함수
    /*suspend fun postCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ) : Int {
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)

        try {
            val response = client.postCard(request = answerDto)
            // 응답 처리
            if (response.isSuccess) {
                Timber.tag("post 성공").d(response.message)
                return response.result.id
            } else {
                Timber.tag("post 실패").d(response.message)
                tokenManager.refreshToken(
                    onSuccess = {
                        CoroutineScope(Dispatchers.IO).launch {
                            postCardData(previewCardModels, exposure)
                        }
                    },
                    onFailure = { Timber.e("postToken API call failed") }
                )
                return 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("post 실패").d(e.message.toString())
            return 0
        }
    }*/
}
