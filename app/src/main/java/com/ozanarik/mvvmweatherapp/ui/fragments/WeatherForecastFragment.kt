package com.ozanarik.mvvmweatherapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherForecastBinding
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherAdapter
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherTodayAdapter
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Resource
import com.ozanarik.mvvmweatherapp.utils.WeatherIconHelperClass
import com.ozanarik.mvvmweatherapp.utils.capitalizeWords
import com.ozanarik.mvvmweatherapp.utils.kelvinToCelsius
import com.ozanarik.mvvmweatherapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class WeatherForecastFragment : Fragment() {
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var binding: FragmentWeatherForecastBinding
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var weatherTodayAdapter: WeatherTodayAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        binding = FragmentWeatherForecastBinding.inflate(inflater,container,false)


        setUpWeatherRecyclerView()

        return (binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getWeatherForecastToday("51.507351","-0.127758")
        getWeeklyForecast("51.507351","-0.127758")

    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastToday(latitude:String,longitude:String){

        showTodayForecast(latitude,longitude)


        //FOR 5 DAYS


    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeeklyForecast(latitude: String, longitude: String){

        showWeeklyForecast(latitude,longitude)

    }

    private fun setUpWeatherRecyclerView(){

        binding.rvWeather.apply {
            weatherAdapter = WeatherAdapter()
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = weatherAdapter
        }

        binding.rvToday.apply {
            weatherTodayAdapter = WeatherTodayAdapter()
            layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL)
            setHasFixedSize(true)
            adapter = weatherTodayAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showWeeklyForecast(latitude: String, longitude: String){
        weatherViewModel.getWeatherForecastByLatitudeLongitude(latitude,longitude)
        viewLifecycleOwner.lifecycleScope.launch {

            weatherViewModel.forecastResponse.collect{forecastResponse->

                when(forecastResponse){
                    is Resource.Success->{

                        val fiveDaysWeatherList:MutableList<WeatherList> = mutableListOf()
                        val forecastList = forecastResponse.data!!.list
                        val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                        forecastList.forEach { weatherList->

                            if(!weatherList.dtTxt!!.split("\\s".toRegex()).contains(todayDate)){

                                fiveDaysWeatherList.add(weatherList)
                            }
                        }

                        fiveDaysWeatherList.let { weatherAdapter.differList.submitList(it) }
                    }
                    is Resource.Loading->{
                        //anim
                    }
                    is Resource.Error->{
                        toast(forecastResponse.message!!)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTodayForecast(latitude: String, longitude: String){


        weatherViewModel.getWeatherForecastByLatitudeLongitude(latitude,longitude)
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.forecastResponse.collect{forecastResponse->

               when(forecastResponse){

                   is Resource.Success->{

                       val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                       val todayList = mutableListOf<WeatherList>()
                       val forecastList = forecastResponse.data!!.list

                       forecastList.forEach { weatherList->

                           if (weatherList.dtTxt!!.split("\\s".toRegex()).contains(todayDate)){

                               todayList.add(weatherList)
                           }
                       }
                       todayList.let { weatherTodayAdapter.differList.submitList(it) }


                       binding.apply {

                           tvCityName.text = forecastResponse.data.city!!.name
                           val weatherDesciptionText = todayList[0].weather[0].description.capitalizeWords()

                           tvDescription.text = weatherDesciptionText
                           tvTempToday.text = todayList[0].main!!.temp!!.kelvinToCelsius().toString()
                           imageViewNowIcon.setImageResource(weatherViewModel.getWeatherIcon(todayList[0].weather[0].icon!!))
                       }
                   }
                   is Resource.Error->{
                       toast(forecastResponse.message!!)
                   }
                   is Resource.Loading->{
                       //anim
                   }
               }
            }
        }
    }

}
