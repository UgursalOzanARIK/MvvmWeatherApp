package com.ozanarik.mvvmweatherapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherForecastBinding
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherAdapter
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherTodayAdapter
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Constants.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.ozanarik.mvvmweatherapp.utils.Resource
import com.ozanarik.mvvmweatherapp.utils.capitalizeWords
import com.ozanarik.mvvmweatherapp.utils.isSplittable
import com.ozanarik.mvvmweatherapp.utils.kelvinToCelsius
import com.ozanarik.mvvmweatherapp.utils.makeInvisible
import com.ozanarik.mvvmweatherapp.utils.makeVisible
import com.ozanarik.mvvmweatherapp.utils.showSnackbar
import com.ozanarik.mvvmweatherapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWeatherForecastBinding.inflate(inflater,container,false)
        initiateVariables()


        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWeatherRecyclerView()
        weatherViewModel.getLocationLatitudeLongitudeKeys()


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



        checkDarkMode()
        setupToolbar()
        binding.cardViewGetLocation.setOnClickListener {

            getWeatherForLocation()
            toast("Fetching Location Data...")

        }


    }

    private fun initiateVariables(){
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    private fun setupToolbar(){
        binding.toolbar.title = "Forecast"

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastToday(latitude:String, longitude:String){

        showTodayForecast(latitude,longitude)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeeklyForecast(latitude: String, longitude: String){
        showWeeklyForecast(latitude,longitude)
    }

    private fun checkPermissions(callback:(Double,Double)->Unit) {

        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED&&
            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){

                showSnackbar(
                    "Permission required to access location",
                    "Grant Permission")
                    {requirePermission()}
            }else{

                requirePermission()

            }

        }else{
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location:Location?->

                location?.let {

                    val longitude = it.longitude
                    val latitude = it.latitude

                    callback(latitude,longitude)
                    weatherViewModel.setLocationLatitudeLongitudeKeys(latitude,longitude)
                }?:run {
                    toast("Error fetching the location, please try again...")
                }
            }

        }
    }
    
    private fun getWeatherForLocation(){
        checkPermissions { latitude, longitude ->

            getWeeklyForecast(latitude.toString(),longitude.toString())
            getWeatherForecastToday(latitude.toString(),longitude.toString())
        }
    }


    private fun requirePermission(){
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
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
                           tvTempToday.text = "${todayList[0].main!!.temp!!.kelvinToCelsius()}"
                           imageViewWind.setImageResource(R.drawable.wind)
                           imageViewWind.setColorFilter(Color.WHITE)
                           tvWind.text = todayList[0].wind!!.speed.toString()
                           imageViewHumidity.setImageResource(R.drawable.humidity)
                           tvHumidity.text = todayList[0].main!!.humidity.toString()
                           imageViewNowIcon.setImageResource(weatherViewModel.getWeatherIcon(todayList[0].weather[0].icon!!))
                           imageViewThermo.setColorFilter(Color.WHITE)


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

    override fun onQueryTextSubmit(query: String?): Boolean {

        Log.e("asd","hahah")

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return false
    }
}
