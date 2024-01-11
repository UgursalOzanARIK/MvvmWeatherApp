package com.ozanarik.mvvmweatherapp.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class DataStoreManager(context: Context) {


    private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("AppPrefs")
    private val dataStore = context.dataStore


    companion object{
        private val darkModeKey = booleanPreferencesKey("isDarkMode")
        private val latitudeKey = doublePreferencesKey("latitudeKey")
        private val longitudeKey = doublePreferencesKey("longitudeKey")
        private val searchedCityQuery = stringPreferencesKey("searchedKey")

    }

    suspend fun setDarkMode(isDarkMode:Boolean){
        dataStore.edit { prefs->
            prefs[darkModeKey] = isDarkMode
        }
    }
    fun getDarkModeKey():Flow<Boolean>{

        return dataStore.data.catch { e->
            if (e is Exception){
                emit(emptyPreferences())
            }else{
                throw e
            }
        }.map { prefs->
            val darkMode = prefs[darkModeKey]?:false
            darkMode
        }
    }


    suspend fun setLatLonKeys(latitude:Double,longitude:Double){
        dataStore.edit { prefs->
            prefs[latitudeKey] = latitude
            prefs[longitudeKey] = longitude
        }
    }

    fun getLocationLatLonKeys():Flow<Pair<Double,Double>>{

        return dataStore.data.map { prefs->
            val locationLatitude = prefs[latitudeKey]?:0.0
            val locationLongitude = prefs[longitudeKey]?:0.0

            Pair(locationLatitude,locationLongitude)
        }
    }

    suspend fun setSearchedCityQuery(query:String){
        dataStore.edit { prefs-> prefs[searchedCityQuery]= query }
    }
    fun getSearchedCityQuery():Flow<String>{
        return dataStore.data.map {prefs->
            val cityQuery = prefs[searchedCityQuery]?:""
            cityQuery
        }.flowOn(Dispatchers.IO)
    }


    suspend fun deleteSearchedCityQuery(){
        dataStore.edit { prefs->
            prefs.remove(searchedCityQuery)
            Log.e("asd","datastore deleted $searchedCityQuery")
        }
    }

}