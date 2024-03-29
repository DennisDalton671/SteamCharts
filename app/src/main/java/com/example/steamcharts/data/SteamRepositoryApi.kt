package com.example.steamcharts.data

import android.util.Log
import com.example.steamcharts.model.AppDetailsResponse
import com.example.steamcharts.model.ReviewResponse
import com.example.steamcharts.model.SteamApiResponse
import com.example.steamcharts.model.SteamAppList
import com.example.steamcharts.network.SteamApiService
import java.util.Locale


/**
 * Implementation of [SteamRepositoryApi] using Retrofit for Steam API-related calls.
 *
 * @param apiService Retrofit service for Steam API calls.
 */

interface SteamRepositoryApi {

    suspend fun getPlayerCounts(appId: String, apiKey: String): SteamApiResponse
    suspend fun getGameInfo(appId: String): Map<String, AppDetailsResponse>
    suspend fun getGameReviews(appId: String): ReviewResponse
    suspend fun getAppList() : SteamAppList
    suspend fun isDatabaseEmpty() : Boolean
    suspend fun populateDatabase()

}

class NetworkSteamRepositoryApi(
    private val gameDao: GameDao,
    private val apiService: SteamApiService,
    private val storeService: SteamApiService
) : SteamRepositoryApi {

    /**
     * Fetches player counts for a given Steam App ID.
     *
     * @param appId The Steam App ID.
     * @param apiKey The API key for authentication.
     * @return [SteamApiResponse] containing player count information.
     * @throws RuntimeException if the API request is unsuccessful or if the response body is null.
     */

    override suspend fun isDatabaseEmpty(): Boolean = gameDao.getAllGames().isEmpty()

    override suspend fun populateDatabase() {
        gameDao.deleteAllGames()
        println("Deletion passed")
        val response = apiService.getAppData() // Ensure this is a suspend function
        // Directly working with List should not cause issues
        //val games = response.applist.apps.app.filter { it.name.isNotEmpty() }
        val games = response.applist.apps.app.filter { it.name.isNotEmpty() }.map { app ->
            val searchName = normalizeString(app.name)
            Log.d("NormalizeString", "Input: ${app.name}, Output $searchName")
            Game(gameId = app.appid.toString(), gameName = app.name, searchName = searchName, playerCount = 0, headerImage = "No URL", priceUS = "$0.0", discount = 0, shortDescription = "None", reviewScore = 0)
       }
        if (games.isNotEmpty()) {
            gameDao.insertGame(games)
        }
//        games.forEach { app ->
//            val game = Game(gameId = app.appid.toString(), gameName = app.name, playerCount = 0)
//            gameDao.insertGame(game)
//        }
    }

    override suspend fun getPlayerCounts(appId: String, apiKey: String): SteamApiResponse {
        return apiService.getSteamPlayerCount(appId = appId, apiKey = apiKey)
    }

    /**
     * Fetches the list of Steam Apps.
     *
     * @return [SteamAppList] containing information about Steam Apps.
     * @throws RuntimeException if the API request is unsuccessful or if the response body is null.
     */

    fun normalizeString(input: String): String {
        return input.replace("[^A-Za-z0-9 ]".toRegex(), "").lowercase(Locale.ROOT)
    }

    override suspend fun getAppList() : SteamAppList{
        return apiService.getAppData()
    }

    override suspend fun getGameReviews(appId: String): ReviewResponse {
        return storeService.getGameReviews(appId, json = 1) // The API expects a json=1 query parameter
    }

    override suspend fun getGameInfo(appId: String): Map<String, AppDetailsResponse> {
        return storeService.getAppDetails(appId.toInt())
    }


}
