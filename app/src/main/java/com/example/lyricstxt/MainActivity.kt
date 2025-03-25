package com.example.lyricstxt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            Scaffold(
                topBar = {
                    BottomNav(navController)
                }
            ) { padding ->
                NavHost(
                    navController,
                    "home",
                    modifier = Modifier.padding(padding)
                ) {
                    composable("home") {

                    }
                    composable("history") {

                    }
                }
            }
        }
    }
}
