package com.waymusics.android.component.network.retro

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class ConvertResponse {

    @SerializedName("message")
    @Expose
    var message : String? = null

    @SerializedName("data")
    @Expose
    val data : Data? = null // btw don't forget to use arrray if you have array object LOL :v



    @Keep
    class Data {

        @SerializedName("youtube_download_path")
        @Expose
        var yt_dl_path: String? = null

        @SerializedName("file_status")
        @Expose
        var file_status: String? = null

    }





}