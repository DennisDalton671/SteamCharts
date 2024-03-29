package com.example.steamcharts.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing the response from the Steam API for player counts.
 * @param response The player count details.
 */

@Serializable
data class SteamApiResponse(
    val response: SteamPlayerCount
)

@Serializable
data class SteamPlayerCount(
    val player_count: Int,  // Number of players for the Steam app
    val result: Int  // Result code or status for the Steam app
)
