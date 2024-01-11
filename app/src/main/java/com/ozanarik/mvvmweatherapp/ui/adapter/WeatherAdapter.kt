package com.ozanarik.mvvmweatherapp.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ozanarik.mvvmweatherapp.Forecast
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.WeatherItemListBinding
import com.ozanarik.mvvmweatherapp.utils.WeatherIconHelperClass
import com.ozanarik.mvvmweatherapp.utils.kelvinToCelsius

class WeatherAdapter(private val clickListener: OnItemClickListener):RecyclerView.Adapter<WeatherAdapter.WeatherHolder>(){



    inner class WeatherHolder(val binding: WeatherItemListBinding):RecyclerView.ViewHolder(binding.root)


    private val diffUtilCallBack = object : DiffUtil.ItemCallback<WeatherList>(){
        override fun areItemsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem.pop == newItem.pop
        }
        override fun areContentsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem == newItem
        }
    }

    val differList = AsyncListDiffer(this,diffUtilCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        val layoutFrom = LayoutInflater.from(parent.context)
        val binding:WeatherItemListBinding = WeatherItemListBinding.inflate(layoutFrom,parent,false)
        return WeatherHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {

        val currentWeather = differList.currentList[position]

        holder.apply {

            val temp = currentWeather.main!!.temp!!.kelvinToCelsius()
            val timeNextDays = currentWeather.dtTxt!!.substring(0,11)

            binding.tvTemp.text = "$temp °C"
            binding.tvDate.text = timeNextDays
            binding.tvMin.text = "Min ${currentWeather.main!!.tempMin?.kelvinToCelsius().toString()}°C "
            binding.tvMax.text = "Max ${currentWeather.main!!.tempMax?.kelvinToCelsius().toString()}°C"

            currentWeather.weather[0].icon?.let { getWeatherIcon(it) }?.let { binding.imageViewIcon.setImageResource(it) }

            itemView.setOnClickListener {
                clickListener.onItemClicked(currentWeather)
            }

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
    override fun getItemCount(): Int {

        return differList.currentList.size
    }
    interface OnItemClickListener{
        fun onItemClicked(weatherData:WeatherList)
    }

}

