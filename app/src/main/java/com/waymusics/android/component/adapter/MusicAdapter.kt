package com.waymusics.android.component.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.media.AudioAttributes
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.waymusics.android.R
import com.waymusics.android.component.network.model.Music
import com.waymusics.android.component.module.ModuleHelper

import android.media.MediaPlayer
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.waymusics.android.component.audio.AudioHelper
import com.waymusics.android.component.network.download.MusicDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.DateFormat


class MusicAdapter(private val music: MutableList<Music>) : RecyclerView.Adapter<MusicAdapter.AppViewHolder>() {

    var activity: Activity? = null


    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val music_title: TextView = itemView.findViewById(R.id.music_title)
        val music_artist_name: TextView = itemView.findViewById(R.id.music_artist_name)
        val music_length: TextView = itemView.findViewById(R.id.music_length)
        val album_icon: ImageView = itemView.findViewById(R.id.album_icon)
        val btn_listen_text: TextView = itemView.findViewById(R.id.text_btn_listen)
        val music_playing: LottieAnimationView = itemView.findViewById(R.id.music_playing)
        val btn_save_to_local: LinearLayoutCompat = itemView.findViewById(R.id.btn_linear_download)
        val btn_music_listen: LinearLayoutCompat = itemView.findViewById(R.id.btn_music_listen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        // Inflate layout
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val appListView = layoutInflater.inflate(R.layout.music_card_item, parent, false)
        return AppViewHolder(appListView)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        initOnBindViewHolder(holder, position)
    }


    override fun getItemCount(): Int {
        return music.size
    }

    @SuppressLint("SetTextI18n")
    private fun initOnBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.music_title.text = music[position].musicName
        holder.music_artist_name.text = music[position].yt_video_uploaded_by
        holder.music_length.text = "Length: ${ModuleHelper.convertMillisecondsToLength(music[position].ytVideoLength!!)}"

        // save music to local storage
        holder.btn_save_to_local.setOnClickListener {
            val dl_source = ModuleHelper.getLocalDownloadPath(music[position].ytVideoId)
            //ModuleHelper.loadingBar?.visibility = View.VISIBLE
            ModuleHelper.musicPlayingAnimation = holder.music_playing
            ModuleHelper.albumIcon = holder.album_icon
            ModuleHelper.music = music
            ModuleHelper.musicAdapterPosition = position


            if(ModuleHelper.isWriteGranted()) {
                ModuleHelper.saveToLocal(position)

            }
        }


        holder.btn_music_listen.setOnClickListener {
            //ModuleHelper.recyclerView?.visibility = View.GONE
            //ModuleHelper.loadingBar?.visibility = View.VISIBLE


            if(!ModuleHelper.isStreamPlaying) {

                holder.album_icon.visibility = View.GONE
                holder.music_playing.setAnimation(R.raw.loading_stream)
                holder.music_playing.playAnimation()
                holder.music_playing.visibility = View.VISIBLE
                ModuleHelper.musicPlayingAnimation = holder.music_playing
                ModuleHelper.albumIcon = holder.album_icon

                if(!MusicDownloader().isFileDownloaded(music[position].dl_music_name_local)) {
                    val dl_source = ModuleHelper.getLocalDownloadPath(music[position].ytVideoId)
                    ModuleHelper.checkFileInServer(music[position].dl_music_name!!, music[position].ytVideoId!!, dl_source, null, false )
                    Log.d("MusicAdapter", music[position].ytVideoId!!)
                    holder.btn_listen_text.text = "STOP"
                    holder.music_artist_name.text = "Getting file from server..."
                    ModuleHelper.playingStatus = holder.music_artist_name
                    ModuleHelper.musicDuration = holder.music_length
                }
                else {
                    // playing audio from local file
                    Log.d("Musicadapter", "user have file to listen this music")
                    try {
                        val path = File("/storage/emulated/0/Music/")
                        val localpath = File(path, "${music[position].dl_music_name_local}-Waymusics.mp3")
                        AudioHelper.playAudio(ModuleHelper.context, localpath.absolutePath.toString())
                        //ModuleHelper.streamMp3(music[position].dl_music_name!!, music[position].ytVideoId!!, false, null, null)
                        Log.d("MusicAdapter", music[position].ytVideoId!!)
                        holder.btn_listen_text.text = "STOP"
                        holder.music_artist_name.text = "Getting file from server..."
                        ModuleHelper.playingStatus = holder.music_artist_name
                        ModuleHelper.musicDuration = holder.music_length

                    } catch (e: Exception) {
                        println(e.toString())
                    }
                }

            }
            else {
                ModuleHelper.isStreamPlaying = false
                ModuleHelper.loadingBar?.visibility = View.GONE
                holder.album_icon.visibility = View.VISIBLE
                holder.music_playing.visibility = View.GONE
                AudioHelper.killMediaPlayer()
                holder.music_artist_name.text = music[position].yt_video_uploaded_by
                holder.btn_listen_text.text = "DENGARKAN"
                // stop animation
                ModuleHelper.musicPlayingAnimation!!.pauseAnimation()
                holder.music_length.text = "Length: ${ModuleHelper.convertMillisecondsToLength(music[position].ytVideoLength!!)}"
            }

        }

    }

    @Throws(IOException::class)
    private fun streamMusic(url: String, onStart: MediaPlayer.() -> Unit) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        MediaPlayer().apply {
            setAudioAttributes(audioAttributes)
            setDataSource(url)

            setOnPreparedListener {
                //isEnabled = false
                start()
                //setImageDrawable(context.getDrawableResource(R.drawable.ic_baseline_stop_24))
            }

            setOnCompletionListener {
                //setImageDrawable(context.getDrawableResource(R.drawable.ic_baseline_volume_up_24))
                release()
                //isEnabled = true
            }
        }.onStart()
    }











}