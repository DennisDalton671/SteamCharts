package com.example.steamcharts.data

import com.example.steamcharts.SteamApplication
import com.example.steamcharts.network.SteamApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val steamRepositoryApi: SteamRepositoryApi
}

class DefaultAppContainer : AppContainer {
    private val baseUrlApi = "https://api.steampowered.com/"
    private val baseUrlStore = "https://store.steampowered.com/"

    /**
     * Use the Retrofit builder to build a Retrofit object using a Gson converter
     */
    private val retrofitApi: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrlApi)
        .build()

    val retrofitDetails = Retrofit.Builder()
        .baseUrl(baseUrlStore)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val steamApi = retrofitDetails.create(SteamApiService::class.java)

    /**
     * Retrofit service object for creating API calls
     */
    private val retrofitServiceApi: SteamApiService by lazy {
        retrofitApi.create(SteamApiService::class.java)
    }

    /**
     * DI implementation for Steam repository
     */
    override val steamRepositoryApi: SteamRepositoryApi by lazy {
        NetworkSteamRepositoryApi(apiService = retrofitServiceApi, storeService = steamApi, gameDao = SteamApplication.database!!.gameDao())
    }
}
