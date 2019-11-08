package ru.ifmo.rain.balahnin

import android.graphics.Bitmap
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import ru.ifmo.rain.balahnin.dayActivity.DayModel
import ru.ifmo.rain.balahnin.dto.WeekDTO
import java.lang.UnsupportedOperationException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs
import kotlin.math.sign

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Wrapped {
}


class WeekToListDayModels {

    @FromJson
    @Wrapped
    fun daysModelList(weekFromJson: WeekDTO): List<DayModel>{
        val responseDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val viewDateFormat = SimpleDateFormat("dd MMM")
        val res: MutableList<DayModel> = ArrayList()
//        val existingDays: MutableSet<Int> = HashSet()
        for (dayDTO in weekFromJson.list) {
            val date = responseDateFormat.parse(dayDTO.date)
            if (date.hours != 15) continue
            val temp = dayDTO.main.temp.toInt()
            val sign = if (temp > 0) "+" else ""
            val icon = when (dayDTO.weather[0].mainDescription) {
                "Clouds" -> R.drawable.ic_021_cloud
                "Rain" -> R.drawable.ic_021_rain_1
                "Snow" -> R.drawable.ic_021_snowing_3
                else -> R.drawable.ic_021_sun
            }
            res.add(
                DayModel(
                icon,
                viewDateFormat.format(date),
                "$sign$temp°C"
            )
            )

        }
        return res
    }
    @ToJson()
    fun toJson(@Wrapped week: List<DayModel>): WeekDTO {
        throw UnsupportedOperationException()
    }

    @FromJson
    fun fr(json: String) : Bitmap {
        throw UnsupportedOperationException()
    }

    @ToJson
    fun bitmap(icon: Bitmap): DayModel{
        throw UnsupportedOperationException()
    }
}