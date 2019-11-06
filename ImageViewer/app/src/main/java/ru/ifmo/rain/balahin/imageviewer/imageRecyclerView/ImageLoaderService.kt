package ru.ifmo.rain.balahin.imageviewer.imageRecyclerView

import android.app.IntentService
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.annotation.UiThread
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.ifmo.rain.balahin.imageviewer.MainActivity
import ru.ifmo.rain.balahin.imageviewer.dto.Image
import ru.ifmo.rain.balahin.imageviewer.viewModel.DownloadInformation
import ru.ifmo.rain.balahin.imageviewer.viewModel.ImageModel
import java.net.HttpURLConnection
import java.net.URL

class ImageLoaderService : IntentService("Loader thread") {
    companion object {
        val mapper = jacksonObjectMapper()
    }

    var images: MutableList<ImageModel> = mutableListOf()
    override fun onHandleIntent(intent: Intent?) {

        val page = intent?.getIntExtra("PAGE_NUMBER", 1) ?: 1
        try {
            val connection = URL("https://api.unsplash.com/photos?page=$page")
                .openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            //todo hide token
            connection.setRequestProperty(
                "Authorization",
                "Client-ID bdea034e30ac4ae092235fafc732e329e813f140781d5fbd2d6f4ca240e14161"
            )
            val newImages = mapper.readValue<MutableList<Image>>(connection.inputStream)
            images = newImages.asSequence().map {
                try {
                    val imageConnection =
                        URL(it.urls["small"]).openConnection() as HttpURLConnection
                    val bitmap = BitmapFactory.decodeStream(imageConnection.inputStream)
                    val description: String = it.description ?: it.alt_description ?: ""
                    val author: String = if (it.user != null) {
                        it.user.name ?: it.user.userName ?: ""
                    } else {
                        ""
                    }
                    val downloadInformation = DownloadInformation(
                        it.urls["full"] ?: "",
                        it.width,
                        it.height
                    )
                    ImageModel(bitmap, description, author, downloadInformation)
                } catch (exception: Exception) {
                    //todo default image
                    ImageModel(null, "ds", "asa", DownloadInformation(
                        it.urls["full"] ?: "",
                        it.width,
                        it.height
                    ))
                }
            }.toMutableList()
        } catch (e: Exception) {
            //todo correct exception
            println("uuuuuuuuuuuuuups")
        } finally {
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        downloaded()
    }

    @UiThread
    fun downloaded() {
        MainActivity.imageAdapter.images.addAll(images)
        MainActivity.imageAdapter.notifyDataSetChanged()
    }

}