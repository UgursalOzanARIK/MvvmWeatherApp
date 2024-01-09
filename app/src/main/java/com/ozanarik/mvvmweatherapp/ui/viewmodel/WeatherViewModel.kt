package com.ozanarik.mvvmweatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozanarik.mvvmweatherapp.Forecast
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.business.repository.WeatherForecastRepository
import com.ozanarik.mvvmweatherapp.utils.DataStoreManager
import com.ozanarik.mvvmweatherapp.utils.Resource
import com.ozanarik.mvvmweatherapp.utils.WeatherIconHelperClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor
            (
    private val weatherForecastRepository: WeatherForecastRepository,
    private val dataStoreManager: DataStoreManager,
            )
    :ViewModel() {

    private val _forecastResponse:MutableStateFlow<Resource<Forecast>> = MutableStateFlow(Resource.Loading())
    val forecastResponse:StateFlow<Resource<Forecast>> = _forecastResponse

    private val _forecastByCityName:MutableStateFlow<Resource<Forecast>> = MutableStateFlow(Resource.Loading())
    val forecastByCityName:StateFlow<Resource<Forecast>> = _forecastByCityName


    private val _locationLatLon = MutableStateFlow(Pair(0.0,0.0))
    val locationLatLon:StateFlow<Pair<Double,Double>> = _locationLatLon


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun setDarkMode(isDarkMode:Boolean)=viewModelScope.launch {
        dataStoreManager.setDarkMode(isDarkMode)
    }
    fun getDarkMode()=dataStoreManager.getDarkModeKey()


    suspend fun setLocationLatLonKeys(pair: Pair<Double,Double>){
        dataStoreManager.setLatLon(pair)
    }

    fun getLocationLatLon()=viewModelScope.launch{
        dataStoreManager.getLatitudeLongitudeKeys().collect{pair->

            _locationLatLon.value = pair
        }
    }


    fun getWeatherForecastByLatitudeLongitude(lat:String,lon:String)= viewModelScope.launch {

        try {
            val weatherForecastResponse = withContext(Dispatchers.IO){
                weatherForecastRepository.getWeatherForecastByLatitudeLongitude(lat, lon)
            }

            withContext(Dispatchers.Main){

                weatherForecastResponse.collect{forecastResult->

                    when(forecastResult){

                        is Resource.Success->   _forecastResponse.value         =       Resource.Success(forecastResult.data!!)
                        is Resource.Error->     _forecastResponse.value         =       Resource.Error(forecastResult.message!!)
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

    fun getWeatherForecastByCityName(cityName:String)=viewModelScope.launch {

       try {
           val weatherForecastByCityNameResponse = withContext(Dispatchers.IO){

               weatherForecastRepository.getWeatherForecastByCityName(cityName)
           }

           withContext(Dispatchers.Main){
               weatherForecastByCityNameResponse.collect{forecastResult->

                   when(forecastResult)
                   {
                       is Resource.Success  ->  _forecastByCityName.value = Resource.Success(forecastResult.data!!)
                       is Resource.Error    ->  _forecastByCityName.value = Resource.Error(forecastResult.message!!)
                       is Resource.Loading  ->  _forecastByCityName.value = Resource.Loading()
                   }

               }
           }
       }catch (e:Exception){
           withContext(Dispatchers.Main){
               _forecastByCityName.value = Resource.Error(e.localizedMessage?:e.message!!)
           }

       }catch (e:IOException){
           withContext(Dispatchers.IO){
               _forecastByCityName.value = Resource.Error(e.localizedMessage?:e.message!!)
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