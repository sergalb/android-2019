package ru.ifmo.rain.balahnin.forecastRecyclerView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.week_forecast_layout.view.*

class DayViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val iconView: ImageView = root.icon
    val forecastDetailsView: TextView = root.forecast_details
    val weekDayView: TextView = root.week_day
}