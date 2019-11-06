package ru.ifmo.rain.balahin.imageviewer.viewModel

import android.graphics.Bitmap

data class ImageModel (
    val image: Bitmap?,
    val description: String = "",
    val author: String = "",
    val downloadInformation: DownloadInformation
)

data class DownloadInformation(
    val url: String,
    val width: Int,
    val height: Int
)