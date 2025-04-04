package com.example.lyricstxt.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import com.example.lyricstxt.api.Song
import com.example.lyricstxt.api.*
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson

class HomeState(private val auth: String, private val refresh: String) {
    var lyrics by mutableStateOf(listOf("Loading..."))
    var times by mutableStateOf(emptyList<Long>())
    val currentLineIndex = mutableIntStateOf(0)
    var startTime by mutableLongStateOf(0)
    var song by mutableStateOf(Song("", "", ""))

    private val basicClient = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }
    private var authClient: HttpClient? = null

    suspend fun getState(): HomeState {
        if (authClient == null) {
            val token = getAccessToken(basicClient, auth, refresh)
            authClient = basicClient.config {
                defaultRequest {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        return this
    }

    suspend fun getSongAndProgress(): Pair<Song, Long> {
        val authClient = authClient ?: return Pair(Song("", "", ""), 0)
        return getCurrentSong(authClient)
    }

    suspend fun updateSongAndProgress(offset: Int=0) {
        try {
            val (s, p) = getSongAndProgress()
            song = s
            startTime = System.currentTimeMillis() - (p + offset)
        } catch (e: Exception) {
            song = Song("", "", "")
        }
    }

    suspend fun updateTimesAndLyrics() {
        try {
            val (lineTimes, songLyrics) = getLyrics(basicClient, song)
            times = lineTimes
            lyrics = songLyrics
        } catch (_: Exception) { }
    }
}