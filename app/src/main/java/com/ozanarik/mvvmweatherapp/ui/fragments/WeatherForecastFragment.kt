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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherForecastBinding
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherAdapter
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherForecastFragment : Fragment() {
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var binding: FragmentWeatherForecastBinding
    private lateinit var weatherAdapter: WeatherAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        binding = FragmentWeatherForecastBinding.inflate(inflater,container,false)
        weatherAdapter = WeatherAdapter()




        return (binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpWeatherRecyclerView()
        getWeatherForecast("44.34","10.99")

    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecast(latitude:String,longitude:String){

        weatherViewModel.getWeatherForecastByLatitudeLongitude(latitude,longitude)
        viewLifecycleOwner.lifecycleScope.launch {

            weatherViewModel.forecastResponse.collect{forecastResponse->
                when(forecastResponse){
                    is Resource.Success->{
                        val forecastList = forecastResponse.data!!.list
                        val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                        val todayList = mutableListOf<WeatherList>()

                        forecastList.forEach { weather->

                            if (weather.dtTxt!!.split("\\s".toRegex()).contains(todayDate)){
                                todayList.add(weather)
                            }
                        }

                        todayList.let { weatherAdapter.differList.submitList(it) }

                        binding.tvWindSpeed.text = todayList[0].wind!!.speed.toString()
                        binding.tvHumidity.text = todayList[0].main!!.humidity.toString()
                        binding.tvCityName.text = forecastResponse.data.city!!.name
                        binding.imageViewNowIcon.setImageResource(R.drawable.humidity)

                        binding.tvSunriseSet.text = todayList[0].weather[0].description!!.uppercase()

                  //      binding.tvSunriseSet.text = "${forecastResponse.data.city!!.sunrise} / ${forecastResponse.data.city!!.sunset}"

                        val temp = todayList[0].main!!.temp!!.minus(272.15).toInt()

                        binding.tvTempToday.text = "$temp °C"
                        binding.imageViewHumidity.setImageResource(R.drawable.forecasticon)
                        binding.imageViewWindSpeed.setImageResource(R.drawable.outline_wb_sunny_24)


                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(),forecastResponse.message.toString(),Toast.LENGTH_LONG).show()
                    }
                    is Resource.Loading->{
                        Toast.makeText(requireContext(),"Fetching Data",Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }

    private fun setUpWeatherRecyclerView(){

        binding.rvWeather.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = weatherAdapter
        }
    }
}