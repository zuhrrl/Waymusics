package com.waymusics.android.component.network.retro

import androidx.annotation.Keep
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


@Keep
interface RetroInterface {

    // get all musics lists
    @POST("backend/get/musics")
    fun getMusicList(@Body clientRequest: ClientRequest
    ) : Call<SearchMusicResponse>

    // download to server
    @POST("backend/getmp3")
    fun downloadMp3(@Body clientRequest: ClientRequest
    ) : Call<ConvertResponse>

    // check if file exist in server
    @POST("backend/file/check")
    fun fileCheck(@Body clientRequest: ClientRequest
    ) : Call<ConvertResponse>


    @GET
    @Streaming
    fun downloadMusic(
        @Url url: String?
    ) : Call<ResponseBody>




}