package ru.ifmo.rain.balahnin.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.squareup.moshi.Json

data class DayDTO (

    val main: Characteristics,

    val weather: List<Weather> = emptyList(),

    val clouds: Map<String, Int> = emptyMap(),

    val wind: Wind,

    val rain: Map<String, Double> = emptyMap(),

    val snow: Map<String, Double> = emptyMap(),

    @Json(name = "dt_txt")
    val date: String

)


data class Characteristics (
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)


data class Weather(
    @Json(name ="main")
    val mainDescription: String,

    val description: String? = null
)


data class Wind(
    val speed: Double,
    @Json(name = "deg")
    val direction: Double
)