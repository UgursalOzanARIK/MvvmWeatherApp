package com.ozanarik.mvvmweatherapp.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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
import com.ozanarik.mvvmweatherapp.Forecast
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherForecastBinding
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherAdapter
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherAdapterRvClickListener
import com.ozanarik.mvvmweatherapp.ui.adapter.WeatherTodayAdapter
import com.ozanarik.mvvmweatherapp.ui.viewmodel.LocationViewModel
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Constants.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.ozanarik.mvvmweatherapp.utils.Resource
import com.ozanarik.mvvmweatherapp.utils.capitalizeWords
import com.ozanarik.mvvmweatherapp.utils.isSplittable
import com.ozanarik.mvvmweatherapp.utils.kelvinToCelsius
import com.ozanarik.mvvmweatherapp.utils.makeInvisible
import com.ozanarik.mvvmweatherapp.utils.makeVisible
import com.ozanarik.mvvmweatherapp.utils.showSnackbar
import com.ozanarik.mvvmweatherapp.utils.substringData
import com.ozanarik.mvvmweatherapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class WeatherForecastFragment : Fragment(),SearchView.OnQueryTextListener{
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var binding: FragmentWeatherForecastBinding
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var weatherTodayAdapter: WeatherTodayAdapter
    private lateinit var locationViewModel: LocationViewModel
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
            checkLocationServicesEnabled()
        }
    }
    private fun setupToolbar(){
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = "Forecast"
        binding.toolbar.setTitleTextColor(Color.WHITE)

    }


    private fun checkLocationServicesEnabled(){

        locationViewModel.areLocationServicesEnabled()
        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.locationServicesPermission.collect{locationServicesPerm->

                when(locationServicesPerm){
                    true->{
                        checkPermissions()
                    }
                    false->{
                        showEnableLocationServiceDialog()
                    }
                }
            }
        }
    }

    private fun checkPermissions(){

        locationViewModel.isLocationPermissionGranted()
        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.isGrantedLocationPermission.collect{locationPerm->
                when(locationPerm){
                    true->{

                        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED&&
                            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                            //

                        }else{
                            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location:Location?->

                                if (location!=null){
                                    val latitude = location.latitude
                                    val longitude =location.longitude

                                    viewLifecycleOwner.lifecycleScope.launch {
                                        weatherViewModel.setLocationLatLonKeys(Pair(latitude,longitude))
                                    }
                                    getWeatherForecast(latitude.toString(),longitude.toString(),false)
                                    getWeatherForecast(latitude.toString(),longitude.toString(),true)
                                }
                            }
                        }
                    }
                    false->{

                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                            showSnackbar("Permission required to access location","Grant Permission")
                            {requirePermission()}
                        }else {
                            requirePermission()
                        }
                    }
                }
            }
        }
    }

    private fun requirePermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun showEnableLocationServiceDialog(){

        val alertDialogBuilder = AlertDialog.Builder(requireContext()).apply {

            setTitle("Location services not enabled")
            setMessage("You need to enable location services to use this application")
            setPositiveButton("To the settings"){ _, _ ->

                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            setNegativeButton("Cancel"){ dialog, _ ->

                dialog.dismiss()
            }
            setCancelable(false)
            create().show()
        }
    }

    private fun getWeatherForecast(latitude: String, longitude: String,isTodayTomorrow:Boolean){
        weatherViewModel.getWeatherForecastByLatitudeLongitude(latitude,longitude)
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.forecastResponse.collect{forecastResult->

                when(forecastResult){
                    is Resource.Success->{

                        val forecastList = forecastResult.data!!.list

                        val today = LocalDate.now()
                        val todayDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val tomorrowDate = today.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val todayList = mutableListOf<WeatherList>()
                        val nextDaysList = mutableListOf<WeatherList>()

                        forecastList.forEach { weatherList->

                            if (weatherList.dtTxt!!.isSplittable(todayDate) || weatherList.dtTxt!!.isSplittable(tomorrowDate)){
                                if (isTodayTomorrow){
                                    todayList.add(weatherList)
                                }
                            }else if (weatherList.dtTxt!!.substringData(11,16)=="00:00"){

                                    nextDaysList.add(weatherList)
                            }
                        }
                        if (isTodayTomorrow){
                            updateUI(todayList)
                            weatherTodayAdapter.differList.submitList(todayList)
                        }else{
                            weatherAdapter.differList.submitList(nextDaysList)
                        }


                        binding.tvCityName.text = forecastResult.data.city!!.name
                        binding.loadingLottieAnim.makeInvisible()
                    }
                    is Resource.Error->{
                        toast(forecastResult.message?:"An error occured, please try again")
                    }
                    is Resource.Loading->{
                        binding.loadingLottieAnim.makeVisible()
                        binding.loadingLottieAnim.playAnimation()
                    }
                }
            }
        }
    }

    private fun updateUI(todayList:List<WeatherList>){
        binding.apply {

            if(todayList.isNotEmpty()){
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
        }
    }
    private fun initiateVariables(){
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationViewModel=ViewModelProvider(this)[LocationViewModel::class.java]
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
    private fun getWeatherForecastByCityName(cityName:String){
        weatherViewModel.getWeatherForecastByCityName(cityName)
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.forecastByCityName.collect{forecastResponse->
                when(forecastResponse){
                    is Resource.Success-> {

                        val cityNameQuery = forecastResponse.data!!.city!!.name
                        cityNameQuery?.let { weatherViewModel.setSearchedCityQuery(it) }

                        Log.e("asd",forecastResponse.data.toString())

                        val today = LocalDate.now()
                        val todayDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val tomorrow = today.plusDays(1)
                        val tomorrowDate = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))


                        val todayList = mutableListOf<WeatherList>()
                        val nextDaysList = mutableListOf<WeatherList>()
                        val forecastList = forecastResponse.data.list

                        forecastList.forEach { weatherList->

                            if (weatherList.dtTxt!!.isSplittable(todayDate) || weatherList.dtTxt!!.isSplittable(tomorrowDate)){

                                todayList.add(weatherList)
                                Log.e("asd",todayList.toString())
                            }else if (weatherList.dtTxt!!.substringData(11,16)=="00:00"){
                                nextDaysList.add(weatherList)
                            }
                        }
                        todayList.let { weatherTodayAdapter.differList.submitList(it) }
                        nextDaysList.let { weatherAdapter.differList.submitList(it) }

                        binding.tvCityName.text = forecastResponse.data.city!!.name
                        updateUI(todayList)
                        binding.loadingLottieAnim.makeInvisible()

                    }
                    is Resource.Error  -> toast(forecastResponse.message!!)
                    is Resource.Loading-> {
                        binding.loadingLottieAnim.makeVisible()
                        binding.loadingLottieAnim.playAnimation()
                    }
                }
            }
        }
    }
    override fun onQueryTextSubmit(query: String?): Boolean {

        query?.let { getWeatherForecastByCityName(it) }

        return true
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}
