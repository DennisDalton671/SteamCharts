package com.example.steamcharts.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SteamAppList (
    val applist: SteamApps
)

@Serializable
data class SteamApps (
    val apps: SteamApp
)

@Serializable
data class SteamApp (
    val app: List<SteamAppData>
)

@Serializable
data class SteamAppData(
    val appid: Int,  // Unique identifier for the Steam app
    val name: String  // Name of the Steam app
)