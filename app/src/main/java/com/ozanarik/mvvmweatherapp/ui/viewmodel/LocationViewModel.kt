package com.ozanarik.mvvmweatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ozanarik.mvvmweatherapp.business.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val locationRepository: LocationRepository) :ViewModel() {

    private val _locationServicesPermission:MutableStateFlow<Boolean> = MutableStateFlow(false)
    val locationServicesPermission:StateFlow<Boolean> = _locationServicesPermission

    private val _isGrantedLocationPermission:MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isGrantedLocationPermission:StateFlow<Boolean> = _isGrantedLocationPermission
    fun areLocationServicesEnabled(){

        when(locationRepository.areLocationServicesEnabled()){
            true->{
                _locationServicesPermission.value = true
            }
            false->{
                _locationServicesPermission.value = false
            }
        }
    }

    fun isLocationPermissionGranted(){
        val isGrantedLocationPermission = locationRepository.isLocationPermissionGranted()

        val fineLocationPermission = isGrantedLocationPermission.first
        val coarseLocationPermission = isGrantedLocationPermission.second
        _isGrantedLocationPermission.value = fineLocationPermission && coarseLocationPermission
    }

}