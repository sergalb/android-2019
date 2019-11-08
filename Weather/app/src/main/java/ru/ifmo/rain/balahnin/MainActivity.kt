package ru.ifmo.rain.balahnin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.ifmo.rain.balahnin.dayActivity.DayModel
import ru.ifmo.rain.balahnin.dto.WeekDTO
import ru.ifmo.rain.balahnin.forecastRecyclerView.WeekForecastAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var weekForecastAdapter: WeekForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRecylerView()
        run()
    }

    fun setRecylerView() {
        val viewManager = LinearLayoutManager(this)
        weekForecastAdapter = WeekForecastAdapter(emptyList()) {}
        forecast_recycler_view.apply {
            layoutManager = viewManager
            adapter = weekForecastAdapter
            setHasFixedSize(true)
        }
    }

    private var call: Call<List<DayModel>>? = null

    fun run() {
        call = MainApp.app.weatherApi.getFiveDayForecastByCityName("Saint Petersburg")
        call?.enqueue(object : Callback<List<DayModel>> {
            override fun onFailure(call: Call<List<DayModel>>, t: Throwable) {
                Log.e("LOG_TAG", "Failed with", t)
            }

            override fun onResponse(call: Call<List<DayModel>>, response: Response<List<DayModel>>) {
                val body:List<DayModel>? = response.body()
                val today = body?.get(0)
                findViewById<TextView>(R.id.date).text = today?.dayOfWeek
                findViewById<TextView>(R.id.main_weather_value).text = today?.forecast
                findViewById<ImageView>(R.id.main_weather_image).setImageResource(today?.iconId ?: R.drawable.ic_021_sun)
                weekForecastAdapter.days = body ?: emptyList()
                weekForecastAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
        call = null
    }
}


