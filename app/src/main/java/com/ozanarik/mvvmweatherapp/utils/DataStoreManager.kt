package com.ozanarik.mvvmweatherapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    suspend fun setSearchedCityQuery(query:String){
        dataStore.edit { prefs-> prefs[searchedCityQuery]= query }
    }
    fun getSearchedCityQuery():Flow<String>{
        return dataStore.data.map {prefs->
            val cityQuery = prefs[searchedCityQuery]?:"Ankara"
            cityQuery
        }
    }



    suspend fun setLatLon(pair: Pair<Double,Double>){
        dataStore.edit { prefs->
            prefs[latitudeKey] = pair.first
            prefs[longitudeKey] = pair.second
        }
    }

    fun getLatitudeLongitudeKeys():Flow<Pair<Double,Double>> {

        return dataStore.data.map { prefs->

            val latKey = prefs[latitudeKey]?:0.0
            val lonKey = prefs[longitudeKey]?:0.0

            Pair(latKey,lonKey)
        }
    }
}