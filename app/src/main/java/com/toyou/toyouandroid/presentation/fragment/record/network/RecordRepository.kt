package com.toyou.toyouandroid.presentation.fragment.record.network

import retrofit2.Call

class RecordRepository(private val recordService: RecordService) {

    fun getMyRecord(year: Int, month: Int): Call<DiaryCardResponse> {
        return recordService.getDiarycardsMine(year, month)
    }

    fun getFriendRecordNum(year: Int, month: Int): Call<DiaryCardNumResponse> {
        return recordService.getDiarycardsNumFriend(year, month)
    }

    fun getFriendRecordPerDay(year: Int, month: Int, day: Int): Call<DiaryCardPerDayResponse> {
        return recordService.getDiarycardsPerDayFriend(year, month, day)
    }
}