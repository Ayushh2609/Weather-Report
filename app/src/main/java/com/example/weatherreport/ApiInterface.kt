package com.example.weatherreport

import android.provider.CallLog.Calls
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather")
    fun getWeatherData(
        @Query("q") city : String,
        @Query("appid") appid : String,
        @Query("units") units : String
    ) : Call<WeatherApp>

    data class CitySuggestion(
        val name : String,
        val state : String?,
        val country : String,
        val lon :String,
        val lat : String,
    )
}