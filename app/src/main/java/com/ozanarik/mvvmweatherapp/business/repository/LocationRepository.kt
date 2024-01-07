package com.ozanarik.mvvmweatherapp.business.repository

import android.app.Activity
import android.content.Context
import com.ozanarik.mvvmweatherapp.utils.LocationUtility
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationRepository @Inject constructor(@ApplicationContext private val context:Context ,private val locationUtility: LocationUtility) {


    fun areLocationServicesEnabled() = locationUtility.areLocationServicesEnabled(context)

    fun isLocationPermissionGranted() = locationUtility.isLocationPermissionGranted(context)






}