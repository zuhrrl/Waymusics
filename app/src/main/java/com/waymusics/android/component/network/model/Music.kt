package com.waymusics.android.component.network.model
import androidx.annotation.Keep

@Keep
data class Music(
    val musicName: String?,
    val yt_video_uploaded_by: String?,
    val dl_music_name: String?,
    val dl_music_name_local: String?,
    val ytVideoId: String?,
    val ytVideoLength: Int?,
    var isPlaying: Boolean,
)
