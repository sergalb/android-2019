package ru.ifmo.rain.balahnin

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.ifmo.rain.balahnin.dayActivity.DayModel
import ru.ifmo.rain.balahnin.dto.WeekDTO

interface WeatherApi {

    @GET("data/2.5/forecast")
    @Wrapped
    fun getFiveDayForecastByCityName(@Query("q") cityName: String,
                                     @Query("units") unit: String = "metric",
                                     @Query("appid") token: String = BuildConfig.apikey): Call<List<DayModel>>

    @GET("data/2.5/forecast")
    fun getFiveDayForecastByCityCoordinate(
        @Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query(
            "units"
        ) unit: String = "metric"
    ): Call<List<DayModel>>


}

fun createWeatherApi(): WeatherApi {
    val client = OkHttpClient()
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(WeekToListDayModels())
        .build()
    val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))

        .baseUrl("http://api.openweathermap.org/")
        .build()
    val api: WeatherApi = retrofit.create(WeatherApi::class.java)
    return api
}