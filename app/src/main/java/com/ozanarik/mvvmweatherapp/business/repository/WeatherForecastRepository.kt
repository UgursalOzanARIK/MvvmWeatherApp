package com.ozanarik.mvvmweatherapp.business.repository

import com.ozanarik.mvvmweatherapp.Forecast
import com.ozanarik.mvvmweatherapp.business.remote.WeatherApi
import com.ozanarik.mvvmweatherapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException

class WeatherForecastRepository @Inject constructor(private val weatherApi: WeatherApi) {

    suspend fun getWeatherForecastByLatitudeLongitude(lat:String,lon:String):Flow<Resource<Forecast>> = flow {

        try {

            val forecastResponse = withContext(Dispatchers.IO){
                weatherApi.getWeatherByLatitudeLongitude(latitude = lat, longitude = lon )
            }

            emit(Resource.Success(forecastResponse.body()!!))
        }catch (e:Exception){

            emit(Resource.Error(e.localizedMessage?:"An Error Occured"))
        }catch (e:IOException){
           emit(Resource.Error(e.localizedMessage?:"An Error Occured"))

        }
    }


    suspend fun getWeatherForecastByCityName(cityName:String):Flow<Resource<Forecast>> = flow {

        try {
            val forecastResponse = withContext(Dispatchers.IO){

                weatherApi.getWeatherByCityName(q =  cityName)
            }
            emit(Resource.Success(forecastResponse.body()!!))

        } catch (e:Exception){

            emit(Resource.Error(e.localizedMessage?:e.message.toString()))

        }catch (e:IOException){
            emit(Resource.Error(e.localizedMessage?:e.message.toString()))
        }
    }
}