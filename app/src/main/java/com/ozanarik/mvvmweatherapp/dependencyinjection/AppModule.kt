package com.ozanarik.mvvmweatherapp.dependencyinjection

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.ozanarik.mvvmweatherapp.business.remote.WeatherApi
import com.ozanarik.mvvmweatherapp.business.repository.LocationRepository
import com.ozanarik.mvvmweatherapp.business.repository.WeatherForecastRepository
import com.ozanarik.mvvmweatherapp.utils.Constants.Companion.BASE_URL
import com.ozanarik.mvvmweatherapp.utils.DataStoreManager
import com.ozanarik.mvvmweatherapp.utils.LocationUtility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(weatherApi: WeatherApi):WeatherForecastRepository{
        return WeatherForecastRepository(weatherApi)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context:Context):Context{
        return context
    }
    @Provides
    @Singleton
    fun provideLocationRepository(@ApplicationContext context:Context,locationUtility: LocationUtility):LocationRepository{
        return LocationRepository(context = context, locationUtility = locationUtility )
    }
    @Provides
    @Singleton
    fun provideLocationUtility():LocationUtility{
        return LocationUtility()
    }

    @Provides
    @Singleton
    fun provideBaseUrl()=BASE_URL

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit):WeatherApi{
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient():OkHttpClient{

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context:Context):DataStoreManager{
        return DataStoreManager(context)
    }


}