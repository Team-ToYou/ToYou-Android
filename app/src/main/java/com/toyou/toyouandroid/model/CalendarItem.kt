package com.toyou.toyouandroid.model

data class CalendarItem(
    val emotion: Int,
    val nickname: String
)

data class DateWithItems(
    val date: String,
    val items: List<CalendarItem>
)
