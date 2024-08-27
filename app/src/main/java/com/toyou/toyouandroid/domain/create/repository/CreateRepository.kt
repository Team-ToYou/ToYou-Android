package com.toyou.toyouandroid.domain.create.repository

import android.util.Log
import com.toyou.toyouandroid.data.create.dto.request.Answer
import com.toyou.toyouandroid.data.create.dto.request.AnswerDto
import com.toyou.toyouandroid.data.create.service.CreateService
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.network.RetrofitInstance

class CreateRepository {
    private val client = RetrofitInstance.getInstance().create(CreateService::class.java)

    suspend fun getAllData() = client.getQuestions(1)

    suspend fun getHomeEntryData() = client.getHomeEntry(1)


    suspend fun patchCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
        cardId : Int
    ) {
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)
        try {
            val response = client.patchCard(1, card = cardId, request = answerDto)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("카드수정 성공!", response.message)
            } else {
                Log.d("카드수정 실패!", response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("카드수정 실패!", e.message.toString())

        }
    }

    private fun convertPreviewCardModelsToAnswerDto(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ): AnswerDto {
        val answers = previewCardModels.map { card ->
            Answer(
                id = card.id,
                answer = card.answer ?: ""
            )
        }

        return AnswerDto(
            answers = answers,
            exposure = exposure
        )
    }

    // 데이터 전송 함수
    suspend fun postCardData (
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ) : Int {
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)

        try {
            val response = client.postCard(1, request = answerDto)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("post성공", response.message)
                return response.result.id
            } else {
               Log.d("post실패", response.message)
                return 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("post실패", e.message.toString())
            return 0
        }
    }
}
