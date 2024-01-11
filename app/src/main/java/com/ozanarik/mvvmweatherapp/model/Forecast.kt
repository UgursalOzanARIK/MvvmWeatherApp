package com.ozanarik.mvvmweatherapp

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Forecast (

    @SerializedName("cod"     ) var cod     : String?         = null,
    @SerializedName("message" ) var message : Int?            = null,
    @SerializedName("cnt"     ) var cnt     : Int?            = null,
    @SerializedName("list"    ) var weatherList    : ArrayList<WeatherList> = arrayListOf(),
    @SerializedName("city"    ) var city    : City?           = City()

):Serializable