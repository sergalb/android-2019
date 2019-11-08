package ru.ifmo.rain.balahnin.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeekDTO (
    val list: List<DayDTO> = emptyList()
)