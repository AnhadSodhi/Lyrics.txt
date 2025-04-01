package com.example.lyricstxt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LyricsList(lyrics: List<String>, currentLine: Int) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        itemsIndexed(lyrics) { index, line ->
            if (index >= currentLine) {
                val isCurrentLine = index == currentLine
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