package com.example.steamcharts

import android.app.Application
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Room
import com.example.steamcharts.data.AppContainer
import com.example.steamcharts.data.AppDatabase
import com.example.steamcharts.data.DefaultAppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SteamApplication : Application() {
    /** AppContainer instance used by the rest of the classes to obtain dependencies */
    lateinit var container: AppContainer
        private set

    companion object {
        var database: AppDatabase? = null
    }


    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(applicationContext)
        container = DefaultAppContainer()
    }
}