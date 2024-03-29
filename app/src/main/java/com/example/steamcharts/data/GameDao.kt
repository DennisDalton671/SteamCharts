package com.example.steamcharts.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    @Query("SELECT * FROM games")
    fun getAllGames(): List<Game>

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: List<Game>)

    @Query("SELECT * FROM games WHERE gameId = :gameId")
    suspend fun getGameById(gameId: String) : Game?

    @Query("SELECT COUNT(*) FROM games")
    suspend fun countGames(): Int

    @Query("UPDATE games SET playerCount = :playerCount WHERE gameId = :gameId")
    suspend fun updatePlayerCount(gameId: String, playerCount: Int)

    @Query("UPDATE games SET headerImage = :headerImage, priceUS = :priceUS, discount = :discount, shortDescription = :shortDescription, lastUpdated = :lastUpdated WHERE gameId = :gameId")
    suspend fun updateAppData(gameId: String, headerImage: String, priceUS: String, discount: Int, shortDescription: String, lastUpdated: Long)

    @Query("UPDATE games SET reviewScore = :gameReview WHERE gameId = :gameId")
    suspend fun updateGameReview(gameId: String, gameReview: Int)

    @Query("SELECT * FROM games WHERE searchName LIKE :query")
    suspend fun searchGames(query: String): List<Game>

    @Query("SELECT * FROM games ORDER BY playerCount DESC LIMIT 5")
    fun getTop5Games(): List<Game>

}