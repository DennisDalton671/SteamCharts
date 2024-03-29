package com.example.steamcharts.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.steamcharts.SteamApplication
import com.example.steamcharts.data.Game
import com.example.steamcharts.data.SteamRepositoryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.steamcharts.data.GameDao

class HomeScreenViewModel(private val repository: SteamRepositoryApi) : ViewModel() {
    private val _topGamesLiveData = MutableLiveData<List<Game>>()
    val topGames: LiveData<List<Game>> = _topGamesLiveData

    // LiveData to track if the data is loading
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        viewModelScope.launch(Dispatchers.IO) {
            initializeDatabaseIfNeeded()
            val gamesList = SteamApplication.database?.gameDao()?.getTop5Games() ?: emptyList()
            _topGamesLiveData.postValue(gamesList)
            _isLoading.postValue(false)
        }
    }

    private suspend fun initializeDatabaseIfNeeded() {
        if (repository.isDatabaseEmpty()) {
            repository.populateDatabase()
        }
    }

}

