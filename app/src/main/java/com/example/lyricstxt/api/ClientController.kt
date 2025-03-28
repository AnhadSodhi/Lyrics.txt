package com.example.lyricstxt.api

import kotlinx.coroutines.runBlocking


class ClientController(private val auth: String, private val refresh: String) {
    private var token: String

    init {
        runBlocking {
            token = getAccessToken(auth, refresh)
        }
    }

    suspend fun getSongAndProgress() : Pair<Song, Long> {
        return getCurrentSong(token)
    }

    suspend fun getTimesAndLyrics(song: Song) : Pair<MutableList<Long>, MutableList<String>> {
        return getLyrics(song)
    }
}
