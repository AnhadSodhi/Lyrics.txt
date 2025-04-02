package com.example.lyricstxt.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson


class ClientController private constructor (
    private val basicClient: HttpClient,
    private val authClient: HttpClient
) {
    companion object {
        suspend fun build(auth: String, refresh: String): ClientController {
            val basicClient = HttpClient {
                install(ContentNegotiation) {
                    gson()
                }
            }

            val token = getAccessToken(basicClient, auth, refresh)

            val authClient = basicClient.config {
                defaultRequest {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            return ClientController(basicClient, authClient)
        }
    }

    suspend fun getSongAndProgress() : Pair<Song, Long> {
        return getCurrentSong(authClient)
    }

    suspend fun getTimesAndLyrics(song: Song) : Pair<MutableList<Long>, MutableList<String>> {
        return getLyrics(basicClient, song)
    }
}
