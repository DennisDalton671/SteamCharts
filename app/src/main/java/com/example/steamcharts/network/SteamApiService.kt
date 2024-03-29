package com.example.steamcharts.network

import com.example.steamcharts.data.Game
import com.example.steamcharts.model.AppDetailsResponse
import com.example.steamcharts.model.ReviewResponse
import com.example.steamcharts.model.SteamApiResponse
import com.example.steamcharts.model.SteamAppList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SteamApiService {

    @GET("games")
    suspend fun getAllGames(): List<Game>

    @GET("games/{gameId}")
    suspend fun getGameById(@Path("gameId") gameId: String) : Game

    @GET("ISteamUserStats/GetNumberOfCurrentPlayers/v1/")
    suspend fun getSteamPlayerCount(
        @Query("appid") appId: String,
        @Query("key") apiKey: String
    ): SteamApiResponse

    @GET("ISteamApps/GetAppList/v1/")
    suspend fun getAppData() : SteamAppList

    @GET("api/appdetails")
    suspend fun getAppDetails(@Query("appids") appid: Int): Map<String, AppDetailsResponse>

    @GET("appreviews/{appId}")
    suspend fun getGameReviews(@Path("appId") appId: String, @Query("json") json: Int): ReviewResponse
}

