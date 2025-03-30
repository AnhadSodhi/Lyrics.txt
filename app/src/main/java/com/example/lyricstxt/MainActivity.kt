package com.example.lyricstxt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
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
            val clientController = ClientController(
                stringResource(R.string.encoded_base_64_id_and_secret),
                stringResource(R.string.refresh_token)
            )

            Scaffold(
                topBar = {
                    TopNav(navController)
                },
                containerColor = Color(0xFFF0F0F0),
            ) { padding ->
                NavHost(
                    navController,
                    "home",
                    modifier = Modifier.padding(padding)
                ) {
                    composable("home") {
                        Home(repo, clientController, navController)
                    }
                    composable("history") {
                        History(repo)
                    }
                }
            }
        }
    }
}
