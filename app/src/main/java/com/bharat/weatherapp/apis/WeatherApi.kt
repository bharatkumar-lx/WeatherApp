package com.bharat.weatherapp.apis

import com.bharat.weatherapp.model.Weather
import com.bharat.weatherapp.model.WeatherReport
import com.bharat.weatherapp.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCityReport(
        @Query("q")
        city: String,
        @Query("appid")
        api: String = API_KEY
    ) : Response<WeatherReport>

    @GET("data/2.5/weather")
    suspend fun getReport(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double,
        @Query("appid")
        api: String = API_KEY,
    ) : Response<WeatherReport>

}