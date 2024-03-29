package com.example.steamcharts

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import com.example.steamcharts.data.AppContainer
import com.example.steamcharts.data.DefaultAppContainer
import com.example.steamcharts.ui.SteamApp
import com.example.steamcharts.ui.theme.SteamChartsTheme

/**
 * The main entry point of the application. Launches the SteamApp with the provided AppContainer.
 */

class MainActivity : ComponentActivity() {
    private val appContainer: AppContainer by lazy {
        DefaultAppContainer()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SteamChartsTheme {
                SteamApp(appContainer)
            }
        }
    }
}
