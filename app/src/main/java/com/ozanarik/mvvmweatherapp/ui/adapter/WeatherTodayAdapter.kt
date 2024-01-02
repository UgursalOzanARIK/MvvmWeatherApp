package com.ozanarik.mvvmweatherapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.WeatherTodayItemListBinding
import com.ozanarik.mvvmweatherapp.utils.WeatherIconHelperClass

class WeatherTodayAdapter:RecyclerView.Adapter<WeatherTodayAdapter.WeatherTodayHolder>() {


    inner class WeatherTodayHolder(val binding: WeatherTodayItemListBinding):RecyclerView.ViewHolder(binding.root)


    private val diffUtil = object : DiffUtil.ItemCallback<WeatherList>(){
        override fun areItemsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem.pop == newItem.pop
        }

        override fun areContentsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem == newItem
        }
    }

    val differList = AsyncListDiffer(this,diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherTodayHolder {
        val layoutFrom = LayoutInflater.from(parent.context)
        val binding:WeatherTodayItemListBinding = WeatherTodayItemListBinding.inflate(layoutFrom,parent,false)
        return WeatherTodayHolder(binding)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeatherTodayHolder, position: Int) {
        val currentWeather = differList.currentList[position]

        holder.binding.apply {

            val timeToday = currentWeather.dtTxt!!.substring(11,16)
            val temp = currentWeather.main!!.temp!!.minus(272.15).toInt()


            tvNow.text = timeToday
            tvTempNow.text = "$temp °C"

            imageViewToday.setImageResource(getWeatherIcon(currentWeather.weather[0].icon!!))


        }

    }

    override fun getItemCount(): Int {
        return differList.currentList.size

    }


    private fun getWeatherIcon(weatherIcon:String):Int{

        return when(weatherIcon){

            WeatherIconHelperClass.CLEAR_SKY.weatherIconString-> R.drawable.clearsky
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