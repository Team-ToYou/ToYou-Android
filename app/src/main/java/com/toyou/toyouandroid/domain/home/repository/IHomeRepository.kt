package com.toyou.toyouandroid.domain.home.repository

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCardResponse
import com.toyou.toyouandroid.network.BaseResponse

interface IHomeRepository {
    suspend fun getCardDetail(id: Long): BaseResponse<CardDetail>
    suspend fun getYesterdayCard(): BaseResponse<YesterdayCardResponse>
}
