package com.example.lyricstxt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lyricstxt.api.Song

class HomeState {
    var lyrics by mutableStateOf(listOf("Loading..."))
    var times by mutableStateOf(emptyList<Long>())
    val currentLineIndex = mutableIntStateOf(0)
    var startTime by mutableLongStateOf(0)
    var song by mutableStateOf(Song("", "", ""))

    fun setSongAndStartTime(s: Song, start: Long = 0L) {
        song = s
        startTime = start
    }

    fun setTimesAndLyrics(t: List<Long>, l: List<String>) {
        times = t
        lyrics = l
    }
}