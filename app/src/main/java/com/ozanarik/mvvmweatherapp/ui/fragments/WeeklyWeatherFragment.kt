package com.ozanarik.mvvmweatherapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ozanarik.mvvmweatherapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeeklyWeatherFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_weather, container, false)
    }

}