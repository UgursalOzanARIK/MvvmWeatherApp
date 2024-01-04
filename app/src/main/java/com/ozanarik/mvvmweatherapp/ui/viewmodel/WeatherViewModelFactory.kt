package com.ozanarik.mvvmweatherapp.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ozanarik.mvvmweatherapp.business.repository.WeatherForecastRepository
import com.ozanarik.mvvmweatherapp.utils.DataStoreManager
import java.lang.IllegalArgumentException

class WeatherViewModelFactory( val dataStoreManager: DataStoreManager, val weatherForecastRepository: WeatherForecastRepository, val application: Application):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)){
            return WeatherViewModel(application,weatherForecastRepository, dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")

    }


}