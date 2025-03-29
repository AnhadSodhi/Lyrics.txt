package com.example.lyricstxt

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.lyricstxt.api.ClientController
import com.example.lyricstxt.data.HistoryEntry
import com.example.lyricstxt.data.HistoryRepository

@Composable
fun Home(historyRepository: HistoryRepository) {
    val auth = stringResource(R.string.encoded_base_64_id_and_secret)
    val refresh = stringResource(R.string.refresh_token)
    val clientController = ClientController(auth, refresh)

    var displayText by remember { mutableStateOf("Loading...") }
    Text(displayText, fontSize = 30.sp)

    LaunchedEffect(Unit) {
        val (song, progress) = clientController.getSongAndProgress()
        //put progress into some kind of auto-updating timer
        val (times, lyrics) = clientController.getTimesAndLyrics(song)
        println(times)
        displayText = lyrics.joinToString("\n")

        val songEntry = HistoryEntry(
            song = song.song,
            artist = song.artist,
            img = song.img
        )
        val recentSong = historyRepository.getMostRecent()
        if (recentSong == null || recentSong != songEntry) {
            historyRepository.insertEntity(songEntry)
        }
    }
}
