package com.waymusics.android.component.network.download

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.waymusics.android.R
import com.waymusics.android.component.module.ModuleHelper
import com.waymusics.android.component.network.retro.Retro
import com.waymusics.android.component.network.retro.RetroInterface
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.media.MediaPlayer
import java.io.*


class MusicDownloader {

    fun downloadFile(context: Context, dlpath: String?, filename: String?) {

        val retro = Retro().getRetrofitInstance().create(RetroInterface::class.java)
        Log.d("Musicdownloader", dlpath!!)

        retro.downloadMusic(dlpath).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val path = File("/storage/emulated/0/Music/")
                val file = File(path, "${filename}-Waymusics.mp3")
                val thread = Thread {
                    try {
                        Log.d("Musicdownloader", dlpath!!)
                        val input = response.body()!!.byteStream()
                        //val path = context.getExternalFilesDir(null)?.absoluteFile
                        //val path = context.getExternalFilesDir(DIRECTORY_MUSIC)

                        val output = BufferedOutputStream(
                            FileOutputStream(file)
                        )
                        val data = ByteArray(1024)

                        var total: Long = 0
                        var count: Int
                        while (input.read(data).also { count = it } != -1) {
                            total += count.toLong()
                            output.write(data, 0, count)

                        }
                        output.flush()
                        output.close()

                        Log.d("Musicdownloader", "downloaded file")
                        MediaScannerConnection(context, object : MediaScannerConnectionClient {
                            override fun onMediaScannerConnected() {}
                            override fun onScanCompleted(path: String?, uri: Uri?) {}
                        })
                        MediaScannerConnection.scanFile(
                            context, arrayOf(
                                file.toString()
                            ), null
                        ) { path: String?, uri: Uri? -> }
                        ModuleHelper.activity?.runOnUiThread {
                            ModuleHelper.loadingBar?.visibility = View.GONE
                            Toast.makeText(ModuleHelper.context, filename+" is ${ModuleHelper.context!!.getString(
                                R.string.is_music_save)}", Toast.LENGTH_SHORT).show()

                            // hide loading
                            ModuleHelper.albumIcon?.visibility = View.VISIBLE
                            ModuleHelper.musicPlayingAnimation?.setAnimation(R.raw.music_playing)
                            ModuleHelper.musicPlayingAnimation?.pauseAnimation()
                            ModuleHelper.musicPlayingAnimation?.visibility = View.GONE
                        }





                    } catch (e: IOException) {
                        Log.e("ConfigDownloader", "Error while writing file!")
                        Log.e("ConfigDownloader", e.toString())
                    }
                }

                thread.start()


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Retrofit", t.message.toString())
                //ModelsHelper.getProgressBar()?.visibility = View.GONE
                Toast.makeText(context, "Failed to save this music", Toast.LENGTH_SHORT).show()

            }

        })
    }

    fun isFileDownloaded(filename: String?): Boolean {
        val path = File("/storage/emulated/0/Music/")
        val file = File(path, "${filename}-Waymusics.mp3")
        if(file!!.exists()) {
            return true
        }
        return false
    }
}