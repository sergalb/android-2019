package ru.ifmo.rain.balahin.game_loader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loadingView = findViewById<TextView>(R.id.loading)
        val blinkAnimation = AlphaAnimation(0.1f, 1f)
        blinkAnimation.duration = 1000
        blinkAnimation.repeatMode = Animation.REVERSE
        blinkAnimation.repeatCount = Animation.INFINITE
        loadingView.startAnimation(blinkAnimation)
    }
}
