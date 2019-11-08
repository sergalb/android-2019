package ru.ifmo.rain.balahnin.dayActivity

import android.graphics.Bitmap
import ru.ifmo.rain.balahnin.Wrapped

data class DayModel(

    val iconId: Int,
    val dayOfWeek: String,
    val forecast: String
)