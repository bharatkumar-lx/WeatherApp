package com.bharat.weatherapp.viewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bharat.weatherapp.repository.WeatherReportRepo

class WeatherViewModelProvider(
    val app: Application,
    private val weatherReportRepo: WeatherReportRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherViewModel(app,weatherReportRepo) as T
    }
}