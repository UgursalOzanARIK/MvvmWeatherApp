package com.ozanarik.mvvmweatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozanarik.mvvmweatherapp.Forecast
import com.ozanarik.mvvmweatherapp.business.repository.WeatherForecastRepository
import com.ozanarik.mvvmweatherapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherForecastRepository: WeatherForecastRepository) :ViewModel() {

    private val _forecastResponse:MutableStateFlow<Resource<Forecast>> = MutableStateFlow(Resource.Loading())
    val forecastResponse:StateFlow<Resource<Forecast>> = _forecastResponse


    fun getWeatherForecastByLatitudeLongitude(lat:String,lon:String)= viewModelScope.launch {

        weatherForecastRepository.getWeatherForecastByLatitudeLongitude(lat,lon).collect{forecast->
            when(forecast){
                is Resource.Success->{
                    forecast.data?.let { _forecastResponse.value = Resource.Success(it) }
                }
                is Resource.Loading->{
                    _forecastResponse.value = Resource.Loading()
                }
                is Resource.Error->{
                    _forecastResponse.value = Resource.Error(forecast.message.toString())
                }
            }
        }
    }
}