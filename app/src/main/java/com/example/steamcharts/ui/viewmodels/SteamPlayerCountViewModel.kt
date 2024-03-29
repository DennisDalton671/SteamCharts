package com.example.steamcharts.ui.viewmodels

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.steamcharts.BuildConfig
import com.example.steamcharts.SteamApplication
import com.example.steamcharts.data.Game
import com.example.steamcharts.data.SteamRepositoryApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalTime

sealed interface SteamUiState {
    data class Success(val playerCounts: Int = 0,
                       var titleUpdated: String = "",
                       var appId : Int? = 0,
                       var headerImage: String,
                       var priceUS: String,
                       var discount: Int,
                       var shortDescription: String,
                       var gameReview: Int
    ) : SteamUiState {
    }
    object Error : SteamUiState
    object Loading : SteamUiState
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
class SteamPlayerCountViewModel(
    private val apiService: SteamRepositoryApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    gameId: String = "881020"
) : ViewModel() {

    /** The mutable State that stores the status of the most recent request */
    var steamUiState: SteamUiState by mutableStateOf(SteamUiState.Loading)
        private set

    init {
        // Initialize the ViewModel by launching the coroutine to get Steam player counts
        viewModelScope.launch(dispatcher) {
            // Use the IO dispatcher for network operations
           // withContext(Dispatchers.IO) {
            getSteamPlayerCounts(gameId = gameId)
            //}
        }
    }

    /**
     * Gets Steam player counts from the Steam API and updates the playerCounts [Map].
     */
    suspend fun getSteamPlayerCounts(gameId: String = "881020") {
        // Set the UI state to Loading before making the API call
        steamUiState = SteamUiState.Loading
        steamUiState = try {
            val game = SteamApplication.database?.gameDao()?.getGameById(gameId)
            // Attempt to fetch data from the API
            val appIds = game?.gameId?.toIntOrNull() ?: 0
            val title = game?.gameName ?: ""
            val playerCount = game?.playerCount ?: 0
            val headerImage = game?.headerImage ?: "No URL"
            val priceUS = game?.priceUS ?: "$0.0"
            val discount = game?.discount ?: 0
            val shortDescription = game?.shortDescription ?: "None"
            val gameReview = game?.reviewScore ?: 0


            // Update the UI state to Success with the fetched data
            SteamUiState.Success(
                playerCounts = playerCount,
                titleUpdated = title,
                appId = appIds,
                headerImage = headerImage,
                priceUS = priceUS,
                discount = discount,
                shortDescription = shortDescription,
                gameReview = gameReview
            )
        } catch (e: IOException) {
            // If an IOException occurs, set the UI state to Error
            SteamUiState.Error
        } catch (e: HttpException) {
            // If an HttpException occurs, set the UI state to Error
            SteamUiState.Error
        }
    }

    /**
     * Sets the player count for a specific game and updates the UI state accordingly.
     */
    suspend fun setAppDetails(gameId: String) {
        try {

            withContext(Dispatchers.IO) {
                updatePlayerCounts(gameId)
                updateAppDetails(gameId)
                updateGameReview(gameId)
            }

            val game = SteamApplication.database?.gameDao()?.getGameById(gameId)


            if (steamUiState is SteamUiState.Success) {
                // Update the UI state with the new player count and game title
                if (game != null) {
                    steamUiState = (steamUiState as SteamUiState.Success).copy(
                        playerCounts = game.playerCount,
                        titleUpdated = game.gameName,
                        appId = game.gameId.toInt(),
                        headerImage = game.headerImage,
                        priceUS = game.priceUS,
                        discount = game.discount,
                        shortDescription = game.shortDescription,
                        gameReview = game.reviewScore
                    )
                }
            }

            // Assuming playerCount.response contains the player count as a String
        } catch (e: IOException) {
            // Handle error case
        } catch (e: HttpException) {
            // Handle error case
        }
    }

    private suspend fun updatePlayerCounts(gameId: String) {
        // Example API call to fetch player count (assuming such an endpoint and method exist)
        val apiKey = BuildConfig.API_KEY
        val gameDetails = apiService.getPlayerCounts(apiKey = apiKey, appId =  gameId)
        SteamApplication.database?.gameDao()?.updatePlayerCount(gameId = gameId, playerCount = gameDetails.response.player_count)
    }


    private suspend fun updateAppDetails(gameId: String) {
        val headerImage = apiService.getGameInfo(gameId)[gameId]?.data?.header_image ?: "No Image Url"
        val priceUS = apiService.getGameInfo(gameId)[gameId]?.data?.price_overview?.final_formatted ?: "N/A"
        val discount = apiService.getGameInfo(gameId)[gameId]?.data?.price_overview?.discount_percent ?: 0
        val shortDescription = apiService.getGameInfo(gameId)[gameId]?.data?.short_description ?: "None Available"
        val lastUpdated = System.currentTimeMillis()
        SteamApplication.database?.gameDao()?.updateAppData(gameId = gameId, headerImage = headerImage, priceUS = priceUS, discount = discount, shortDescription = shortDescription, lastUpdated = lastUpdated)
    }

    private suspend fun updateGameReview(gameId: String) {
        val gameReview = apiService.getGameReviews(appId = gameId)
        SteamApplication.database?.gameDao()?.updateGameReview(gameId = gameId, gameReview = gameReview.query_summary.review_score)
    }

    fun searchGames(query: String): LiveData<List<Game>> {
        val result = MutableLiveData<List<Game>>()
        viewModelScope.launch(dispatcher) {
            result.value = SteamApplication.database?.gameDao()?.searchGames("%$query%")
        }
        return result
    }

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[APPLICATION_KEY] as SteamApplication)
//                val repository = application.container.steamRepositoryApi
//                SteamPlayerCountViewModel(repository)
//                }
//            }
//        }


}

