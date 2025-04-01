package com.example.lyricstxt.api

import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson

suspend fun getAccessToken(auth: String, refreshToken: String) : String {
    val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }
    val response = client.post(SPOTIFY_TOKEN_ENDPOINT) {
        headers {
            append(HttpHeaders.Authorization, auth)
            append(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
        }
        setBody(
            "grant_type=refresh_token&refresh_token=$refreshToken"
        )
    }
    val jsonString = response.bodyAsText()
    val jsonObject = JsonParser.parseString(jsonString).asJsonObject
    return jsonObject.get("access_token").asString
}

suspend fun getCurrentSong(accessToken: String) : Pair<Song, Long> {
    val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
    }
    val response = client.get(SPOTIFY_GET_SONG_ENDPOINT)
    val jsonString = response.bodyAsText()
    val jsonObject = JsonParser.parseString(jsonString).asJsonObject

    val item = jsonObject.getAsJsonObject("item")
    var song = item.get("name").asString
    var artist = ""
    var img = ""
    val progress = jsonObject.get("progress_ms").asLong

    try {
        val album = item.getAsJsonObject("album")
        val images = album.getAsJsonArray("images")
        img = images[0].asJsonObject.get("url").asString

        val artists = item.getAsJsonArray("artists")
        artist = artists[0].asJsonObject.get("name").asString

    } catch (_: Exception) {
    } finally {
        val (s, a) = sanitizeInfo(song, artist) ?: Pair("Unknown", "Unknown")
        song = s
        artist = a
    }
    return Pair(Song(song, artist, img), progress)
}

fun sanitizeInfo(song: String?, artist: String?) : Pair<String, String>? {
    if (song.isNullOrEmpty()) {
        return null
    }
    else if (artist.isNullOrEmpty()) {
        val (s, a) = song.split(" by ")
        return Pair(s.trim(), a.trim())
    }
    else {
        return Pair(song.trim(),artist.trim())
    }
}