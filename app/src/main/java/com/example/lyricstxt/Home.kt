package com.example.lyricstxt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lyricstxt.api.ClientController
import com.example.lyricstxt.api.Song
import com.example.lyricstxt.data.HistoryEntry
import com.example.lyricstxt.data.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(historyRepository: HistoryRepository, clientController: ClientController) {
    val auth = stringResource(R.string.encoded_base_64_id_and_secret)
    val refresh = stringResource(R.string.refresh_token)

    var lyrics by remember { mutableStateOf(emptyList<String>()) }
    var times by remember { mutableStateOf(emptyList<Long>()) }
    var currentLineIndex by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        var song: Song
        var progress: Long
        try {
            val (s, p) = clientController.getSongAndProgress()
            song = s
            progress = p
            launch { addSongToDb(song, historyRepository) }
        } catch (e: Exception) {
            song = Song("", "", "")
            progress = 0L
        }

        try {
            val (lineTimes, songLyrics) = clientController.getTimesAndLyrics(song)
            times = lineTimes
            lyrics = songLyrics
        } catch (e: Exception) {
            times = emptyList()
            lyrics = listOf("Error fetching lyrics - check that Spotify is running and the current song is supported.")
        }

        startTime = System.currentTimeMillis() - progress
    }

    LaunchedEffect(startTime, times) {
        val offset = 500
        while (true) {
            val elapsedTime = System.currentTimeMillis() - (startTime ?: 0L)

            val newIndex = times.indexOfLast { it <= elapsedTime + offset }.coerceAtLeast(0)
            if (newIndex != currentLineIndex) {
                currentLineIndex = newIndex
            }

            delay(100) // Update every 100ms
        }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        itemsIndexed(lyrics) { index, line ->
            if (index >= currentLineIndex) {
                val isCurrentLine = index == currentLineIndex
                Text(
                    text = line,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentLine) Color.Black else Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

fun addSongToDb(s: Song, repo: HistoryRepository) {
    val songEntry = HistoryEntry(
        song = s.song,
        artist = s.artist,
        img = s.img
    )
    val recentSong = repo.getMostRecent()
    if (recentSong == null || recentSong != songEntry) {
        repo.insertEntity(songEntry)
    }
}