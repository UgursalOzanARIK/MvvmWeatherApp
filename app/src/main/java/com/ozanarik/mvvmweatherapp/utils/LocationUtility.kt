package com.ozanarik.mvvmweatherapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.ozanarik.mvvmweatherapp.utils.Constants.Companion.LOCATION_PERMISSION_REQUEST_CODE

class LocationUtility  (
    private val fineLocationPermission:String = Manifest.permission.ACCESS_FINE_LOCATION,
    private val coarseLocationPermission:String = Manifest.permission.ACCESS_COARSE_LOCATION)  {

        fun areLocationServicesEnabled(context: Context): Boolean {

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        }
        fun isLocationPermissionGranted(context: Context):Pair<Boolean,Boolean>{


            val fineLocationPermission = ContextCompat.checkSelfPermission(context,fineLocationPermission)==PackageManager.PERMISSION_GRANTED
            val coarseLocationPermission = ContextCompat.checkSelfPermission(context,coarseLocationPermission)==PackageManager.PERMISSION_GRANTED

            return Pair(fineLocationPermission,coarseLocationPermission)
        }

        fun requestPermissions(activity:Activity){

            ActivityCompat.requestPermissions(activity, arrayOf(
                fineLocationPermission,
                coarseLocationPermission),
                LOCATION_PERMISSION_REQUEST_CODE)
        }

    fun promptToEnableLocationService(context: Context){
        val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(locationSettingsIntent)
    }






}