package ru.ifmo.rain.balahin.imageviewer.imageRecyclerView

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.rain.balahin.imageviewer.MainActivity
import java.sql.Time
import java.util.*

class ImageScrollListener(
    private val layoutManager: LinearLayoutManager,
    forLoadContext: Context? = null
) : RecyclerView.OnScrollListener() {

    private val VISIABLE_TRESHOLD = 5

    companion object {
        var page = 1
        var viewedCountImages = 0
    }

    init {
        if (forLoadContext != null) {
            onLoad(forLoadContext)
        }
    }

    private var loading = false
    private var countImages = 0

    private fun isLoading(): Boolean{
        if (!loading) return false
        if (countImages == viewedCountImages) {
            return true
        }
        countImages = viewedCountImages
        loading = false
        return false
    }

    private var lastShownTime = System.currentTimeMillis()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (isLoading()) {
            if (layoutManager.findLastVisibleItemPosition() + 1 >= layoutManager.itemCount) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShownTime >= 3_000) {
                    lastShownTime = currentTime
                    Toast.makeText(recyclerView.context, "images are loaded", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        if (layoutManager.findLastVisibleItemPosition() + VISIABLE_TRESHOLD >= layoutManager.itemCount) {
            onLoad(recyclerView.context)
        }
    }

    private fun onLoad(context: Context) {
        loading = true

        val intent = Intent(context, ImageLoaderService::class.java)
        intent.putExtra(MainActivity.INTENT_PAGE_NUMBER, page++)
        context.startService(intent)

    }


}