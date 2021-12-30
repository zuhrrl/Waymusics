package com.waymusics.android.component.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import com.waymusics.android.component.module.ModuleHelper

object AudioHelper {
    private var mediaPlayer: MediaPlayer? = null


    @SuppressLint("NotifyDataSetChanged")
    @Throws(Exception::class)
    fun playAudio(context: Context?, url: String?) {
        Log.d("AudioHelper", url.toString())
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, Uri.parse(url))
        }

        mediaPlayer!!.setOnPreparedListener {
            ModuleHelper.mediaPlayer = mediaPlayer
            mediaPlayer!!.start()

            if(mediaPlayer!!.isPlaying && mediaPlayer !== null) {
                ModuleHelper.playingStatus?.text = ModuleHelper.music?.get(ModuleHelper.musicPosition!!)?.yt_video_uploaded_by
                ModuleHelper.musicPlayingAnimation?.pauseAnimation()
                ModuleHelper.musicPlayingAnimation?.visibility = View.GONE
                ModuleHelper.albumIcon?.visibility = View.VISIBLE
                ModuleHelper.isStreamPlaying = true
                ModuleHelper.updateMusicDuration(mediaPlayer)
            }

        }
        mediaPlayer!!.setOnCompletionListener {
            killMediaPlayer()
        }
        //mediaPlayer.setonbu
        //mediaPlayer!!.prepareAsync()



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


}