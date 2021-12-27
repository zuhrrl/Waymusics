package com.waymusics.android.component.builder

import com.waymusics.android.component.network.model.Music

data class MusicBuilder(var musicName: String? = null,
                        var dlMusicName: String? = null,
                        var dlMusicNameLocal: String? = null,
                        var ytVideoId: String? = null,
                        var ytVideoUploadBy: String? = null,
                        var ytVideoLength: Int? = null) {

    fun setMusicName(value: String?): MusicBuilder {
        this.musicName = value
        return this
    }

    fun setMusicStreamPath(value: String?): MusicBuilder {
        this.ytVideoId = value
        return this
    }

    fun setDlMusicName(value: String?): MusicBuilder {
        this.dlMusicName = value
        return this
    }

    fun setDlMusicNameLocal(value: String?): MusicBuilder {
        this.dlMusicNameLocal = value
        return this
    }

    fun setMusicUploadedBy(value: String?): MusicBuilder {
        this.ytVideoUploadBy = value
        return this
    }

    fun setMusicLength(value: Int?): MusicBuilder {
        this.ytVideoLength = value
        return this
    }

    fun build(): Music {
        return Music(musicName, ytVideoUploadBy, dlMusicName, dlMusicNameLocal, ytVideoId, ytVideoLength)
    }
}