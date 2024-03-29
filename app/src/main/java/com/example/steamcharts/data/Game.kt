package com.example.steamcharts.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val gameId: String,
    val gameName: String,
    val searchName: String,
    val playerCount: Int,
    val headerImage: String,
    val priceUS: String,
    val discount: Int,
    val shortDescription: String,
    val reviewScore: Int,
    val lastUpdated: Long = System.currentTimeMillis()

)
