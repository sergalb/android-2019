package ru.ifmo.rain.balahin.imageviewer.imageRecyclerView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.image_layout.view.*

class ImageViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val imageView: ImageView = root.image
    val descriptionView: TextView = root.image_description
}