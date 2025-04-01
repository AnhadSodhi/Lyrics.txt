package com.example.lyricstxt

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.lyricstxt.api.ClientController
import com.example.lyricstxt.api.Song
import com.example.lyricstxt.data.HistoryEntry
import com.example.lyricstxt.data.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(historyRepository: HistoryRepository, clientController: ClientController, navController: NavController) {
    val homeState = remember { HomeState() }

    LaunchedEffect(Unit) {
        val offset = 500
        try {
            val (song, progress) = clientController.getSongAndProgress()
            homeState.setSongAndStartTime(song, System.currentTimeMillis() - (progress + offset))
            launch { addSongToDb(homeState.song, historyRepository) }
        } catch (e: Exception) {
            homeState.setSongAndStartTime(Song("", "", ""))
        }

        try {
            val (lineTimes, songLyrics) = clientController.getTimesAndLyrics(homeState.song)
            homeState.setTimesAndLyrics(lineTimes, songLyrics)
        } catch (_: Exception) { }

        launch { timeUpdater(homeState.startTime, homeState.times, homeState.currentLineIndex) }
        launch { songChangeChecker(clientController, homeState.song, navController) }
    }

    LyricsList(homeState.lyrics, homeState.currentLineIndex.intValue)
}

suspend fun timeUpdater(startTime: Long, times: List<Long>, currentLineIndex: MutableIntState) {
    while (true) {
        val elapsedTime = System.currentTimeMillis() - startTime

        val newIndex = times.indexOfLast { it <= elapsedTime }
        if (newIndex != currentLineIndex.intValue) {
            currentLineIndex.intValue = newIndex
        }

        delay(100) // Update every 100ms
    }
}

suspend fun songChangeChecker(clientController: ClientController, song: Song, navController: NavController) {
    while (true) {
        try {
            val (newSong, _) = clientController.getSongAndProgress()
            if (newSong.song != song.song) {
                navController.navigate("home")
                break
            }
        } catch (_: Exception) { }
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