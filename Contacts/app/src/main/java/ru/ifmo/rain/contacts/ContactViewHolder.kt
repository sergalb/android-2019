package ru.ifmo.rain.contacts

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contact_layout.view.*

class ContactViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val name: TextView = root.name
    val number: TextView = root.number
}