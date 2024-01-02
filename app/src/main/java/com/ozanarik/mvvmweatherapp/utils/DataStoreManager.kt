package com.ozanarik.mvvmweatherapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreManager(context:Context) {

    private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("AppPrefs")
    val dataStore = context.dataStore



    companion object{
        private val darkModeKey = booleanPreferencesKey("isDarkMode")
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