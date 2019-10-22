package com.example.retrofit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
class LauncherActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_TIME_OUT = 1000L
    }

    internal lateinit var handler: Handler
    internal lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler = Handler()
        runnable = Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        handler.postDelayed(runnable, SPLASH_TIME_OUT)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        handler.removeCallbacks(runnable)
    }
}