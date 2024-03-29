package com.example.steamcharts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.steamcharts.data.AppDatabase
import com.example.steamcharts.data.Game
import com.example.steamcharts.data.GameDao
import com.example.steamcharts.data.SteamRepositoryApi
import com.example.steamcharts.model.SteamApiResponse
import com.example.steamcharts.model.SteamPlayerCount
import com.example.steamcharts.network.SteamApiService
import com.example.steamcharts.ui.viewmodels.SteamPlayerCountViewModel
import com.example.steamcharts.ui.viewmodels.SteamUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when`
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import android.content.Context
import androidx.test.core.app.ApplicationProvider

@RunWith(AndroidJUnit4::class)
class SteamPlayerCountViewModelTest {


    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockApiService: SteamRepositoryApi

    private lateinit var viewModel: SteamPlayerCountViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = SteamPlayerCountViewModel(mockApiService, testDispatcher, "testGameId")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun getSteamPlayerCounts_setsSuccessState() = runTest {
        // Arrange
        val expectedGame = Game(gameId = "0", gameName = "", playerCount = 0, discount = 0,
            headerImage = "No URL", priceUS = "$0.0", reviewScore = 0, searchName = "Test", shortDescription = "None")


        whenever(mockApiService.getPlayerCounts(anyString(), anyString())).thenReturn(
            SteamApiResponse(SteamPlayerCount(player_count = expectedGame.playerCount, result = 1))
        )

        // Act
        viewModel.getSteamPlayerCounts("0")

        // Assert
        val expectedUiState = SteamUiState.Success(
            playerCounts = expectedGame.playerCount,
            titleUpdated = expectedGame.gameName,
            appId = expectedGame.gameId.toInt(),
            headerImage = expectedGame.headerImage,
            priceUS = expectedGame.priceUS,
            discount = expectedGame.discount,
            shortDescription = expectedGame.shortDescription,
            gameReview = expectedGame.reviewScore
        )
        assertEquals(expectedUiState, viewModel.steamUiState)
    }



}