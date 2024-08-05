package com.toyou.toyouandroid.utils

import com.toyou.toyouandroid.model.FriendDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object FriendDates {

    fun generateFriendDates(calendar: Calendar): List<FriendDate> {
        val dates = mutableListOf<FriendDate>()
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        // 달의 첫 번째 날의 요일 계산 (월요일이 시작일인 경우)
        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7

        // 전월의 마지막 일로 채우기
        cal.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek)
        for (i in 0 until firstDayOfWeek) {
            dates.add(FriendDate(cal.time, "0"))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 현재 달의 일자 추가
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until daysInMonth) {

            val dateString = dateFormat.format(cal.time)
            val items = FriendRecordData.getItemsForDate(dateString)
            val peopleCount = items.size

            dates.add(FriendDate(cal.time, peopleCount.toString()))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 다음 달의 처음 일로 채우기
        val lastDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 6) % 7
        val remainingDays = 6 - lastDayOfWeek

        // 마지막 날이 일요일이 아니거나, 마지막 날이 일요일이지만 다음 달 1일이 월요일이 아닌 경우에만 채우기
        if (lastDayOfWeek != 6 || (remainingDays == 0 && cal.get(Calendar.DAY_OF_MONTH) != 1)) {
            for (i in 0 until remainingDays) {
                dates.add(FriendDate(cal.time, "0"))
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return dates
    }

    fun generateFriendDatesForMonths(calendar: Calendar, monthsBefore: Int, monthsAfter: Int): List<FriendDate> {
        val dates = mutableListOf<FriendDate>()
        for (i in -monthsBefore..monthsAfter) {
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.MONTH, i)
            dates.addAll(generateFriendDates(cal))
        }
        return dates
    }
}