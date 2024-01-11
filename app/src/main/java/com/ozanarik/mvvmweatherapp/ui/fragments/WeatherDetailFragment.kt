package com.ozanarik.mvvmweatherapp.ui.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherDetailBinding
import com.ozanarik.mvvmweatherapp.ui.viewmodel.WeatherViewModel
import com.ozanarik.mvvmweatherapp.utils.Constants.Companion.OBJECT_ANIMATION_DURATION
import com.ozanarik.mvvmweatherapp.utils.ObjectAnimationManager
import com.ozanarik.mvvmweatherapp.utils.capitalizeWords
import com.ozanarik.mvvmweatherapp.utils.kelvinToCelsius
import com.ozanarik.mvvmweatherapp.utils.substringData
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherDetailFragment : Fragment() {
    private lateinit var binding: FragmentWeatherDetailBinding
    private lateinit var weatherViewModel: WeatherViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentWeatherDetailBinding.inflate(inflater,container,false)
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        initDetailedWeatherData()
        animateWeatherDetailCardView()

        return binding.root
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun initDetailedWeatherData(){
        val args:WeatherDetailFragmentArgs by navArgs()

        val weatherData = args.weatherList


        binding.textViewDate.text = weatherData.dtTxt!!.substringData(0,11)
        binding.textViewTemp.text = "${ weatherData.main!!.temp!!.kelvinToCelsius()}°C"
        binding.textViewMin.text = "Max : ${weatherData.main!!.tempMin!!.kelvinToCelsius()}°C"
        binding.textViewMax.text = " Min : ${weatherData.main!!.tempMax!!.kelvinToCelsius()}°C"
        binding.textViewDescription.text = weatherData.weather[0].description.capitalizeWords()
        binding.textViewDayOfTheWeek.text = getDayOfTheWeek(weatherData.dtTxt!!.substring(0,10))


        binding.lottieAnimationView.setAnimation(weatherViewModel.getWeatherAnimForLottie(weatherData.weather[0].icon!!))

    }

    private fun animateWeatherDetailCardView(){
        val cardAnimation = ObjectAnimator.ofFloat(binding.cardViewWeatherDetailData,"alpha",0.0f,1.0f)
        val lottieAnimation = ObjectAnimator.ofFloat(binding.lottieAnimationView,"alpha",0.0f,1.0f)
        val cardYTranslationFromDownside = ObjectAnimator.ofFloat(binding.cardViewWeatherDetailData,"translationY",0.0f,-100.0f)

        val multiAnim = AnimatorSet().apply {

            duration = 650L

            playTogether(cardYTranslationFromDownside,cardAnimation,lottieAnimation)
        }
        multiAnim.start()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfTheWeek(date:String):String{

        date.trim()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val week = LocalDate.parse(date,dateFormat)

        return week.dayOfWeek.toString()

    }

}