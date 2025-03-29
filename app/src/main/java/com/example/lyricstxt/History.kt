package com.example.lyricstxt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.lyricstxt.data.HistoryEntry
import com.example.lyricstxt.data.HistoryRepository

@Composable
fun History(repo: HistoryRepository) {
    val entries = remember { repo.getAll().toMutableStateList() }

    Column {
        Button(onClick = {
            repo.wipe()
            entries.clear()
        }) {
            Text("Clear history")
        }

        if (entries.isEmpty()) {
            Text("No songs played yet!")
        }
        else {
            LazyColumn {
                items(entries.size) {
                    EntryCard(entries[it])
                }
            }
        }
    }
}

@Composable
fun EntryCard(entry: HistoryEntry) {
    Card {
        AsyncImage(
            model = entry.img,
            contentDescription = null
        )
        if (entry.song != null)
            Text(entry.song)
        if (entry.artist != null)
            Text(entry.artist)
    }
}