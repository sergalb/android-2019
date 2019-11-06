package ru.ifmo.rain.balahin.imageviewer.imageActivity

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.annotation.UiThread
import ru.ifmo.rain.balahin.imageviewer.MainActivity
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class FullImageLoaderService : IntentService("Loader thread") {
    companion object {
        var imageView: WeakReference<ImageView>? = null
    }

    lateinit var bitmap: Bitmap
    lateinit var cashInd: CashInd
    override fun onHandleIntent(intent: Intent?) {

        var url = intent?.getStringExtra(MainActivity.INTENT_FULL_LINK)
        val maxWidth: Int = intent?.getIntExtra(ImageActivity.INTENT_MAX_WIDTH, 0) ?: 0
        val maxHeight: Int = intent?.getIntExtra(ImageActivity.INTENT_MAX_HEIGHT, 0) ?: 0
        val width: Int = intent?.getIntExtra(MainActivity.INTENT_WIDTH, maxWidth) ?: 0
        val height: Int = intent?.getIntExtra(MainActivity.INTENT_HEIGHT, maxHeight) ?: 0
        val ind: Int = intent?.getIntExtra(MainActivity.INTENT_INDEX, 0) ?: 0
        val portrait: Boolean = intent?.getBooleanExtra(ImageActivity.INTENT_ORIENTATION, true) ?: true

        cashInd = CashInd(ind, portrait)
        var querySymbol = "?"
        if (width >= maxWidth) {
            url += "${querySymbol}w=${maxWidth}"
            querySymbol = "&"
        }
        if (height >= maxHeight) {
            url += "${querySymbol}h=${maxHeight}"
        }


        try {
            val connection = URL(url)
                .openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            bitmap = BitmapFactory.decodeStream(connection.inputStream)
        } catch (exception: RuntimeException) {
            Log.d("tag", exception.message ?: "error while download full image")
            //todo default image
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        downloaded()
    }

    @UiThread
    fun downloaded() {
        ImageActivity.cash[cashInd] = CashValue(false, bitmap)
        if (imageView == null || imageView!!.get() == null) return

        //dirty hack, but only current thread can change imageView
        imageView!!.get()!!.setImageBitmap(bitmap)
    }
}
