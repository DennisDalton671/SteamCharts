package com.example.steamcharts.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.steamcharts.R
import com.example.steamcharts.SteamApplication
import com.example.steamcharts.data.Game
import com.example.steamcharts.data.SteamRepositoryApi
import com.example.steamcharts.model.SteamAppData
import com.example.steamcharts.ui.viewmodels.SteamPlayerCountViewModel
import com.example.steamcharts.ui.viewmodels.SteamUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.Locale

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun SteamPlayerCountScreen(
    modifier: Modifier = Modifier,
    gameId: String = "881020"
    //retryAction: () -> Unit,
) {
    // Obtain the SteamApplication instance to access the container
    val appContext = LocalContext.current.applicationContext as SteamApplication
    val apiService = appContext.container.steamRepositoryApi
    val dispatcher: CoroutineDispatcher = Dispatchers.Main

    // Initialize the ViewModelFactory with the required dependencies
    val viewModelFactory = SteamPlayerCountViewModelFactory(apiService, dispatcher, gameId)

    // Use the factory to initialize the ViewModel
    val viewModel: SteamPlayerCountViewModel = viewModel(factory = viewModelFactory)

    // Access the UI state from the ViewModel
    val steamUiState = viewModel.steamUiState
    when (steamUiState) {
        is SteamUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is SteamUiState.Success -> PlayerCountsListScreen(steamUiState, viewModel, gameId, modifier)
        else -> ErrorScreen(
            //retryAction,
            modifier = modifier.fillMaxSize())
    }

}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(dimensionResource(id = R.dimen.spacing_large)*10),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(
    //retryAction: () -> Unit,
    modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_medium)))
    }
}


@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun PlayerCountsListScreen(
    steamUiState: SteamUiState,
    viewModel: SteamPlayerCountViewModel,
    gameId: String,
    modifier: Modifier = Modifier) {

    var searchQuery by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    val searchResults = viewModel.searchGames(searchQuery).observeAsState(initial = listOf())

    val steamLightBlue = colorResource(id = R.color.steam_light_blue)

    //viewModel.viewModelScope.launch(Dispatchers.IO) {
    //    viewModel.setAppDetails(gameId)
   // }


    Column (modifier = modifier
        .padding(
            vertical = dimensionResource(id = R.dimen.spacing_medium),
            horizontal = dimensionResource(id = R.dimen.spacing_medium)
            )
    ){


        Row {
            if (!showSearchBar) {
                Button(onClick = { showSearchBar = !showSearchBar },
                    colors = ButtonDefaults.buttonColors(containerColor = steamLightBlue)) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")

                }
            }
            if (showSearchBar) {
                SearchBar(
                    modifier = modifier,
                    searchQuery = searchQuery,
                    onSearch = { query ->
                        // Handle search query
                        searchQuery = query
                        showSuggestions = query.isNotEmpty()
//                        if (query.isNotEmpty()) {
//                            viewModel.searchGames(query)
//                   }
                        //filteredGames = filterGames(appList.app, query)
                    },
                )
            }
        }



        Box {

            if (showSuggestions && searchQuery.isNotEmpty()) {

                //val searchResults = viewModel.searchGames(searchQuery).observeAsState(initial = listOf()).value

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.spacing_small))
                        .zIndex(2f)
                ) {

                    SuggestionsDropdown(
                        suggestions = searchResults.value,
                        onSuggestionSelect = { selectedGame ->
                            searchQuery = selectedGame.searchName // Assuming your Game object has a 'name' property
                            viewModel.viewModelScope.launch(Dispatchers.IO) {
                                viewModel.setAppDetails(selectedGame.gameId)
                            }
                            showSuggestions = false
                            // Here, you might navigate to a detail screen for the selected game
                            // Or perform other actions based on the game selection
                        }
                    )

                }

//                {
//                    GameSuggestions(searchResults) { clickedGame ->
//                        // Handle the click on the game suggestion
//                        searchQuery = ""
//                        viewModel.viewModelScope.launch {
//                            viewModel.setPlayerCountForGame(clickedGame)
//                        }
//                        // You might want to dismiss the suggestions or perform a search here
//                        showSuggestions = false
//                        showSearchBar = false
//
//                    }
//                }

            } else if (steamUiState is SteamUiState.Success) {
                GameInfoDisplay(
                    playerCounts = steamUiState.playerCounts,
                    updatedTitle = steamUiState.titleUpdated,
                    headerImage = steamUiState.headerImage,
                    priceUS = steamUiState.priceUS,
                    discount = steamUiState.discount,
                    gameReview = steamUiState.gameReview,
                    shortDescription = steamUiState.shortDescription
                )
            }


        }
    }

}

@Composable
fun SuggestionsDropdown(suggestions: List<Game>, onSuggestionSelect: (Game) -> Unit) {
    val steamText = colorResource(id = R.color.steam_text)
    val steamLightBlue = colorResource(id = R.color.steam_light_blue)
    val steamDark = colorResource(id = R.color.steam_dark)
    val steamDarkBlue = colorResource(id = R.color.steam_blue)
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .background(steamDarkBlue)
    ) {
        items(suggestions) { game ->
            Text(
                text = game.gameName,
                color = steamText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionSelect(game) }
                    .padding(dimensionResource(R.dimen.spacing_small)/4)
                    .background(steamDark)
                    .padding(dimensionResource(R.dimen.spacing_medium))
            )
        }
    }
}

//    fun filterGames(gameList: List<SteamAppData>, query: String): List<String> {
//        val sanitizedQuery = query.lowercase(Locale.ROOT)
//
//        // Create an index for quick access to the original names
//        val indexedNames = gameList.map { it.name.lowercase(Locale.ROOT) to it.name }.toMap()
//
//        // Filter the lowercase names directly using the index
//        return indexedNames.keys
//            .filter { it.contains(sanitizedQuery) }
//            .map { indexedNames[it] ?: "" }
//    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBar(
        modifier: Modifier = Modifier,
        searchQuery: String,
        onSearch: (String) -> Unit
    ) {

        val steamLightBlue = colorResource(id = R.color.steam_light_blue)
        val steamDark = colorResource(id = R.color.steam_dark)
        val steamText = colorResource(id = R.color.steam_text)

        var currentSearchQuery by remember { mutableStateOf(searchQuery) }
        val debounceDelay = (R.integer.debounce_delay).toLong()

        LaunchedEffect(currentSearchQuery) {
            delay(debounceDelay)  // Adjust the debounce delay as needed
            onSearch(currentSearchQuery)
        }

        TextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = steamText,
                unfocusedTextColor = steamText,
                focusedContainerColor = steamLightBlue,
                unfocusedContainerColor = steamLightBlue,
                disabledContainerColor = steamDark,
            ),
            value = currentSearchQuery,
            onValueChange = { query ->
                currentSearchQuery = query
                onSearch(query)
            },
            singleLine = true,
            label = { Text("Search for a game" , color = steamText) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done, // Set the keyboard action to Done
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.spacing_medium))


        )
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun GameInfoDisplay(
        playerCounts: Int,
        updatedTitle: String,
        headerImage: String,
        priceUS: String,
        discount: Int,
        shortDescription: String,
        gameReview: Int,
        modifier: Modifier = Modifier,
    ) {

        val steamText = colorResource(id = R.color.steam_text)
        val steamGray = colorResource(id = R.color.steam_gray)
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.spacing_medium))
        ) {
            // Check if playerCounts is not null

            Text(
                //text = "${playerCounts.player_count ?: "N/A"}",
                text = updatedTitle, // Replace with actual player count
                fontSize = 20.sp,
                color = steamText,
                modifier = modifier.padding(
                    start = dimensionResource(id = R.dimen.spacing_medium),
                    top = dimensionResource(id = R.dimen.spacing_small)
                )
            )
            // Display game image (replace "R.drawable.game_image" with your image resource)
            //GameLogoScreen(steamUiState = steamUiState, viewModel = viewModel)

            Image(
                painter = rememberAsyncImagePainter(model = headerImage),
                contentDescription = "Game Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.spacing_medium))
                    .height(dimensionResource(id = R.dimen.spacing_medium) * 10)
                    .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.spacing_small)))
            )
//            Image(
//                painter = painterResource(id = R.drawable.steam_logo),
//                contentDescription = "Game Image",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = dimensionResource(id = R.dimen.spacing_medium))
//                    .height(dimensionResource(id = R.dimen.spacing_large) * 10)
//                    .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.spacing_small)))
//            )

            // Overlay the image with a Box for the text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.spacing_small)))
                    .background(color = steamGray) // Make the background transparent
                    .padding(dimensionResource(id = R.dimen.spacing_small)) // Adjust padding as needed
            ) {
                // Display player count text
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.spacing_medium),
                            vertical = dimensionResource(id = R.dimen.spacing_small)
                        )
                        .align(Alignment.BottomStart),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Player Count: $playerCounts",
                        fontSize = 25.sp,
                        style = MaterialTheme.typography.bodyMedium, // or use your own typography styles
                        color = steamText
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                    Text(
                        text = "Review Score: $gameReview",
                        fontSize = 25.sp,
                        style = MaterialTheme.typography.bodyMedium, // or use your own typography styles
                        color = steamText
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                    Text(
                        text = "Price: $priceUS",
                        fontSize = 25.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = steamText
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                    Text(
                        text = "Discount: $discount%",
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = steamText
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                    Text(
                        text = "Short Description:",
                        fontSize = 25.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = steamText
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                    Text(
                        text = shortDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = steamText
                    )
                }
            }
        }


    }

    class SteamPlayerCountViewModelFactory(
        private val apiService: SteamRepositoryApi,
        private val dispatcher: CoroutineDispatcher,
        private val gameId: String
    ) : ViewModelProvider.Factory {
        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SteamPlayerCountViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SteamPlayerCountViewModel(apiService, dispatcher, gameId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

