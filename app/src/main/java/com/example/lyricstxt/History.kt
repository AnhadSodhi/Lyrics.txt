package com.example.lyricstxt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.lyricstxt.data.HistoryEntry
import com.example.lyricstxt.data.HistoryRepository

@Composable
fun History(repo: HistoryRepository) {
    val entries = remember { repo.getAll().toMutableStateList() }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            repo.wipe()
            entries.clear()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Clear history")
        }

        if (entries.isEmpty()) {
            Text("No songs played yet!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = entry.img,
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                if (entry.song != null)
                    Text(entry.song, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                else
                    Text("Unknown song", fontWeight = FontWeight.Bold, fontSize = 20.sp)

                if (entry.artist != null)
                    Text(entry.artist)
                else
                    Text("Unknown artist")
            }
        }
    }
}