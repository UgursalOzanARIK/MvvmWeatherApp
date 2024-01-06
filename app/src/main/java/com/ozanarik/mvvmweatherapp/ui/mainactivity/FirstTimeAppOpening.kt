package com.ozanarik.mvvmweatherapp.ui.mainactivity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ozanarik.mvvmweatherapp.R
import com.ozanarik.mvvmweatherapp.databinding.ActivityFirstTimeAppOpeningBinding
import com.ozanarik.mvvmweatherapp.utils.Constants
import com.ozanarik.mvvmweatherapp.utils.LocationUtility
import com.ozanarik.mvvmweatherapp.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirstTimeAppOpening () : AppCompatActivity() {
    private lateinit var binding: ActivityFirstTimeAppOpeningBinding
    @Inject lateinit var context:Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirstTimeAppOpeningBinding.inflate(layoutInflater)

        binding.buttonGrantPermission.setOnClickListener {
            checkPermissions()

        }


        setContentView(binding.root)
    }



    private fun checkPermissions(){

        when(LocationUtility.areLocationServicesEnabled(this)){

            true->{
                requestPermission()
            }
            false->{

                val ad = AlertDialog.Builder(this).apply {


                    setTitle("Location services not enabled")
                    setMessage("You need to enable location services to use this app")
                    setPositiveButton("To the settings"){dialog,i->

                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                    setNegativeButton("Cancel"){dialog,i->
                        dialog.dismiss()
                    }

                    setCancelable(false)
                    create().show()
                }
            }
        }
    }

    private fun requestPermission(){

        val locationPair = LocationUtility.isGrantedLocationPermission(this)

        if (locationPair.first && locationPair.second){


            findNavController(R.id.navHostFragment).navigate(R.id.firstTimeAppOpeningToWeatherForecastFragment)


        }else{

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                binding.buttonGrantPermission.showSnackbar(
                    "Permission required to access location",
                    "Grant Permission")
                {requestLocationPermission()}
            }else{
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission(){

        ActivityCompat.requestPermissions(
            this,
            arrayOf (
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
                    )
    }
}