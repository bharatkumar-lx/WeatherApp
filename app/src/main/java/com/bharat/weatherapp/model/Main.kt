package com.bharat.weatherapp.model

data class Main(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    var temp: Double,
    val temp_max: Double,
    val temp_min: Double
)