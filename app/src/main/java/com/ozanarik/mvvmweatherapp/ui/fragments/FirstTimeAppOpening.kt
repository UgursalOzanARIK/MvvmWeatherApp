package com.ozanarik.mvvmweatherapp.ui.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.databinding.FragmentFirstTimeAppOpeningBinding
import com.ozanarik.mvvmweatherapp.ui.viewmodel.LocationViewModel
import com.ozanarik.mvvmweatherapp.utils.Constants.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.ozanarik.mvvmweatherapp.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FirstTimeAppOpening : Fragment() {
    private lateinit var binding: FragmentFirstTimeAppOpeningBinding
    private lateinit var locationViewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentFirstTimeAppOpeningBinding.inflate(inflater,container,false)




        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]


        binding.buttonGrantPerms.setOnClickListener {


            locationViewModel.areLocationServicesEnabled()
            viewLifecycleOwner.lifecycleScope.launch {

                locationViewModel.locationServicesPermission.collect{locationServicePerm->

                    when(locationServicePerm){
                        true->{
                            Log.e("asd","loca services ${locationServicePerm.toString()}")
                            getLocationPermission()


                        }
                        false->{

                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)

                        }
                    }
                }
            }
        }


        return binding.root

    }

    private fun getLocationPermission(){
        locationViewModel.isLocationPermissionGranted()
        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.isGrantedLocationPermission.collect{isGrantedLocationPerm->
                when(isGrantedLocationPerm){
                    true->{

                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION)){
                            showSnackbar("Permission needed to access location","Grant Permission"){
                                requestLocationPermission()
                            }
                        }else{
                            requestLocationPermission()
                        }
                        Log.e("asd","granted  ${ isGrantedLocationPerm.toString()}")
                    }
                    false->{

                        findNavController().navigate(R.id.action_firstTimeAppOpening_to_weatherForecastFragment)

                    }
                }
            }
        }
    }

    private fun requestLocationPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE)
    }

}