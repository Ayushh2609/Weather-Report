package com.example.weatherreport

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApiService {
    @GET("geo/1.0/direct")
    fun getCitySuggestions(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Call<List<CitySuggestion>>
}