package com.waymusics.android.component.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlin.Throws
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.waymusics.android.R
import com.waymusics.android.component.module.ModuleHelper
import com.waymusics.android.component.network.retro.Retro
import com.waymusics.android.component.network.retro.RetroInterface
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.Exception
import java.util.*
import java.util.logging.Handler





object AudioHelper {
    private var mediaPlayer: MediaPlayer? = null
    @Throws(Exception::class)
    fun playAudio(context: Context?, url: String?) {
        ModuleHelper.isStreamPlaying = true
        Log.d("AudioHelper", url.toString())
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, Uri.parse(url))
        }

        mediaPlayer!!.setOnPreparedListener {
            ModuleHelper.loadingBar?.visibility = View.GONE
            ModuleHelper.albumIcon?.visibility = View.GONE
            ModuleHelper.musicPlayingAnimation?.visibility = View.VISIBLE
            ModuleHelper.musicPlayingAnimation?.setAnimation(R.raw.music_playing)
            ModuleHelper.musicPlayingAnimation?.playAnimation()
            ModuleHelper.playingStatus?.text = "Playing"

            ModuleHelper.updateMusicDuration(mediaPlayer)
            mediaPlayer!!.start()

        }
        mediaPlayer!!.setOnCompletionListener {
            killMediaPlayer()
        }
        //mediaPlayer.setonbu
        //mediaPlayer!!.prepareAsync()

    }

    fun streamAudio(context: Context, dlpath: String?) {
        val retro = Retro().getRetrofitInstance().create(RetroInterface::class.java)
        Log.d("Musicdownloader", dlpath!!)

        retro.downloadMusic(dlpath).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {



            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Retrofit", t.message.toString())
                //ModelsHelper.getProgressBar()?.visibility = View.GONE
                Toast.makeText(context, "Failed to save this music", Toast.LENGTH_SHORT).show()

            }

        })
    }



    fun killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer!!.reset()
                mediaPlayer!!.release()
                mediaPlayer = null
                ModuleHelper.isStreamPlaying = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playByteArray(url: String?) {

       try {
           val mp = MediaPlayer.create(ModuleHelper.context, Uri.parse(url))
           mp.setAudioAttributes(AudioAttributes.Builder()
               .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
               .setUsage(AudioAttributes.USAGE_MEDIA)
               .build())
           mp.setOnPreparedListener {
               mp.start()
           }
           mp.prepareAsync()
           mp.start()


       }
       catch (e: IOException) {
           Log.d("Audio", "err: $e")
       }


    }

}