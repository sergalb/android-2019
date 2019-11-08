package ru.ifmo.rain.balahnin

import android.app.Application

class MainApp: Application() {
    lateinit var weatherApi: WeatherApi
        private set

    override fun onCreate() {
        super.onCreate()
        weatherApi = createWeatherApi()
        app = this
    }

    companion object {
        lateinit var app: MainApp
            private set
    }
}