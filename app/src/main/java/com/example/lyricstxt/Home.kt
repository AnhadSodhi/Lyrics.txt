package com.example.lyricstxt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lyricstxt.api.ClientController
import com.example.lyricstxt.api.Song
import com.example.lyricstxt.data.HistoryEntry
import com.example.lyricstxt.data.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(historyRepository: HistoryRepository, clientController: ClientController, navController: NavController) {
    var lyrics by remember { mutableStateOf(emptyList<String>()) }
    var times by remember { mutableStateOf(emptyList<Long>()) }
    var currentLineIndex by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableStateOf<Long?>(null) }
    var song by remember { mutableStateOf(Song("", "", "")) }

    LaunchedEffect(song) {
        lyrics = listOf("Loading...")
        var progress: Long
        val offset = 500
        try {
            val (s, p) = clientController.getSongAndProgress()
            song = s
            progress = p+offset
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
            lyrics = listOf("Error fetching lyrics - check that a song is playing and it is supported.")
        }

        startTime = System.currentTimeMillis() - progress

        launch { checkSongChanged(clientController, song, navController) }
    }

    LaunchedEffect(startTime, times) {
        while (true) {
            val elapsedTime = System.currentTimeMillis() - (startTime ?: 0L)

            val newIndex = times.indexOfLast { it <= elapsedTime }.coerceAtLeast(0)
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

suspend fun checkSongChanged(clientController: ClientController, song: Song, navController: NavController) {
    while (true) {
        try {
            val (newSong, _) = clientController.getSongAndProgress()
            if (newSong.song != song.song) {
                navController.navigate("home")
                break
            }
        } catch (_: Exception) {

        }

        delay(500) // Check twice every second
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