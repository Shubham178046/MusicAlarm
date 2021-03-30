package com.musicalarm.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.musicalarm.R

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        initViews()
    }

    private fun initViews() {
        Handler().postDelayed({
                val mainIntent =
                    Intent(this@SplashActivity, DaysActivity::class.java)
                startActivity(mainIntent)
                finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}