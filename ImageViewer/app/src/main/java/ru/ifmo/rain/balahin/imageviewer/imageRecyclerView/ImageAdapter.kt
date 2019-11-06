package ru.ifmo.rain.balahin.imageviewer.imageRecyclerView

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.rain.balahin.imageviewer.MainActivity
import ru.ifmo.rain.balahin.imageviewer.R
import ru.ifmo.rain.balahin.imageviewer.imageActivity.ImageActivity
import ru.ifmo.rain.balahin.imageviewer.viewModel.ImageModel

class ImageAdapter : RecyclerView.Adapter<ImageViewHolder>() {
    val images: MutableList<ImageModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val holder = ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.image_layout,
                parent,
                false
            )
        )
        holder.root.setOnClickListener {
            val image = images[holder.adapterPosition]
            val ind = holder.adapterPosition
            val intent = Intent(holder.root.context, ImageActivity::class.java)
            intent.putExtra(MainActivity.INTENT_FULL_LINK, image.downloadInformation.url)
            intent.putExtra(MainActivity.INTENT_WIDTH, image.downloadInformation.width)
            intent.putExtra(MainActivity.INTENT_HEIGHT, image.downloadInformation.height)
            intent.putExtra(MainActivity.INTENT_INDEX, ind)
            startActivity(holder.root.context, intent, null)
        }

        return holder
    }

    override fun getItemCount() = images.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        val author = if (image.author.isNotEmpty()) {
            " by ${image.author}"
        } else {
            ""
        }
        //todo localization
        holder.descriptionView.text = "${image.description}$author"
        holder.imageView.setImageBitmap(image.image)
    }


}