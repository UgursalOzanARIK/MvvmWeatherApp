package com.ozanarik.mvvmweatherapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherForecastBinding
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherAdapter
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherTodayAdapter
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Resource
import com.ozanarik.mvvmweatherapp.utils.capitalizeWords
import com.ozanarik.mvvmweatherapp.utils.isSplittable
import com.ozanarik.mvvmweatherapp.utils.kelvinToCelsius
import com.ozanarik.mvvmweatherapp.utils.makeInvisible
import com.ozanarik.mvvmweatherapp.utils.makeVisible
import com.ozanarik.mvvmweatherapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class WeatherForecastFragment : Fragment(),SearchView.OnQueryTextListener {
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var binding: FragmentWeatherForecastBinding
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var weatherTodayAdapter: WeatherTodayAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        binding = FragmentWeatherForecastBinding.inflate(inflater,container,false)
        checkDarkMode()

        binding.toolbar.title = "Forecast"
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu,menu)
                val searchItem = menu.findItem(R.id.action_Search)
                val searchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(this@WeatherForecastFragment)


            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return false
            }
        },viewLifecycleOwner,Lifecycle.State.RESUMED)




        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWeatherRecyclerView()
        getWeatherForecastToday("51.507351","-0.127758")
        getWeeklyForecast("51.507351","-0.127758")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastToday(latitude:String, longitude:String){

        showTodayForecast(latitude,longitude)
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

                            if(!weatherList.dtTxt!!.isSplittable(todayDate)){

                                fiveDaysWeatherList.add(weatherList)
                            }
                        }

                        fiveDaysWeatherList.let { weatherAdapter.differList.submitList(it) }
                        binding.loadingLottieAnim.makeInvisible()

                    }
                    is Resource.Loading->{
                        //anim
                        binding.loadingLottieAnim.makeVisible()
                        binding.loadingLottieAnim.playAnimation()
                    }
                    is Resource.Error->{
                        toast(forecastResponse.message!!)
                    }
                }
            }
        }
    }

    private fun checkDarkMode(){
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.getDarkMode().collect{isDarkMode->
                when(isDarkMode){
                    true->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    false->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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
                       val tomorrowDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))


                       val todayList = mutableListOf<WeatherList>()
                       val forecastList = forecastResponse.data!!.list

                       forecastList.forEach { weatherList->

                           if (weatherList.dtTxt!!.isSplittable(todayDate) || weatherList.dtTxt!!.isSplittable(tomorrowDate)){

                               todayList.add(weatherList)
                           }
                       }
                       todayList.let { weatherTodayAdapter.differList.submitList(it) }

                       binding.apply {

                           tvCityName.text = forecastResponse.data.city!!.name
                           val weatherDescriptionText = todayList[0].weather[0].description.capitalizeWords()

                           tvDescription.text = weatherDescriptionText
                           tvTempToday.text = "${todayList[0].main!!.temp!!.kelvinToCelsius()} °C"
                           imageViewWind.setImageResource(R.drawable.wind)
                           tvWind.text = todayList[0].wind!!.speed.toString()
                           imageViewHumidity.setImageResource(R.drawable.humidity)
                           tvHumidity.text = todayList[0].main!!.humidity.toString()

                           Log.e("asd",todayList[0].weather[0].icon.toString())

                       }

                       binding.loadingLottieAnim.makeInvisible()
                   }
                   is Resource.Error->{
                       toast(forecastResponse.message!!)
                   }
                   is Resource.Loading->{
                       //anim
                       binding.loadingLottieAnim.makeVisible()
                       binding.loadingLottieAnim.playAnimation()
                   }
               }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        Log.e("asd","hahah")

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return false
    }
}
