package com.example.steamcharts.ui

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.steamcharts.R
import com.example.steamcharts.data.AppContainer
import com.example.steamcharts.ui.screens.HomeScreen
import com.example.steamcharts.ui.screens.SteamPlayerCountScreen
import com.example.steamcharts.ui.viewmodels.SteamPlayerCountViewModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SteamApp(appContainer: AppContainer) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navController = rememberNavController() // Remember the navController

    val steamBlue = colorResource(id = R.color.steam_blue)

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                // Pass the navController to the SteamTopAppBar
                SteamTopAppBar(scrollBehavior = scrollBehavior, navController = navController)
            },
            containerColor = steamBlue
        )
        { innerpadding ->
            Column (
                modifier = Modifier
                    .padding(innerpadding)
                    .background(steamBlue)
                    //.verticalScroll(rememberScrollState())
                    ){
                NavHost(
                    navController = navController,
                    startDestination = "home" // Set the starting destination
                ) {
                    composable("home") {
                        // Pass the navController to the HomeScreen
                        HomeScreen().HomeSetupScreen(navController = navController)
                    }
                    composable("player-count") {
                        SteamPlayerCountScreen()
                    }
                    composable("game-info/{gameId}") { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                        SteamPlayerCountScreen(gameId = gameId)

                    }
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SteamTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val steamDark = colorResource(id = R.color.steam_dark)
    val steamText = colorResource(id = R.color.steam_text)
    val steamLightBlue = colorResource(id = R.color.steam_light_blue)


    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier
            .fillMaxWidth()
    ){
        CenterAlignedTopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                ClickableText(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 20.sp, color = steamText, fontWeight = FontWeight.Bold)) {
                            append("Steam Charts")
                        }
                    },
                    onClick = {
                        navController.navigate("home")
                        navController.addOnDestinationChangedListener { controller, destination, arguments ->
                            Log.d("Navigation", "Navigated to ${destination.route}")
                        }
                    }
                )
            },
            modifier = modifier,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = steamDark,
                titleContentColor = steamText,
                navigationIconContentColor = steamText,
                actionIconContentColor = steamText
            ),

            navigationIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(steamLightBlue)
                ) {
                    DropdownMenuItem(text = { Text("Home", color = steamText)},
                        onClick = { navController.navigate("home")
                        expanded = false
                    })
                    DropdownMenuItem(
                        text = { Text(text = "Player Count", color = steamText)},
                        onClick = { navController.navigate("player-count")
                        expanded = false
                    })
                    // Add more items as needed
                }
            }
        )
    }
}