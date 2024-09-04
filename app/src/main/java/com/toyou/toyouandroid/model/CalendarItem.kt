package com.toyou.toyouandroid.model

data class DateWithItems(
    val date: String,
    val items: List<CalendarItem>
)

data class CalendarItem(
    val emotion: Int,
    val nickname: String
)
