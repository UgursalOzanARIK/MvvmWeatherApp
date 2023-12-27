package com.ozanarik.mvvmweatherapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ozanarik.mvvmweatherapp.Forecast
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherForecastBinding
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherForecastFragment : Fragment() {
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var binding: FragmentWeatherForecastBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        binding = FragmentWeatherForecastBinding.inflate(inflater,container,false)



        binding.buttonGet.setOnClickListener {

            weatherViewModel.getWeatherForecastByLatitudeLongitude("44.34","10.99")
            viewLifecycleOwner.lifecycleScope.launch {
                weatherViewModel.forecastResponse.collect{forecastResponse->
                    when(forecastResponse){
                        is Resource.Success->{

                            val date = LocalDate.now()
                            val currentDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                            val tomorrowDate = date.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))


                            val tomorrowForecastList = mutableListOf<WeatherList>()

                            val forecastList = forecastResponse.data!!.list
                            val todayForecastList = mutableListOf<WeatherList>()

                            forecastList.forEach { weather->

                                if (weather.dtTxt!!.split("\\s".toRegex()).contains(currentDate)){
                                    todayForecastList.add(weather)
                                }else if (weather.dtTxt!!.split("\\s".toRegex()).contains(tomorrowDate)){
                                    tomorrowForecastList.add(weather)
                                }

                                tomorrowForecastList.forEach { weatherTomorrow->
                                    Log.e("tomorrow","$tomorrowDate = ${weatherTomorrow.dtTxt}")
                                }


                                todayForecastList.forEach { weatherList->
                                    Log.e("asd",weatherList.dtTxt.toString())

                                }


                            }

                        }
                        is Resource.Error->{
                            Log.e("asd",forecastResponse.message.toString())
                        }
                        is Resource.Loading->{

                            Log.e("asd","loading")
                        }
                    }

                }
            }
        }

        return (binding.root)
    }
}