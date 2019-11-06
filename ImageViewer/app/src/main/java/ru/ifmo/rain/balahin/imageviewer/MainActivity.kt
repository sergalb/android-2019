package ru.ifmo.rain.balahin.imageviewer

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ifmo.rain.balahin.imageviewer.imageRecyclerView.ImageAdapter
import ru.ifmo.rain.balahin.imageviewer.imageRecyclerView.ImageScrollListener


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_INTERNET = 478

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            checkPermissionAndSetRecyclerView()
        } else {
            setImageRecyclerView(false)
        }
    }

    private fun setImageRecyclerView(needLoad: Boolean = true) {
        val viewManager = LinearLayoutManager(this)

        val scrollListener = ImageScrollListener(
            viewManager,
            if (needLoad) this else null
        )

        image_recycler_view.apply {
            layoutManager = viewManager
            adapter = imageAdapter
            addOnScrollListener(scrollListener)
        }
    }

    private fun checkPermissionAndSetRecyclerView() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, getString(R.string.explanation_permission), Toast.LENGTH_LONG)
                    .show()
            }

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET),
                PERMISSIONS_REQUEST_INTERNET
            )

        } else {
            setImageRecyclerView()
        }

    }

    companion object {

        val INTENT_FULL_LINK = "FULL_LINK"
        val INTENT_PAGE_NUMBER = "PAGE_NUMBER"
        val INTENT_INDEX = "INDEX"
        val INTENT_WIDTH = "WIDTH"
        val INTENT_HEIGHT = "HEIGHT"
        val imageAdapter = ImageAdapter()

    }
}

