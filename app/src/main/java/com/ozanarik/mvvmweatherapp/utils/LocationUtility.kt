package com.ozanarik.mvvmweatherapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat

class LocationUtility  {



    companion object{
        fun areLocationServicesEnabled(context:Context):Boolean{

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        fun isGrantedLocationPermission(context:Context): Pair<Boolean, Boolean> {

            val fineLocationPermission = ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
            val coarseLocationPermission = ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED


            return Pair(fineLocationPermission,coarseLocationPermission)
        }


    }

}