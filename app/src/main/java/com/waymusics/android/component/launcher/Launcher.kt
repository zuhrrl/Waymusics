package com.waymusics.android.component.launcher

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

import com.waymusics.android.BuildConfig
import com.waymusics.android.R
import com.waymusics.android.component.ui.Homepage
import java.util.*

class Launcher : AppCompatActivity() {
    private lateinit var iconSplash: ImageView
    lateinit var textView: TextView
    private lateinit var textTitle: TextView
    private lateinit var mainIntent: Intent
    private lateinit var handler: Handler

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // connect to server data
        //ServerHelper.getServerData(this)
        setContentView(R.layout.launcher_activity)
        iconSplash = findViewById(R.id.id_fps_icon)
        textView = findViewById(R.id.stable_version)
        textView.text = BuildConfig.VERSION_NAME
        val animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        Glide.with(this).load(R.drawable.ic_launcher).into(iconSplash)
        iconSplash.startAnimation(animation)
        mainIntent = Intent(this, Homepage::class.java)

        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            launchActivity()
        }, 500)






    }

    private fun launchActivity() {

        handler.postDelayed({
            startActivity(mainIntent)
            finish()
        }, 5000)
    }



}