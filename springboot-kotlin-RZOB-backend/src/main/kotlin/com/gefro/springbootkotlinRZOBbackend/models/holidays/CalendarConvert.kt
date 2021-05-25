package com.gefro.springbootkotlinRZOBbackend.models.holidays

data class CalendarConvert(
    val holidays: List<String>,
    val nowork: List<String>,
    val preholidays: List<String>
)
