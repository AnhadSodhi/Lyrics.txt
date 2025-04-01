package com.example.lyricstxt.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.runBlocking


class ClientController(private val auth: String, private val refresh: String) {
    private var token: String
    private val basicClient = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }
    private var authClient: HttpClient

    init {
        runBlocking {
            token = getAccessToken(basicClient, auth, refresh)
            authClient = basicClient.config {
                defaultRequest {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    suspend fun getSongAndProgress() : Pair<Song, Long> {
        return getCurrentSong(authClient)
    }

    suspend fun getTimesAndLyrics(song: Song) : Pair<MutableList<Long>, MutableList<String>> {
        return getLyrics(basicClient, song)
    }
}
