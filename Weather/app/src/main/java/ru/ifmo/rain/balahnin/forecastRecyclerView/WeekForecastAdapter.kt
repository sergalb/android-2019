package ru.ifmo.rain.balahnin.forecastRecyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.rain.balahnin.R
import ru.ifmo.rain.balahnin.dayActivity.DayModel

class WeekForecastAdapter(
    var days: List<DayModel>,
    val onClick: (DayModel) -> Unit
) : RecyclerView.Adapter<DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val holder = DayViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.week_forecast_layout,
                parent,
                false
            )
        )
        holder.root.setOnClickListener {
            onClick(days[holder.adapterPosition])
        }


        return holder
    }

    override fun getItemCount() = days.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day= days[position]

        holder.weekDayView.text = day.dayOfWeek
        holder.forecastDetailsView.text = day.forecast
        holder.iconView.setImageResource(day.iconId)

    }


}

