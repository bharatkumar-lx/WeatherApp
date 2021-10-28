package com.bharat.weatherapp.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharat.weatherapp.model.Weather
import com.bharat.weatherapp.model.WeatherReport
import com.bharat.weatherapp.repository.WeatherReportRepo
import com.bharat.weatherapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(
    val app: Application,
    private val weatherReportRepo: WeatherReportRepo
) : ViewModel() {
    init {
//        getLocationReport(35.0,139.0)
    }

    var weatherReport: MutableLiveData<Resource<WeatherReport>> = MutableLiveData()

    fun getWeatherReport(city: String) = viewModelScope.launch {
        weatherReport.postValue(Resource.Loading())
        if(hasInternetConnection()){
            val response = handleResponse(weatherReportRepo.getCityReport(city))
            weatherReport.postValue(response)
        }else{
            weatherReport.postValue(Resource.Error("Error No Internet"))
        }
    }


    fun getLocationReport(lat: Double, lon: Double) = viewModelScope.launch {
        weatherReport.postValue(Resource.Loading())
        if(hasInternetConnection()){
            val response = handleResponse(weatherReportRepo.getLocationReport(lat,lon))
            weatherReport.postValue(response)
        }else{
            weatherReport.postValue(Resource.Error("Error No Internet"))
        }


    }

    private fun handleResponse(response: Response<WeatherReport>): Resource<WeatherReport> {
            if (response.isSuccessful) {
                response.body()?.let {
                    return Resource.Success(it)
                }
            }
        return Resource.Error(" Error ${response.message()}")
}

    private fun hasInternetConnection(): Boolean{
        val connectivityManager = app.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) ->true
                capabilities.hasTransport(TRANSPORT_CELLULAR) ->true
                capabilities.hasTransport(TRANSPORT_ETHERNET) ->true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else ->false
                }
            }
        }
        return false
    }










}