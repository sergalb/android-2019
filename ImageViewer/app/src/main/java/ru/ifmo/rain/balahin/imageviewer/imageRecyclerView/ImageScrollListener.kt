package ru.ifmo.rain.balahin.imageviewer.imageRecyclerView

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.rain.balahin.imageviewer.MainActivity

class ImageScrollListener(
    private val layoutManager: LinearLayoutManager,
    forLoadContext: Context? = null
) : RecyclerView.OnScrollListener() {

    private val VISIABLE_TRESHOLD = 5
    companion object {
        var page = 1
    }

    init {
        if (forLoadContext != null) {
            onLoad(forLoadContext)
        }
    }

    private var loading = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (loading) {
            if (layoutManager.findLastVisibleItemPosition() + 1 >= layoutManager.itemCount) {
                Toast.makeText(recyclerView.context, "images are loaded", Toast.LENGTH_SHORT)
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

        loading = false
    }



}