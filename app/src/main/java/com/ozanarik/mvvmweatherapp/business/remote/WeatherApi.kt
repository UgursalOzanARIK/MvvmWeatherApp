package com.ozanarik.mvvmweatherapp.business.remote

import com.ozanarik.mvvmweatherapp.Forecast
import com.ozanarik.mvvmweatherapp.utils.Constants.Companion.APPID
import com.ozanarik.mvvmweatherapp.utils.Resource
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast?")
    suspend fun getWeatherByLatitudeLongitude
                (

        @Query("lat")latitude:String,
        @Query("lon")longitude:String,
        @Query("appid")appid:String = APPID
                ):Response<Forecast>
}