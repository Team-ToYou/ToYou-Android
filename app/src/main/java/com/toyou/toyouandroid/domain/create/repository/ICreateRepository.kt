package com.toyou.toyouandroid.domain.create.repository

import com.toyou.toyouandroid.data.create.dto.response.AnswerPost
import com.toyou.toyouandroid.data.create.dto.response.HomeDto
import com.toyou.toyouandroid.data.create.dto.response.QuestionsDto
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.network.BaseResponse

interface ICreateRepository {
    suspend fun getAllData(): BaseResponse<QuestionsDto>
    suspend fun getHomeEntryData(): BaseResponse<HomeDto>
    suspend fun patchCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
        cardId: Int
    ): BaseResponse<Unit>
    suspend fun postCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ): BaseResponse<AnswerPost>
}
