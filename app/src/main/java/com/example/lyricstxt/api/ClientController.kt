package com.example.lyricstxt.api

import com.example.lyricstxt.R

class ClientController {
    init {
        val auth = R.string.encoded_base_64_id_and_secret
        val refresh = R.string.refresh_token
//        val token = getAccessToken(auth, refresh)
    }

//    suspend fun getSongAndProgress() : Pair<Song, Int> {
//        return getCurrentSong(token)
//    }
//
//    suspend fun getLyricsAndTimes() : Pair<MutableList<Long>, MutableList<String>> {
//        return getLyrics(song)
//    }
}
