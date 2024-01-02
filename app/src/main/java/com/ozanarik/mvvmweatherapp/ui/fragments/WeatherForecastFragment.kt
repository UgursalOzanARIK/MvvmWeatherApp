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
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherForecastBinding
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherAdapter
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Resource
import com.ozanarik.mvvmweatherapp.utils.WeatherIconHelperClass
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

        getWeatherForecast("39.965027","32.783058")
    //    sendCityName()
    }

 /*   private fun sendCityName(){
        binding.tvNextFiveDays.setOnClickListener {

            val navigation = WeatherForecastFragmentDirections.actionWeatherForecastFragmentToWeeklyWeatherFragment(binding.tvCityName.text.toString())
            Navigation.findNavController(it).navigate(navigation)
        }
    }*/


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

                        binding.tvCityName.text = forecastResponse.data.city!!.name
                        binding.imageViewNowIcon.setImageResource(getWeatherIcon(todayList[0].weather[0].icon!!))


                        Log.e("asd",todayList[0].weather[0].icon.toString())
                        val descriptionText = todayList[0].weather[0].description



                        binding.tvDescription.text = descriptionText

                        binding.tvNowTemp.text = "${todayList[0].main!!.temp} °C"
                        binding.tv3hTemp.text =  "${todayList[1].main!!.temp} °C"
    //                    binding.tv6hTemp.text =  "${todayList[2].main!!.temp} °C"
      //                  binding.tv9hTemp.text =  "${todayList[3].main!!.temp} °C"
        //                binding.tv12hTemp.text = "${todayList[4].main!!.temp} °C"

                        for (i in todayList){

                            Log.e("asd",i.dtTxt.toString())

                        }





                   //     binding.tv6.text = todayList[2].dtTxt
                 //       binding.tv9.text = todayList[3].dtTxt
               //         binding.tv12.text = todayList[4].dtTxt

                        binding.imageViewNow.setImageResource(getWeatherIcon(todayList[0].weather[0].icon!!))
                        binding.imageView3h.setImageResource(getWeatherIcon(todayList[1].weather[0].icon!!))
                     //   binding.imageView6h.setImageResource(getWeatherIcon(todayList[2].weather[0].icon!!))
                     //   binding.imageView9h.setImageResource(getWeatherIcon(todayList[3].weather[0].icon!!))
                     //   binding.imageView12h.setImageResource(getWeatherIcon(todayList[4].weather[0].icon!!))


                        val temp = todayList[0].main!!.temp!!.minus(272.15).toInt()

                        binding.tvTempToday.text = "$temp °C"


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


        //FOR 5 DAYS
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
                        Toast.makeText(requireContext(),"Fetching Data",Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(),forecastResponse.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    private fun setUpWeatherRecyclerView(){

        binding.rvWeather.apply {
            weatherAdapter = WeatherAdapter()
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = weatherAdapter
        }
    }


    private fun getWeatherIcon(weatherIconString: String):Int{

        return when(weatherIconString){

            WeatherIconHelperClass.CLEAR_SKY.weatherIconString->R.drawable.clearsky
            WeatherIconHelperClass.FEW_CLOUDS.weatherIconString->R.drawable.fewclouds
            WeatherIconHelperClass.SCATTERED_CLOUDS.weatherIconString->R.drawable.scatteredclouds
            WeatherIconHelperClass.BROKEN_CLOUDS.weatherIconString->R.drawable.brokenclouds
            WeatherIconHelperClass.SHOWER_RAIN.weatherIconString->R.drawable.rain
            WeatherIconHelperClass.RAIN.weatherIconString->R.drawable.rain
            WeatherIconHelperClass.THUNDERSTORM.weatherIconString->R.drawable.thunderstorm
            WeatherIconHelperClass.SNOW.weatherIconString->R.drawable.snow
            WeatherIconHelperClass.MIST.weatherIconString->R.drawable.mist
            WeatherIconHelperClass.OVERCAST_CLOUDS.weatherIconString->R.drawable.scatteredclouds
            WeatherIconHelperClass.LIGHT_RAIN.weatherIconString->R.drawable.rain

            else -> {R.drawable.clearsky}
        }

}
}