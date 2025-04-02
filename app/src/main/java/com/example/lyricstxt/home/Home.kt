package com.example.lyricstxt.home

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lyricstxt.api.ClientController
import com.example.lyricstxt.api.Song
import com.example.lyricstxt.data.HistoryEntry
import com.example.lyricstxt.data.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(historyRepository: HistoryRepository, clientController: ClientController, navController: NavController) {
    val homeState: HomeState = viewModel()

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

        launch { timeUpdater(homeState) }
        launch { songChangeChecker(clientController, homeState.song, navController) }
    }

    LyricsList(homeState.lyrics, homeState.currentLineIndex)
}

suspend fun timeUpdater(homeState: HomeState) {
    while (true) {
        val elapsedTime = System.currentTimeMillis() - homeState.startTime

        // set currentLineIndex to the last index that is before the timestamp (most recent line)
        val newIndex = homeState.times.indexOfLast { it <= elapsedTime }
        if (newIndex != homeState.currentLineIndex) {
            homeState.currentLineIndex = newIndex
        }

        delay(100) // Update every 100ms
    }
}

suspend fun songChangeChecker(clientController: ClientController, song: Song, navController: NavController) {
    while (true) {
        try {
            val (newSong, _) = clientController.getSongAndProgress()

            // if a new song is detected, reload the page
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

    // if the song is not the same as the most recent one in the db, add it
    if (recentSong == null || recentSong != songEntry) {
        repo.insertEntity(songEntry)
    }
}