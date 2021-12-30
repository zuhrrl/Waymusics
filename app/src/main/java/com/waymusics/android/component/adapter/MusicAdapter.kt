package com.waymusics.android.component.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.waymusics.android.R
import com.waymusics.android.component.module.ModuleHelper
import com.waymusics.android.component.network.model.Music


class MusicAdapter(private val music: MutableList<Music>) : RecyclerView.Adapter<MusicAdapter.AppViewHolder>() {


    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val music_title: TextView = itemView.findViewById(R.id.music_title)
        val music_artist_name: TextView = itemView.findViewById(R.id.music_artist_name)
        val music_length: TextView = itemView.findViewById(R.id.music_length)
        val album_icon: ImageView = itemView.findViewById(R.id.album_icon)
        //val btn_listen_text: TextView = itemView.findViewById(R.id.text_btn_listen)
        val music_playing: LottieAnimationView = itemView.findViewById(R.id.music_playing)
        val btn_save_to_local: LinearLayoutCompat = itemView.findViewById(R.id.btn_linear_download)
        val btn_music_listen: CardView = itemView.findViewById(R.id.card_btn_play)
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

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initOnBindViewHolder(holder: AppViewHolder, position: Int) {

        holder.music_title.text = music[position].musicName
        holder.music_artist_name.text = music[position].yt_video_uploaded_by
        holder.music_length.text =
            "Duration: ${ModuleHelper.convertMillisecondsToLength(music[position].ytVideoLength!!)}"
        // save music to local storage
        holder.btn_save_to_local.setOnClickListener {
            Log.d("Musicadapter", "save clicked")
            //val dl_source = ModuleHelper.getLocalDownloadPath(music[position].ytVideoId)
            //ModuleHelper.loadingBar?.visibility = View.VISIBLE
            ModuleHelper.musicPlayingAnimation = holder.music_playing
            ModuleHelper.albumIcon = holder.album_icon
            ModuleHelper.music = music
            ModuleHelper.isSaveLocalBtnClick = true

            if (ModuleHelper.isWriteGranted()) {

                if (ModuleHelper.isStreamPlaying) {
                    Log.d("Musicposition", "save local stream play")
                    ModuleHelper.saveToLocal(position)

                } else {
                    ModuleHelper.saveToLocal(position)
                }



            }
        }
        holder.btn_music_listen.setOnClickListener {
            if (ModuleHelper.isWriteGranted()) {
                ModuleHelper.stopAudio()
                ModuleHelper.music = music
                ModuleHelper.musicPosition = position
                ModuleHelper.musicTitlePlay?.text = music[position].musicName
                ModuleHelper.musicAlbumPlay?.text = music[position].yt_video_uploaded_by
                ModuleHelper.musicDurationPlay?.text =
                    "Duration: ${ModuleHelper.convertMillisecondsToLength(music[position].ytVideoLength!!)}"
                ModuleHelper.albumIcon = holder.album_icon
                ModuleHelper.musicPlayingAnimation = holder.music_playing
                ModuleHelper.playingStatus = holder.music_artist_name
                if (!ModuleHelper.isStreamPlaying) {
                    ModuleHelper.playAudio(position, music)
                } else {
                    ModuleHelper.stopAudio()
                    ModuleHelper.playAudio(position, music)
                }

            }
        }


    }












}