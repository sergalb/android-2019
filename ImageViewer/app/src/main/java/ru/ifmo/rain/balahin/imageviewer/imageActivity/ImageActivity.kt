package ru.ifmo.rain.balahin.imageviewer.imageActivity

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ru.ifmo.rain.balahin.imageviewer.MainActivity
import ru.ifmo.rain.balahin.imageviewer.R
import java.lang.ref.WeakReference

class ImageActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    var maxWidth: Int? = null
    var maxHeight: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageInd = intent.getIntExtra(MainActivity.INTENT_INDEX, -1)
        assert(imageInd != -1)

        val portrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        setContentView(R.layout.image_activity)
        imageView = findViewById(R.id.full_image)
        defineWidthAndHeight()

        val cashInd = CashInd(imageInd, portrait)
        if (cash.containsKey(cashInd)) {
            val cashValue = cash[cashInd]
            if (!cashValue!!.isLoading) {
                imageView.setImageBitmap(cashValue.bitmap)
            } else {
                FullImageLoaderService.imageView = WeakReference(imageView)
            }
            return
        }
        cash[cashInd] = CashValue(true, null)
        FullImageLoaderService.imageView = WeakReference(imageView)
        val serviceIntent = Intent(this, FullImageLoaderService::class.java)
        serviceIntent.putExtra(
            MainActivity.INTENT_FULL_LINK,
            intent.getStringExtra(MainActivity.INTENT_FULL_LINK)
        )
        serviceIntent.putExtra(
            MainActivity.INTENT_WIDTH,
            intent.getIntExtra(MainActivity.INTENT_WIDTH, 0)
        )
        serviceIntent.putExtra(
            MainActivity.INTENT_HEIGHT,
            intent.getIntExtra(MainActivity.INTENT_HEIGHT, 0)
        )
        serviceIntent.putExtra(MainActivity.INTENT_INDEX, imageInd)
        serviceIntent.putExtra(INTENT_ORIENTATION, portrait)
        serviceIntent.putExtra(INTENT_MAX_WIDTH, maxWidth)
        serviceIntent.putExtra(INTENT_MAX_HEIGHT, maxHeight)

        startService(serviceIntent)
    }


    private fun defineWidthAndHeight() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        maxWidth = size.x
        maxHeight = size.y
    }

    override fun onDestroy() {
        super.onDestroy()
        if (FullImageLoaderService.imageView == imageView) {
            FullImageLoaderService.imageView?.clear()
        }
    }


    companion object {
        val cash: MutableMap<CashInd, CashValue> = HashMap()
        val INTENT_MAX_WIDTH = "MAX_WIDTH"
        val INTENT_MAX_HEIGHT = "MAX_HEIGHT"
        val INTENT_ORIENTATION = "ORIENTATION"
    }
}

data class CashInd(
    val ind: Int,
    var isPortrait: Boolean
)

data class CashValue(
    var isLoading: Boolean,
    var bitmap: Bitmap?
)