package com.example.lyricstxt

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNav(navController: NavController) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(30.dp))
            }
        },

        title = {
            Text("Lyrics.txt")
        },

        actions = {
            IconButton(onClick = {
                navController.navigate("home")
            }) {
                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(30.dp))
            }
            IconButton(onClick = {
                navController.navigate("history")
            }) {
                Icon(painter = painterResource(id = R.drawable.history_icon), contentDescription = null, modifier = Modifier.size(30.dp))
            }
        }
    )
}