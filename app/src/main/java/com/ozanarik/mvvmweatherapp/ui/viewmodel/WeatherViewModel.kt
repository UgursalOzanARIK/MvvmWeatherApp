package com.ozanarik.mvvmweatherapp.ui.viewmodel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozanarik.mvvmweatherapp.Forecast
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.business.repository.WeatherForecastRepository
import com.ozanarik.mvvmweatherapp.utils.DataStoreManager
import com.ozanarik.mvvmweatherapp.utils.Resource
import com.ozanarik.mvvmweatherapp.utils.WeatherIconHelperClass
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor
            (
    application:Application,
    private val weatherForecastRepository: WeatherForecastRepository,
    private val dataStoreManager: DataStoreManager,
            )
    :AndroidViewModel(application = application) {

    private val _forecastResponse:MutableStateFlow<Resource<Forecast>> = MutableStateFlow(Resource.Loading())
    val forecastResponse:StateFlow<Resource<Forecast>> = _forecastResponse


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    init {
        getWeatherForecastByLatitudeLongitude(lat = "51.507351", lon = "-0.127758")
    }


    fun setDarkMode(isDarkMode:Boolean)=viewModelScope.launch {
        dataStoreManager.setDarkMode(isDarkMode)
    }
    fun getDarkMode()=dataStoreManager.getDarkMode()


    fun getWeatherForecastByLatitudeLongitude(lat:String,lon:String)= viewModelScope.launch {

        try {
            val weatherForecastResponse = withContext(Dispatchers.IO){
                weatherForecastRepository.getWeatherForecastByLatitudeLongitude(lat, lon)
            }

            withContext(Dispatchers.Main){

                weatherForecastResponse.collect{forecast->

                    when(forecast){

                        is Resource.Success->   _forecastResponse.value         =       Resource.Success(forecast.data!!)
                        is Resource.Error->     _forecastResponse.value         =       Resource.Error(forecast.message!!)
                        is Resource.Loading->   _forecastResponse.value         =       Resource.Loading()
                    }

                }
            }

        }catch (e:Exception){

            withContext(Dispatchers.Main){
                _forecastResponse.value = Resource.Error(e.message?:e.localizedMessage)
            }

        }catch (e:IOException){
            withContext(Dispatchers.IO){
                _forecastResponse.value = Resource.Error(e.message?:e.localizedMessage)
            }
        }
    }


    fun getWeatherIcon(weatherIconString: String):Int{

        return when(weatherIconString){

            WeatherIconHelperClass.CLEAR_SKY.weatherIconString-> R.drawable.clearsky
            WeatherIconHelperClass.FEW_CLOUDS.weatherIconString-> R.drawable.fewclouds
            WeatherIconHelperClass.SCATTERED_CLOUDS.weatherIconString-> R.drawable.scatteredclouds
            WeatherIconHelperClass.BROKEN_CLOUDS.weatherIconString-> R.drawable.brokenclouds
            WeatherIconHelperClass.SHOWER_RAIN.weatherIconString-> R.drawable.rain
            WeatherIconHelperClass.RAIN.weatherIconString-> R.drawable.rain
            WeatherIconHelperClass.THUNDERSTORM.weatherIconString-> R.drawable.thunderstorm
            WeatherIconHelperClass.SNOW.weatherIconString-> R.drawable.snow
            WeatherIconHelperClass.MIST.weatherIconString-> R.drawable.mist
            WeatherIconHelperClass.OVERCAST_CLOUDS.weatherIconString-> R.drawable.scatteredclouds
            WeatherIconHelperClass.LIGHT_RAIN.weatherIconString-> R.drawable.rain
            WeatherIconHelperClass.SCATTERED_CLOUDS_1.weatherIconString->R.drawable.scatteredclouds

            else -> {
                R.drawable.clearsky}
        }
}

}