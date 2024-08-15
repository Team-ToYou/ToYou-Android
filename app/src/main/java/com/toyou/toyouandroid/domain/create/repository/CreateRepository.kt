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

    // PreviewCardModel 리스트를 AnswerDto로 변환하는 함수
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
    suspend fun postCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ) {
        // AnswerDto로 변환
        val answerDto = convertPreviewCardModelsToAnswerDto(previewCardModels, exposure)

        try {
            // 서버에 POST 요청
            val response = client.postCard(1, request = answerDto)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("post성공", response.message)
            } else {
               Log.d("post실패", response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("post실패", e.message.toString())

        }
    }
}
