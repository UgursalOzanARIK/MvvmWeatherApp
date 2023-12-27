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
import com.ozanarik.mvvmweatherapp.WeatherList
import com.ozanarik.mvvmweatherapp.databinding.WeatherItemListBinding
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WeatherAdapter:RecyclerView.Adapter<WeatherAdapter.WeatherHolder>() {



    inner class WeatherHolder(val binding: WeatherItemListBinding):RecyclerView.ViewHolder(binding.root)


    private val diffUtilCallBack = object : DiffUtil.ItemCallback<WeatherList>(){


        override fun areItemsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem.pop == newItem.pop
        }

        override fun areContentsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem == newItem
        }


    }

    private val differList = AsyncListDiffer(this,diffUtilCallBack)

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

            val temp = currentWeather.main!!.temp!!.minus(272.15)
            val timeToday = currentWeather.dtTxt!!.substring(11,16)

            binding.tvTemp.text = "$temp °C"
            binding.tvDate.text = timeToday

        }
    }
    override fun getItemCount(): Int {

        return differList.currentList.size
    }
}