package com.example.lyricstxt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lyricstxt.api.ClientController
import com.example.lyricstxt.data.HistoryRepository
import com.example.lyricstxt.data.MyDatabase

class MainActivity : ComponentActivity() {

    private val db by lazy {
        MyDatabase.getDatabase(applicationContext)
    }
    private val repo by lazy {
        HistoryRepository(db.historyDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            Scaffold(
                topBar = {
                    TopNav(navController)
                }
            ) { padding ->
                NavHost(
                    navController,
                    "home",
                    modifier = Modifier.padding(padding)
                ) {
                    composable("home") {
                        Home(repo)
                    }
                    composable("history") {
                        History(repo)
                    }
                }
            }
        }
    }
}
