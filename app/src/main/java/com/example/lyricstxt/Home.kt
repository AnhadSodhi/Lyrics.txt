package com.example.lyricstxt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
fun Home(historyRepository: HistoryRepository) {
    val auth = stringResource(R.string.encoded_base_64_id_and_secret)
    val refresh = stringResource(R.string.refresh_token)
    val clientController = ClientController(auth, refresh)

    var lyrics by remember { mutableStateOf(emptyList<String>()) }
    var times by remember { mutableStateOf(emptyList<Long>()) }
    var currentLineIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val (song, progress) = clientController.getSongAndProgress()
        val (lineTimes, songLyrics) = clientController.getTimesAndLyrics(song)

        times = lineTimes
        lyrics = songLyrics

        launch { addSongToDb(song, historyRepository) }

        // Start updating the current line index based on the progress
        var currentTime = progress
        while (true) {
            val newIndex = times.indexOfLast { it <= currentTime }
            if (newIndex != currentLineIndex) {
                currentLineIndex = newIndex
            }
            delay(100) // Update every 100ms
            currentTime += 100 // Simulate progress increment in milliseconds
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        itemsIndexed(lyrics) { index, line ->
            val isCurrentLine = index == currentLineIndex
            Text(
                text = line,
                fontSize = 20.sp,
                fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrentLine) Color.Black else Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )
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