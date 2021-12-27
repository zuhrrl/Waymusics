package com.waymusics.android.component.network.retro

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class SearchMusicResponse {

    @SerializedName("message")
    @Expose
    var message : String? = null

    @SerializedName("data")
    @Expose
    val data : Data? = null // btw don't forget to use arrray if you have array object LOL :v



    @Keep
    class Data {

        @SerializedName("items")
        @Expose
        var items : List<Items>? = null

        @SerializedName("youtube_download_path")
        @Expose
        var yt_dl_path: String? = null

    }

    @Keep
    class Items {

        @SerializedName("youtube_video_title")
        @Expose
        var youtube_video_title : String? = null

        @SerializedName("youtube_video_id")
        @Expose
        var youtube_video_id : String? = null


        @SerializedName("youtube_video_upload_by")
        @Expose
        var youtube_video_upload_by: String? = null

        @SerializedName("youtube_video_length")
        @Expose
        var youtube_video_length: Int? = null


        @SerializedName("kind")
        @Expose
        var kind : String? = null

        @SerializedName("snippet")
        @Expose
        var snippet : Snippet? = null

        @SerializedName("id")
        @Expose
        var id : ID? = null
    }

    @Keep
    class ID {
        @SerializedName("videoId")
        @Expose
        var video_id : String? = null
    }

    @Keep
    class Snippet {
        @SerializedName("title")
        @Expose
        var title : String? = null
    }



}