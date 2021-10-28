package com.bharat.weatherapp.repository

import com.bharat.weatherapp.apis.RetrofitInstance

class WeatherReportRepo {

    suspend fun getLocationReport(lat:Double,lon:Double) =
        RetrofitInstance.instance.getReport(lat, lon)

    suspend fun getCityReport(city: String) =
        RetrofitInstance.instance.getCityReport(city)

}