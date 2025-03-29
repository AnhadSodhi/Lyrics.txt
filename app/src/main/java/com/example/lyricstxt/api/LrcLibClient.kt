package com.example.lyricstxt.api

import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.parameters
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

suspend fun getLyrics(song: Song) : Pair<MutableList<Long>, MutableList<String>> {
    val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }
    val response = client.get(LRCLIB_ENDPOINT) {
        parameter("track_name", song.song)
        parameter("artist_name", song.artist)
    }
    val jsonString = response.bodyAsText()
    val jsonObject = JsonParser.parseString(jsonString).asJsonObject
    val syncedLyrics = jsonObject.get("syncedLyrics").asString.split("\n")

    val times = mutableListOf<Long>()
    val lyrics = mutableListOf<String>()

    for (line in syncedLyrics) {
        val matchResult = Regex("""\[(\d{2}):(\d{2})\.(\d{2})\](.*)""").find(line)
        if (matchResult != null) {
            val (hours, minutes, seconds, lyric) = matchResult.destructured
            val timeInMillis = (hours.toLong() * 3600 + minutes.toLong() * 60 + seconds.toLong()) * 1000
            times.add(timeInMillis)
            lyrics.add(lyric)
        }
    }
    return Pair(times, lyrics)
}
