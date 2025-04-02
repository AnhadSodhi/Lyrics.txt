package com.example.lyricstxt.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyricstxt.api.Song
import com.example.lyricstxt.api.getAccessToken
import com.example.lyricstxt.api.getCurrentSong
import com.example.lyricstxt.api.getLyrics
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.launch

class HomeState (
    private val auth: String,
    private val refresh: String
) : ViewModel() {
    private lateinit var basicClient: HttpClient
    private lateinit var authClient: HttpClient

    var lyrics by mutableStateOf(listOf("Loading..."))
    var times by mutableStateOf(emptyList<Long>())
    var currentLineIndex by mutableIntStateOf(0)
    var startTime by mutableLongStateOf(0)
    var song by mutableStateOf(Song("", "", ""))

    init {
        viewModelScope.launch {
            basicClient = HttpClient {
                install(ContentNegotiation) {
                    gson()
                }
            }

            val token = getAccessToken(basicClient, auth, refresh)

            authClient = basicClient.config {
                defaultRequest {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    suspend fun getSongAndProgress(): Pair<Song, Long> {
        return getCurrentSong(authClient)
    }

    suspend fun updateSongAndProgress(offset: Int) {
        try {
            val (s, progress) = getCurrentSong(authClient)
            song = s
            startTime = System.currentTimeMillis() - (progress + offset)
        }
        catch (e: Exception) {
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