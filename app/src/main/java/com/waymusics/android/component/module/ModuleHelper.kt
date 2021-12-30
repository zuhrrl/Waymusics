package com.waymusics.android.component.module

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.text.Html
import android.text.Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat.fromHtml
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waymusics.android.Constant
import com.waymusics.android.R
import com.waymusics.android.component.adapter.MusicAdapter
import com.waymusics.android.component.audio.AudioHelper
import com.waymusics.android.component.builder.MusicBuilder
import com.waymusics.android.component.network.download.MusicDownloader
import com.waymusics.android.component.network.model.Music
import com.waymusics.android.component.network.retro.*
import com.waymusics.android.component.storage.PermissionHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*


@SuppressLint("StaticFieldLeak")
object ModuleHelper {
    var context: Context? = null
    var recyclerView: RecyclerView? = null
    var btnOpenCloseDrawer: ImageView? = null
    var isStreamPlaying: Boolean = false
    var loadingBar: ProgressBar? = null
    var playingStatus: TextView? = null
    var musicDuration: TextView? = null
    var activity: Activity? = null
    var musicPlayingAnimation: LottieAnimationView? = null
    var albumIcon: ImageView? = null
    var music: MutableList<Music>? = null
    var musicUploadedBy: TextView? = null
    var isNewMusicSearch: Boolean = false
    var isSaveLocalBtnClick: Boolean = false
    var countDownTimer: CountDownTimer? = null
    var musicAdapter: MusicAdapter? = null
    var mediaPlayer: MediaPlayer? = null
    var musicTitlePlay: TextView? = null
    var musicAlbumPlay: TextView? = null
    var musicPauseBtn: ImageView? = null
    var musicDurationPlay: TextView? = null
    var musicPosition: Int? = null
    var musicOverlay: CardView? = null



    fun updateMusicDuration(mediaPlayer: MediaPlayer?) {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                // the code in the method run
                if(isStreamPlaying) {

                    //Log.d("ModuleHelper", "Mediaplayerstatus: "+mediaPlayer?.isPlaying)
                    //Log.d("ModuleHelper", "Mediaplayerstatus: "+mediaPlayer?.currentPosition)




                    activity?.runOnUiThread {

                        val totalDuration = mediaPlayer?.duration
                        val currentDuration = mediaPlayer?.currentPosition
                        val updateDuration = totalDuration?.minus(currentDuration!!)
                        //Log.d("AudioHelper", "Remaining: ${convertMillisecondsToLength(updateDuration!!)}")


                        musicDurationPlay?.text = "Remaining: ${convertMillisecondsToLength(updateDuration!!)}"
                        if(updateDuration == 0) {
                            cancel()
                        }
                    }
                }
                else {
                    isSaveLocalBtnClick = false
                    cancel()
                }

            }

            override fun onFinish() {}
        }
        (countDownTimer as CountDownTimer).start()



    }

    fun resetCountDown() {
        countDownTimer?.cancel()
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

    private fun checkFileInServer(dl_music_name: String?, yt_video_id: String?, dl_source: String?, localFileName: String?, isSaveLocal: Boolean) {
        playingStatus?.text = "Getting file from server..."
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
                        // save to local
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
        Log.d("Modulehelper", "trying to call saveToLocal()")
        val dlSource = getLocalDownloadPath(music?.get(position)?.ytVideoId)
        val localFileName = music?.get(position)?.dl_music_name_local
        if(!MusicDownloader().isFileDownloaded(localFileName)) {
            albumIcon?.visibility = View.GONE
            musicPlayingAnimation?.setAnimation(R.raw.save_loading)
            musicPlayingAnimation?.playAnimation()
            musicPlayingAnimation?.visibility = View.VISIBLE
            checkFileInServer(music?.get(position)?.dl_music_name!!, music!![position].ytVideoId, dlSource, localFileName, true)
        }
        else {
            loadingBar?.visibility = View.GONE
            musicPlayingAnimation?.pauseAnimation()
            musicPlayingAnimation?.visibility = View.GONE
            albumIcon?.visibility = View.VISIBLE
            Toast.makeText(context, "$localFileName is available in Storage", Toast.LENGTH_SHORT).show()
            val length = 323000
            Log.d("Musicadapter", convertMillisecondsToLength(length))
            Log.d("Musicadapter", "file is exist dont need download")
        }
    }

    fun initHomepageMusicList(keyword: String?, musicList : MutableList<Music>, musicAdapter: MusicAdapter) {
        val request = ClientRequest()
        request.keyword = keyword
        val retro = Retro().getRetrofitInstance().create(RetroInterface::class.java)
        retro.getMusicList(request).enqueue(object : Callback<SearchMusicResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SearchMusicResponse>,
                response: Response<SearchMusicResponse>
            ) {
                // finally we already get response from backend
                val resp = response.body()
                // we want to detect if response is success
                if(resp?.message.toString().contains("success")) {
                    loadingBar?.visibility = View.GONE


                    if(resp?.data != null) {
                        for (i in 0 until resp.data.items?.size!!) {
                            val item = resp.data.items!![i]

                            val TEN_MINUTES = 600000
                            var musicTitle = if (item.youtube_video_title?.length!! >= 20) substringName(item.youtube_video_title!!) + "..." else item.youtube_video_title!!
                            var musicTitleFilter = musicTitle.replace("[\\|\\/\\&quot;]".toRegex(), "")
                            var albumUploadedBy = if (item.youtube_video_upload_by?.length!! >= 15) substringUploadBy(item.youtube_video_upload_by!!) + "..." else item.youtube_video_upload_by!!
                            var musicLocalName = item?.youtube_video_title!!.replace("[\\|\\/\\&quot;]".toRegex(), "")

                            if(item?.youtube_video_id != null && item.youtube_video_length!! < TEN_MINUTES) {
                                val musicBuilder = MusicBuilder()
                                    .setMusicName(musicTitleFilter)
                                    .setMusicUploadedBy(albumUploadedBy)
                                    .setDlMusicName(item?.youtube_video_id)
                                    .setDlMusicNameLocal(musicLocalName)
                                    .setMusicStreamPath(item?.youtube_video_id)
                                    .setMusicLength(item?.youtube_video_length)
                                    .setIsPlaying(false)
                                    .build()
                                if(musicBuilder !in musicList) {
                                    musicList?.add(musicBuilder)
                                }
                            }





                        }

                    }

                    musicAdapter.notifyDataSetChanged()
                }

            }

            override fun onFailure(call: Call<SearchMusicResponse>, t: Throwable) {
                Log.d("Modulehelper", "error ${t.message}")
            }

        })

    }


    // play audio
    @SuppressLint("SetTextI18n")
    fun playAudio(position: Int, music: List<Music>) {
        albumIcon?.visibility = View.GONE
        musicPlayingAnimation?.visibility = View.VISIBLE
        musicPlayingAnimation?.playAnimation()
        musicOverlay?.visibility = View.VISIBLE



        // checking to our localstorage if file is exist!
        if (!MusicDownloader().isFileDownloaded(music[position].dl_music_name_local)) {
            val dlSource = getLocalDownloadPath(music[position].ytVideoId)
            // checking to our server if file is exist!
            checkFileInServer(
                music[position].dl_music_name!!,
                music[position].ytVideoId!!,
                dlSource,
                null,
                false
            )

        } else {
            // playing audio from local file
            Log.d("Musicadapter", "user have file to listen this music")
            try {
                val path = File("/storage/emulated/0/Music/")
                val localPath =
                    File(path, "${music[position].dl_music_name_local}-Waymusics.mp3")
                AudioHelper.playAudio(
                    context,
                    localPath.absolutePath.toString()
                )


            } catch (e: Exception) {
                println(e.toString())
            }
        }

    }

    // pause audio

    fun pauseAudio() {
        if(mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        }
    }
    fun resumeAudio() {
        if(mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        }
    }

    // stop audio

    @SuppressLint("SetTextI18n")
    fun stopAudio() {
        resetCountDown()
        AudioHelper.killMediaPlayer()
    }

    // fun show Dialog
    @SuppressLint("WrongConstant")
    fun showAboutDialog() {
        val msg = "<p>Thanks for choosing Waymusics, This app is in the Beta version, may sometimes not stable on higher devices!.</p><br><p>License: We are using YouTube Data API to get source. and we are not affiliated with any listed source in this app!</p>"
        MaterialAlertDialogBuilder(context!!)
            .setTitle("About Waymusics")
            .setMessage(fromHtml(msg, FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH))
            .setNegativeButton("Dismiss") { dialog, which ->
                dialog.dismiss()

            }
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()

            }
            .show()
    }

    fun openPlaystoreMarket(packname: String) {
        val market = "market://details?id=$packname"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(market)
        activity?.startActivity(intent)
    }









}