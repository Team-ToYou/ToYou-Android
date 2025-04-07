package com.toyou.toyouandroid.model.local.calendar

data class DateWithItems(
    val date: String,
    val items: List<CalendarItem>
)