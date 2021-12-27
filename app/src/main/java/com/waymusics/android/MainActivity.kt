package com.waymusics.android

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.materialdrawer.iconics.iconicsIcon
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.waymusics.android.component.adapter.MusicAdapter
import com.waymusics.android.component.builder.MusicBuilder
import com.waymusics.android.component.network.model.Music
import com.waymusics.android.component.module.ModuleHelper
import com.waymusics.android.component.network.retro.ClientRequest
import com.waymusics.android.component.network.retro.Retro
import com.waymusics.android.component.network.retro.RetroInterface
import com.waymusics.android.component.ui.FintechReview
import com.waymusics.android.component.network.retro.SearchMusicResponse
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.media.MediaPlayer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import com.waymusics.android.component.audio.AudioHelper


class MainActivity : AppCompatActivity() {
    var tag: String? = null
    val musicList = mutableListOf<Music>()
    private val mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMainActivity()
        initSearchView()
        initModule()
        initDrawer()
        initFintechAdapter(Constant.keywordHome)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                Log.d("Permission result", "i have access")
                ModuleHelper.saveToLocal(ModuleHelper.musicAdapterPosition!!)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioHelper.killMediaPlayer()
    }


    private fun initMainActivity() {
        tag = this.localClassName
        ModuleHelper.loadingBar = loading_bar
    }

    private fun initSearchView() {
        search_music.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }


            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })
        search_music.setOnClickListener {
            search_music.text?.clear()
        }
        search_music.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                initFintechAdapter(search_music.text?.trim().toString())
                hideKeyboard(v.rootView)
                return@OnKeyListener true
            }
            false
        })
    }

    private fun initModule() {
        ModuleHelper.activity = this
        ModuleHelper.context = this
        ModuleHelper.recyclerView = main_recycler_view
        ModuleHelper.btnOpenCloseDrawer = btn_open_close_drawer
    }

    private fun initDrawer() {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        val item1 = PrimaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_home; nameRes = R.string.drawer_item_home; identifier = 1 }
        val item2 = SecondaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_fire_alt; nameRes = R.string.drawer_item_top_rated; identifier = 2 }
        val item3 = SecondaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_wrench; nameRes = R.string.drawer_item_settings }
        // get the reference to the slider and add the items
        slider.itemAdapter.add(
            item1,
            item2,
            item3
        )

        // specify a click listener
        slider.onDrawerItemClickListener = { v, drawerItem, position ->
            // do something with the clicked item :D
            false
        }
        btn_open_close_drawer.setOnClickListener {
            if(ModuleHelper.isReviewFintech) {
                ModuleHelper.removeFragment(FintechReview())
                ModuleHelper.recyclerView?.visibility = View.VISIBLE
                ModuleHelper.btnOpenCloseDrawer?.setImageResource(R.drawable.baseline_menu_white_24)
                ModuleHelper.isReviewFintech = false
            }
            else {
                if(!slider?.drawerLayout!!.isOpen) {
                    slider?.drawerLayout!!.openDrawer(slider)
                }
            }

        }
        slider.setSelection(item1.identifier, true)
    }

    private fun initFintechAdapter(keyword: String) {
        ModuleHelper.loadingBar?.visibility = View.VISIBLE
        if(musicList.isNotEmpty()) {
            musicList.clear()
        }
        // adapter
        val fintechAdapter = MusicAdapter(musicList)

        val request = ClientRequest()
        request.keyword = keyword

        Log.d(tag, "Keyword: "+request.keyword)

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
                    ModuleHelper.loadingBar?.visibility = View.GONE

                    if(resp?.data != null) {
                        for (i in 0 until resp.data.items?.size!!) {
                            val item = resp.data.items!![i]

                            val TEN_MINUTES = 600000
                            var musicTitle = if (item.youtube_video_title?.length!! >= 20) ModuleHelper.substringName(item.youtube_video_title!!) + "..." else item.youtube_video_title!!
                            var albumUploadedBy = if (item.youtube_video_upload_by?.length!! >= 15) ModuleHelper.substringUploadBy(item.youtube_video_upload_by!!) + "..." else item.youtube_video_upload_by!!

                            var musicLocalName = item?.youtube_video_title!!.replace("[\\|\\/\\&quot;]".toRegex(), "")

                            if(item?.youtube_video_id != null && item.youtube_video_length!! < TEN_MINUTES) {
                                val musicBuilder = MusicBuilder()
                                    .setMusicName(musicTitle)
                                    .setMusicUploadedBy(albumUploadedBy)
                                    .setDlMusicName(item?.youtube_video_id)
                                    .setDlMusicNameLocal(musicLocalName)
                                    .setMusicStreamPath(item?.youtube_video_id)
                                    .setMusicLength(item?.youtube_video_length)
                                    .build()
                                if(musicBuilder !in musicList) {
                                    musicList?.add(musicBuilder)
                                }
                            }





                        }

                    }

                    // notify to update fintech items
                    fintechAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<SearchMusicResponse>, t: Throwable) {
                Log.d(tag, "error ${t.message}")
            }

        })



        main_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fintechAdapter
        }
    }

    private fun hideKeyboard(view: View) {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }



}