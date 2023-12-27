package com.ozanarik.mvvmweatherapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ozanarik.mvvmweatherapp.databinding.FragmentWeatherForecastBinding

class WeatherForecastFragment : Fragment() {

    private lateinit var binding: FragmentWeatherForecastBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentWeatherForecastBinding.inflate(inflater,container,false)


        return (binding.root)
    }


}