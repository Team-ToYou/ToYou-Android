package com.toyou.toyouandroid.presentation.fragment.record.network

import retrofit2.Call

class RecordRepository(private val recordService: RecordService) {

    fun getMyRecord(userId: Int, year: Int, month: Int): Call<DiaryCardResponse> {
        return recordService.getDiarycardsMine(userId, year, month)
    }

    fun getFriendRecordNum(userId: Int, year: Int, month: Int): Call<DiaryCardNumResponse> {
        return recordService.getDiarycardsNumFriend(userId, year, month)
    }

    fun getFriendRecordPerDay(userId: Int, year: Int, month: Int, day: Int): Call<DiaryCardPerDayResponse> {
        return recordService.getDiarycardsPerDayFriend(userId, year, month, day)
    }
}