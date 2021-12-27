package com.waymusics.android.component.network.retro

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class ClientRequest {
    @SerializedName("keyword")
    @Expose
    var keyword : String? = null

    @SerializedName("page")
    @Expose
    var page : Int? = null

    @SerializedName("download_path")
    @Expose
    var downloadPath : String? = null

    @SerializedName("youtube_video_id")
    @Expose
    var yt_video_id : String? = null

    @SerializedName("youtube_video_name")
    @Expose
    var dl_video_name : String? = null

    @SerializedName("youtube_file_type")
    @Expose
    var yt_file_type : String? = null


}