package com.toyou.toyouandroid.utils

import com.toyou.toyouandroid.model.MyDate
import java.util.Calendar

object MyDates {

    fun generateDates(calendar: Calendar, imageMap: Map<Pair<Int, Int>, Map<Int, Int>>): List<MyDate> {
        val dates = mutableListOf<MyDate>()
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)

        // 달의 첫 번째 날의 요일 계산 (월요일이 시작일인 경우)
        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7

        // 전월의 마지막 일로 채우기
        cal.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek)
        for (i in 0 until firstDayOfWeek) {
            dates.add(MyDate(cal.time, null))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 현재 달의 일자 추가
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until daysInMonth) {

            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val imageResId = imageMap[Pair(year, month)]?.get(day)
            dates.add(MyDate(cal.time, imageResId))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 다음 달의 처음 일로 채우기
        val lastDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 6) % 7
        val remainingDays = 6 - lastDayOfWeek

        // 마지막 날이 일요일이 아니거나, 마지막 날이 일요일이지만 다음 달 1일이 월요일이 아닌 경우에만 채우기
        if (lastDayOfWeek != 6 || (remainingDays == 0 && cal.get(Calendar.DAY_OF_MONTH) != 1)) {
            for (i in 0 until remainingDays) {
                dates.add(MyDate(cal.time, null))
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return dates
    }
}