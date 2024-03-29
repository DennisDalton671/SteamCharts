package com.example.steamcharts.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.viewpager.widget.PagerAdapter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.load
import com.example.steamcharts.R
import com.example.steamcharts.SteamApplication
import com.example.steamcharts.data.Game
import com.example.steamcharts.data.SteamRepositoryApi
import com.example.steamcharts.ui.viewmodels.HomeScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeScreen(
    modifier: Modifier = Modifier
) {



    @Composable
    fun HomeSetupScreen(navController: NavController) {

        val steamLightBlue = colorResource(id = R.color.steam_light_blue)
        val textColor = colorResource(id = R.color.steam_text)

        val appContext = LocalContext.current.applicationContext as SteamApplication
        val apiService = appContext.container.steamRepositoryApi

        val homeScreenViewModel: HomeScreenViewModel = viewModel(
            factory = HomeScreenViewModelFactory(repository = apiService)
        )

        val topGames by homeScreenViewModel.topGames.observeAsState(initial = emptyList())

        val isLoading by homeScreenViewModel.isLoading.observeAsState(true)

        if (isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.spacing_medium),
                        vertical = dimensionResource(id = R.dimen.spacing_medium)
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {

                Text(
                    text = "Welcome To Steam Charts",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.spacing_medium)),
                    color = textColor
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
                Text(
                    text = "Top 5 Played Games",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
                if (topGames.isNotEmpty()) {
                    GameCarousel(games = topGames, navController)
                    Text(
                        text = "Results are based on your previous search results",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = textColor
                    )
                } else {
                    // Show loading or empty state here
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                        color = textColor
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
                Button(
                    onClick = { navController.navigate("player-count") },
                    colors = ButtonDefaults.buttonColors(containerColor = steamLightBlue)) {
                    Text(text = "View Player Counts", color = textColor)
                }
                // Add more content or features as needed
            }
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun GameCarousel(games: List<Game>, navController: NavController) {

        val textColor = colorResource(id = R.color.steam_text)
        val backgroundColor = colorResource(id = R.color.steam_dark)
        val pagerState = rememberPagerState(pageCount = { games.size })




        // Coroutine scope for side effects
        val coroutineScope = rememberCoroutineScope()

        // LaunchedEffect to auto-scroll pages every 3 seconds
        LaunchedEffect(Unit) {
            while (true) {
                delay(5000) // 3 seconds delay
                with(pagerState) {
                    val nextPage = if (currentPage < pageCount - 1) currentPage + 1 else 0
                    coroutineScope.launch {
                        animateScrollToPage(nextPage)
                    }
                }
            }
        }

        HorizontalPager(state = pagerState) { page ->

            val date = Date(games[page].lastUpdated)
            val format = SimpleDateFormat("MMMM dd, yyyy HH:mm a", Locale.getDefault())
            val dateString = format.format(date)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Image loading logic using Coil
                Image(
                    painter = rememberAsyncImagePainter(model = games[page].headerImage),
                    contentDescription = "Game Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable {navController.navigate("game-info/${games[page].gameId}")},
                    contentScale = ContentScale.Crop // Adjust the scaling as needed

                )

                // Display the player count
                Box (
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                        )
                        .padding(dimensionResource(id = R.dimen.spacing_small))
                ){
                    Text(
                        text = "Title: ${games[page].gameName}\n${games[page].playerCount} players\nLast Updated: $dateString",
                        //style = MaterialTheme.typography.subtitle1
                        color = textColor,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    @Composable
    fun LoadingScreen() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Default Material Design loading spinner
        }
    }


    // Example ViewModel Factory
    class HomeScreenViewModelFactory(
        private val repository: SteamRepositoryApi
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeScreenViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    
}