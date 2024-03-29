package com.example.steamcharts.model

data class AppDetailsResponse(
    val success: Boolean,
    val data: GameDetails
)

data class GameDetails(
    val name: String,
    val steam_appid: Int,
    val header_image: String,
    val price_overview: PriceOverview? = null,
    val short_description: String
)

data class PriceOverview(
    val currency: String,
    val final_formatted: String, // This can be your priceUS
    val discount_percent: Int
)