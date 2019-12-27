package ru.ifmo.rain.balahin.imageviewer.imageRecyclerView

import android.app.IntentService
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.annotation.UiThread
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toCollection
import ru.ifmo.rain.balahin.imageviewer.BuildConfig
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

    var images: List<ImageModel> = ArrayList()
    override fun onHandleIntent(intent: Intent?) {
        val page = intent?.getIntExtra("PAGE_NUMBER", 1) ?: 1
        try {
            val connection = URL("https://api.unsplash.com/photos?page=$page")
                .openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            connection.setRequestProperty(
                "Authorization",
                "Client-ID ${BuildConfig.apikey}"
            )
            runBlocking {
                val loadMetaInf = async {
                    mapper.readValue<MutableList<Image>>(connection.inputStream)
                }
                val newImages = loadMetaInf.await()
                val loaderJobs = mutableListOf<Deferred<ImageModel>>()
                for (image in newImages) {
                    loaderJobs += async {
                        try {
                            val imageConnection =
                                URL(image.urls["small"]).openConnection() as HttpURLConnection
                            val bitmap = BitmapFactory.decodeStream(imageConnection.inputStream)
                            val description: String = image.description ?: image.alt_description ?: ""
                            val author: String = if (image.user != null) {
                                image.user.name ?: image.user.userName ?: ""
                            } else {
                                ""
                            }
                            val downloadInformation = DownloadInformation(
                                image.urls["full"] ?: "",
                                image.width,
                                image.height
                            )
                            (ImageModel(bitmap, description, author, downloadInformation))
                        } catch (exception: Exception) {
                            //todo default image
                            ImageModel(
                                null, "ds", "asa", DownloadInformation(
                                    image.urls["full"] ?: "",
                                    image.width,
                                    image.height
                                )
                            )

                        }
                    }
                }
                images = loaderJobs.awaitAll()
            }
        } catch (e: Exception) {
            //todo correct exception
            println("uuuuuuuuuuuuuups")
        } finally {
            stopSelf()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        downloaded(images)
    }

    @UiThread
    fun downloaded(images: List<ImageModel>) {
        MainActivity.imageAdapter.images.addAll(images)
        MainActivity.imageAdapter.notifyDataSetChanged()
    }

}