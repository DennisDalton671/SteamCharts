package com.example.steamcharts

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.steamcharts.data.AppDatabase
import com.example.steamcharts.data.Game
import com.example.steamcharts.data.GameDao
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class GameDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var gameDao: GameDao

    @Before
    fun setup() {
        // Use an in-memory database for testing, which will not be persisted
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        gameDao = database.gameDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetGame() = runBlocking {
        val game = Game(gameId = "1", gameName = "Test Game", playerCount = 0, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None")
        gameDao.insertGame(listOf(game))
        val allGames = gameDao.getAllGames()
        assertTrue(allGames.contains(game))
    }

    @Test
    fun deleteAllGames_clearsAllData() = runBlocking {
        val game = Game(gameId = "1", gameName = "Test Game", playerCount = 0, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None")
        // Arrange: Insert some games first
        val games = listOf(
            // ... create a list of games
            game
        )
        gameDao.insertGame(games)

        // Act: Delete all games
        gameDao.deleteAllGames()

        // Assert: Verify that no games exist in the database anymore
        val allGames = gameDao.getAllGames()
        assertTrue(allGames.isEmpty())
    }

    @Test
    fun updatePlayerCount_updatesCountSuccessfully() = runBlocking {
        // Arrange: Insert a game first
        val game = Game(gameId = "1", gameName = "Test Game", playerCount = 0, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None")
        gameDao.insertGame(listOf(game))

        // Act: Update the player count for the inserted game
        val newPlayerCount = 123
        gameDao.updatePlayerCount(game.gameId, newPlayerCount)

        // Assert: Verify that the player count has been updated
        val updatedGame = gameDao.getGameById(game.gameId)
        assertEquals(newPlayerCount, updatedGame?.playerCount)
    }

    @Test
    fun updateAppData_updatesDataSuccessfully() = runBlocking {
        // Arrange: Insert a game first
        val game = Game(gameId = "1", gameName = "Test Game", playerCount = 0, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None")
        gameDao.insertGame(listOf(game))

        // Act: Update the app data for the inserted game
        val newHeaderImage = "new_image_url"
        val newPriceUS = "$9.99"
        val newDiscount = 10
        val newShortDescription = "New description"
        gameDao.updateAppData(game.gameId, newHeaderImage, newPriceUS, newDiscount, newShortDescription, System.currentTimeMillis())

        // Assert: Verify that the app data has been updated
        val updatedGame = gameDao.getGameById(game.gameId)
        assertEquals(newHeaderImage, updatedGame?.headerImage)
        assertEquals(newPriceUS, updatedGame?.priceUS)
        assertEquals(newDiscount, updatedGame?.discount)
        assertEquals(newShortDescription, updatedGame?.shortDescription)
    }

    @Test
    fun searchGames_returnsCorrectResults() = runBlocking {
        // Arrange: Insert multiple games first
        val game1 = Game(gameId = "1", gameName = "Test Game", playerCount = 0, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "test1", shortDescription = " None")
        val game2 = Game(gameId = "2", gameName = "Test Game2", playerCount = 1, discount = 1,
            headerImage = "Image2", priceUS = "1", reviewScore = 1, searchName = "test2", shortDescription = " None2")
        gameDao.insertGame(listOf(game1, game2))

        // Act: Perform a search with a query that only matches one of the games
        val searchQuery = "%test1%"
        val searchResults = gameDao.searchGames(searchQuery)

        // Assert: Verify that only the game with searchName "test1" is returned
        assertTrue(searchResults.any { it.gameId == game1.gameId })
        assertTrue(searchResults.none { it.gameId == game2.gameId })
    }

    @Test
    fun updateReview_updatesCountSuccessfully() = runBlocking {
        // Arrange: Insert a game first
        val game = Game(gameId = "1", gameName = "Test Game", playerCount = 0, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None")
        gameDao.insertGame(listOf(game))

        // Act: Update the player count for the inserted game
        val newReview = 123
        gameDao.updateGameReview(game.gameId, newReview)

        // Assert: Verify that the player count has been updated
        val updatedGame = gameDao.getGameById(game.gameId)
        assertEquals(newReview, updatedGame?.reviewScore)
    }


    @Test
    fun getTop5Games_returnsTopGames() = runBlocking {
        // Arrange: Insert multiple games with different player counts
        val games = listOf(
            Game(gameId = "1", gameName = "Game 1", playerCount = 100, discount = 0,
                headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None"),
        Game(gameId = "2", gameName = "Game 2", playerCount = 200, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None"),
        Game(gameId = "3", gameName = "Game 3", playerCount = 300, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None"),
        Game(gameId = "4", gameName = "Game 4", playerCount = 400, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None"),
        Game(gameId = "5", gameName = "Game 5", playerCount = 500, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None"),
        Game(gameId = "6", gameName = "Game 6", playerCount = 600, discount = 0,
            headerImage = "Image", priceUS = "0", reviewScore = 0, searchName = "Test", shortDescription = " None")
        )
        gameDao.insertGame(games)

        // Act: Retrieve top 5 games
        val topGames = gameDao.getTop5Games()

        // Assert: Verify that only the top 5 games based on player count are returned
        assertEquals(5, topGames.size)
        assertTrue(topGames.none { it.gameId == "1" }) // Assuming "Game 1" has the least players
        assertTrue(topGames.sortedByDescending { it.playerCount } == topGames) // Ensure the list is sorted by player count
    }


}