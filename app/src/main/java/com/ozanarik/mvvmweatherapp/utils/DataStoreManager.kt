package com.ozanarik.mvvmweatherapp.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager(context:Context) {

    private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("AppPrefs")
    val dataStore = context.dataStore



    companion object{
        private val darkModeKey = booleanPreferencesKey("isDarkMode")
        private val locationLatitudeKey = doublePreferencesKey("locationLatitudeKey")
        private val locationLongitudeKey = doublePreferencesKey("locationLongitudeKey")
    }



    suspend fun setLocationLatitudeLongitudeKeys(latitude:Double, longitude:Double){

        dataStore.edit { appPrefs->

            appPrefs[locationLatitudeKey] = latitude
            appPrefs[locationLongitudeKey] = longitude
        }
    }

    suspend fun getLocationLatitudeLongitudeKeys(): Pair<Double,Double>{


        val prefs = dataStore.data.first()
        val latitude = prefs[locationLatitudeKey]?:0.0
        val longitude = prefs[locationLongitudeKey]?:0.0
        Log.e("asd","${latitude.toString()} from datastore")
        Log.e("asd","${longitude.toString()} from datastore")

        return Pair(latitude,longitude)

    }


    suspend fun setDarkMode(isDarkMode:Boolean){

        dataStore.edit { appPrefs->
            appPrefs[darkModeKey] = isDarkMode
        }
    }

    fun getDarkMode():Flow<Boolean>{

        return dataStore.data.catch { e->
                if (e is Exception){
                    emit(emptyPreferences())
                }else{
                    throw e
                }
        }.map { appPrefs->
            val darkMode = appPrefs[darkModeKey]?:false
            darkMode
        }
    }
}