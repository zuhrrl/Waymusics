package com.waymusics.android.component.module

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.waymusics.android.Constant
import com.waymusics.android.R
import com.waymusics.android.component.audio.AudioHelper
import com.waymusics.android.component.network.retro.*
import com.waymusics.android.component.storage.PermissionHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.waymusics.android.component.network.download.MusicDownloader
import com.waymusics.android.component.network.model.Music
import java.lang.Exception


@SuppressLint("StaticFieldLeak")
object ModuleHelper {
    var context: Context? = null
    var recyclerView: RecyclerView? = null
    var btnOpenCloseDrawer: ImageView? = null
    var isReviewFintech: Boolean = false
    var reviewPackname: String? = null
    var isStreamPlaying: Boolean = false
    var loadingBar: ProgressBar? = null
    var playingStatus: TextView? = null
    var musicDuration: TextView? = null
    var activity: Activity? = null
    var musicPlayingAnimation: LottieAnimationView? = null
    var albumIcon: ImageView? = null
    var music: MutableList<Music>? = null
    var musicAdapterPosition: Int? = null

    // update music duration

    fun update() {
        val timer: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // the code in the method run
            }

            override fun onFinish() {}
        }
    }

    fun updateMusicDuration(mediaPlayer: MediaPlayer?) {
        object : CountDownTimer(Long.MAX_VALUE, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                // the code in the method run
                if(isStreamPlaying) {
                    val totalDuration = mediaPlayer?.duration
                    val currentDuration = mediaPlayer?.currentPosition
                    val updateDuration = totalDuration?.minus(currentDuration!!)
                    Log.d("AudioHelper", "Remaining: ${convertMillisecondsToLength(updateDuration!!)}")

                    activity?.runOnUiThread {
                        musicDuration?.text = "Remaining: ${convertMillisecondsToLength(updateDuration!!)}"
                        if(updateDuration == 0) {
                            cancel()
                        }
                    }
                } else {
                    cancel()
                }

            }

            override fun onFinish() {}
        }.start()



    }

    fun getLocalDownloadPath(videoId: String?): String {
        return Constant.API_URL + "/public/musics/" + videoId + ".mp3"
    }

    fun isWriteGranted(): Boolean {
        return PermissionHelper.checkAndRequestPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    // get video length format h:m:s
    fun convertMillisecondsToLength(lengthms: Int): String {
        val h = (lengthms / 1000 / 3600)
        val m = (lengthms / 1000 / 60 % 60)
        val s = (lengthms / 1000 % 60)
        val length = "$m:$s"
        return length
    }

    fun loadFragment(fragment: Fragment){
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_content, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    fun removeFragment(fragment: Fragment) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()
    }

    fun substringName(item: String) : String {
        return item.substring(0, 20)
    }

    fun substringUploadBy(item: String) : String {
        return item.substring(0, 15)
    }

    /*fun test() {
        try {
            val dl_source = ModuleHelper.getLocalDownloadPath(ModuleHelper.music?.get(position)?.ytVideoId)
            ModuleHelper.checkFileInServer(music[position].dl_music_name!!, music[position].ytVideoId!!, dl_source, null, false )
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

     */

    fun streamMp3(dl_video_name: String?, yt_video_id: String?, isSaveLocal: Boolean, dl_source: String?, localFileName: String?) {
        val request = ClientRequest()
        request.yt_video_id = yt_video_id
        request.dl_video_name = dl_video_name
        val retro = Retro().getRetrofitInstance().create(RetroInterface::class.java)
        retro.downloadMp3(request).enqueue(object : Callback<ConvertResponse> {
            override fun onResponse(
                call: Call<ConvertResponse>,
                response: Response<ConvertResponse>
            ) {
                val resp = response.body()
                // we want to detect if response is success
                if(resp?.message.toString().contains("success")) {
                    var dl_path = resp?.data?.yt_dl_path
                    dl_path = Constant.API_URL + "/" + dl_path
                    Log.d("Module Helper", dl_video_name.toString())
                    Log.d("Module Helper", "Dlpath: $dl_path")
                    if(!isSaveLocal) {
                        //MusicDownloader().downloadFile(context!!, "https://api.sobocode.online/public/musics/m3zvVGJrTP8.mp3", localFileName)
                        //AudioHelper.playAudio(context, "https://api.sobocode.online/public/musics/m3zvVGJrTP8.mp3")
                        AudioHelper.playAudio(context!!, dl_path)
                    }
                    else {
                        MusicDownloader().downloadFile(context!!, dl_source, localFileName)
                    }

                }
            }

            override fun onFailure(call: Call<ConvertResponse>, t: Throwable) {
                Log.d("Onfailure ModuleHelper", t.message.toString())
            }


        })
    }

    fun checkFileInServer(dl_music_name: String?, yt_video_id: String?, dl_source: String?, localFileName: String?, isSaveLocal: Boolean) {
        val request = ClientRequest()
        request.yt_video_id = yt_video_id
        val retro = Retro().getRetrofitInstance().create(RetroInterface::class.java)
        retro.fileCheck(request).enqueue(object : Callback<ConvertResponse> {
            override fun onResponse(
                call: Call<ConvertResponse>,
                response: Response<ConvertResponse>
            ) {
                val resp = response.body()
                // we want to detect if response is success
                if(resp?.message.toString().contains("success")) {
                    if(!isSaveLocal) {
                        Log.d("Module Helper", resp?.data?.file_status!!)
                        AudioHelper.playAudio(context, dl_source)
                    }
                    else {
                        MusicDownloader().downloadFile(context!!, dl_source, localFileName)
                        Log.d("Module Helper", resp?.data?.file_status!!)
                    }


                } else {
                    // file is not in our server / so we need to wait server download music
                    Log.d("Module Helper", resp?.data?.file_status!!)
                    Toast.makeText(context, "Please wait getting file from server", Toast.LENGTH_SHORT).show()
                    streamMp3(dl_music_name, yt_video_id, isSaveLocal, dl_source, localFileName)
                }
            }

            override fun onFailure(call: Call<ConvertResponse>, t: Throwable) {
                Log.d("Onfailure ModuleHelper", t.message.toString())
            }


        })
    }

    fun getRandomString(): String {
       return UUID.randomUUID().toString().substring(0,30)
    }

    fun saveToLocal(position: Int) {
        val dl_source = getLocalDownloadPath(music?.get(position)?.ytVideoId)
        val localFileName = music?.get(position)?.dl_music_name_local
        if(!MusicDownloader().isFileDownloaded(localFileName)) {
            albumIcon?.visibility = View.GONE
            musicPlayingAnimation?.setAnimation(R.raw.save_loading)
            musicPlayingAnimation?.playAnimation()
            musicPlayingAnimation?.visibility = View.VISIBLE
            checkFileInServer(music?.get(position)?.dl_music_name!!, music!![position].ytVideoId, dl_source, localFileName, true)
        }
        else {
            loadingBar?.visibility = View.GONE
            Toast.makeText(context, "$localFileName is available in Storage", Toast.LENGTH_SHORT).show()
            val length = 323000
            Log.d("Musicadapter", convertMillisecondsToLength(length))
            Log.d("Musicadapter", "file is exist dont need download")
        }
    }




}