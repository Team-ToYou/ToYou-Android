package com.toyou.toyouandroid.utils.calendar

import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.local.calendar.CalendarItem
import com.toyou.toyouandroid.model.local.calendar.DateWithItems

object FriendRecordData {

    val data: List<DateWithItems> = listOf(
        DateWithItems(
            date = "20240801",
            items = listOf(
                CalendarItem(R.drawable.home_stamp_option_normal, "태연"),
                CalendarItem(R.drawable.home_stamp_option_exciting, "승원"),
                CalendarItem(R.drawable.home_stamp_option_happy, "현정"),
                CalendarItem(R.drawable.home_stamp_option_anxiety, "유은"),
                CalendarItem(R.drawable.home_stamp_option_exciting, "혜음킹"),
                CalendarItem(R.drawable.home_stamp_option_anxiety, "태연킹"),
                CalendarItem(R.drawable.home_stamp_option_happy, "승원킹"),
                CalendarItem(R.drawable.home_stamp_option_normal, "현정킹"),
                CalendarItem(R.drawable.home_stamp_option_anxiety, "유은킹")
            )
        ),
        DateWithItems(
            date = "20240802",
            items = listOf(
                CalendarItem(R.drawable.home_stamp_option_normal, "태연"),
                CalendarItem(R.drawable.home_stamp_option_exciting, "승원"),
                CalendarItem(R.drawable.home_stamp_option_happy, "현정"),
                CalendarItem(R.drawable.home_stamp_option_anxiety, "유은"),
                CalendarItem(R.drawable.home_stamp_option_anxiety, "혜음"),
                CalendarItem(R.drawable.home_stamp_option_normal, "태연킹왕짱"),
                CalendarItem(R.drawable.home_stamp_option_happy, "현정킹왕짱"),
                CalendarItem(R.drawable.home_stamp_option_anxiety, "유은킹왕짱"),
                CalendarItem(R.drawable.home_stamp_option_normal, "혜음킹왕짱"),
                CalendarItem(R.drawable.home_stamp_option_upset, "승원"),
                CalendarItem(R.drawable.home_stamp_option_exciting, "현정"),
                CalendarItem(R.drawable.home_stamp_option_normal, "유은")
            )
        )
    )

    fun getItemsForDate(date: String): List<CalendarItem> {
        return data.find { it.date == date }?.items ?: emptyList()
    }
}