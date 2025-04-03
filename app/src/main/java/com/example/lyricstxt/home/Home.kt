package com.example.lyricstxt.home

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.lyricstxt.api.Song
import com.example.lyricstxt.data.HistoryEntry
import com.example.lyricstxt.data.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(historyRepository: HistoryRepository, homeState: HomeState, navController: NavController) {
    LaunchedEffect(Unit) {
        val offset = 1000
        homeState.updateSongAndProgress(offset)
        launch { addSongToDb(homeState.song, historyRepository) }
        homeState.updateTimesAndLyrics()

        launch { timeUpdater(homeState.startTime, homeState.times, homeState.currentLineIndex) }
        launch { songChangeChecker(homeState, navController) }
    }

    LyricsList(homeState.lyrics, homeState.currentLineIndex.intValue)
}

suspend fun timeUpdater(startTime: Long, times: List<Long>, currentLineIndex: MutableIntState) {
    while (true) {
        val elapsedTime = System.currentTimeMillis() - startTime
        // set currentLineIndex to the last index that is before the timestamp (most recent line)
        val newIndex = times.indexOfLast { it <= elapsedTime }
        if (newIndex != currentLineIndex.intValue) {
            currentLineIndex.intValue = newIndex
        }
        delay(100) // Update every 100ms
    }
}

suspend fun songChangeChecker(homeState: HomeState, navController: NavController) {
    while (true) {
        try {
            val (newSong, _) = homeState.getSongAndProgress()
            // if a new song is detected, reload the page
            if (newSong.song != homeState.song.song) {
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