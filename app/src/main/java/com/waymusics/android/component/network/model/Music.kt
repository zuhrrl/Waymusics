package com.waymusics.android.component.network.model
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Music(
    val musicName: String?,
    val yt_video_uploaded_by: String?,
    val dl_music_name: String?,
    val dl_music_name_local: String?,
    val ytVideoId: String?,
    val ytVideoLength: Int?
)
