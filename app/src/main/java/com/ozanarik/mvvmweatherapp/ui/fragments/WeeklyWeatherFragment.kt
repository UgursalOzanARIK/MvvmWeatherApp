package com.ozanarik.mvvmweatherapp.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeeklyWeatherBinding
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherAdapter
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeeklyWeatherFragment : Fragment() {
    private lateinit var binding: FragmentWeeklyWeatherBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var weatherAdapter: WeatherAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment


        binding = FragmentWeeklyWeatherBinding.inflate(inflater,container,false)
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        weatherAdapter = WeatherAdapter()
        setUpWeatherRv()
        getCityName()


        return binding.root
    }

    private fun getCityName(){
        val bundle:WeeklyWeatherFragmentArgs by navArgs()

        val cityName = bundle.cityName

        binding.tv5DaysCityName.text = cityName


    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel.getWeatherForecastByLatitudeLongitude("44.34","10.99")
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
                        Toast.makeText(requireContext(),"Fetching Data",Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(),forecastResponse.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    private fun setUpWeatherRv(){

        binding.weatherRv.apply {

            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weatherAdapter

        }


    }

}