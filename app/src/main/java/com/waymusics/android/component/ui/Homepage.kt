package com.waymusics.android.component.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.materialdrawer.iconics.iconicsIcon
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.util.addStickyFooterItem
import com.waymusics.android.Constant
import com.waymusics.android.R
import com.waymusics.android.component.adapter.MusicAdapter
import com.waymusics.android.component.audio.AudioHelper
import com.waymusics.android.component.module.ModuleHelper
import com.waymusics.android.component.module.ModuleHelper.music
import com.waymusics.android.component.network.model.Music
import kotlinx.android.synthetic.main.activity_main.*


class Homepage : AppCompatActivity() {
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
        initPlayingOverlay()
        initMusicAdapter(Constant.keywordHome)
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
                ModuleHelper.playAudio(ModuleHelper.musicPosition!!, music!!)

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
                ModuleHelper.isNewMusicSearch = true
                initMusicAdapter(search_music.text?.trim().toString())
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

    // fun init playing overlay
    private fun initPlayingOverlay() {
        ModuleHelper.musicTitlePlay = music_title_play
        ModuleHelper.musicAlbumPlay = music_album_name_play
        ModuleHelper.musicDurationPlay = music_duration_play
        ModuleHelper.musicPauseBtn = btn_pause_music
        ModuleHelper.musicOverlay = playing_music_overlay
        btn_pause_music.setOnClickListener {
            Log.d("Main", "Is Streaming play: ${ModuleHelper.isStreamPlaying}")
            if(ModuleHelper.isStreamPlaying) {
                ModuleHelper.isStreamPlaying = false
                ModuleHelper.pauseAudio()
                btn_pause_music.setImageResource(R.drawable.baseline_play_circle_filled_white_36)
            }
            else {
                ModuleHelper.isStreamPlaying = true
                btn_pause_music.setImageResource(R.drawable.baseline_pause_circle_white_36)
                ModuleHelper.resumeAudio()
            }
        }
    }

    private fun initDrawer() {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        val item1 = PrimaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_home; nameRes =
            R.string.drawer_item_home; identifier = 1 }
        val item2 = SecondaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_fire_alt; nameRes =
            R.string.drawer_item_top_rated; identifier = 2 }
        val item3 = SecondaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_grin_stars; nameRes =
            R.string.drawer_item_feedback; identifier = 3 }
        val item4 = SecondaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_question; nameRes =
            R.string.drawer_item_about; identifier = 4 }
        val item5 = SecondaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_window_close; nameRes =
            R.string.drawer_item_exit; identifier = 5 }


        // get the reference to the slider and add the items
        slider.itemAdapter.add(
            item1,
            item2,
            item3,
            item4,
            item5
        )
        slider.addStickyFooterItem(PrimaryDrawerItem().apply { iconicsIcon = FontAwesome.Icon.faw_info; nameRes =
            R.string.drawer_item_version
        })

        // specify a click listener
        slider.onDrawerItemClickListener = { v, drawerItem, position ->
            // do something with the clicked item :D
            when(position) {
                0-> initMusicAdapter("venom eminem")
                1-> initMusicAdapter("popular music 2022")
                2-> {ModuleHelper.openPlaystoreMarket(packageName)}
                3-> {
                    ModuleHelper.showAboutDialog()
                }
                4-> {finishAndRemoveTask()}
            }
            false
        }
        btn_open_close_drawer.setOnClickListener {
            if(!slider?.drawerLayout!!.isOpen) {
                slider?.drawerLayout!!.openDrawer(slider)
            }

        }
        slider.setSelection(item1.identifier, true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initMusicAdapter(keyword: String) {
        ModuleHelper.loadingBar?.visibility = View.VISIBLE

        if(musicList.isNotEmpty()) {
            musicList.clear()

        }


        // adapter
        val musicAdapter = MusicAdapter(musicList)
        ModuleHelper.musicAdapter = musicAdapter
        ModuleHelper.initHomepageMusicList(keyword, musicList, musicAdapter)

        main_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@Homepage)
            adapter = musicAdapter
        }
    }

    private fun hideKeyboard(view: View) {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }



}