package com.bharat.weatherapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.bharat.weatherapp.databinding.ActivityMainBinding
import com.bharat.weatherapp.repository.WeatherReportRepo
import com.bharat.weatherapp.util.Resource
import com.bharat.weatherapp.viewModel.WeatherViewModel
import com.bharat.weatherapp.viewModel.WeatherViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel : WeatherViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val weatherViewModelProvider = WeatherViewModelProvider(application,WeatherReportRepo())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation(CancellationTokenSource().token)
        viewModel = ViewModelProvider(this,weatherViewModelProvider).get(WeatherViewModel::class.java)

        onViewModelChange()
        search()
    }

    private fun onViewModelChange(){
        viewModel.weatherReport.observe(this,{response->
            run {
                when (response) {
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        response.data?.let {
                            binding.cityView.text = it.name +" "
                            binding.tempView.text = String.format("%.2f °C",(it.main?.temp ?: 273.0) -273.0)
                            binding.descriptionView.text = String.format("%s",it.weather?.get(0)?.description)
                            binding.windView.text = String.format("%s Km/h",it.wind?.speed)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(this,"${response.message}",Toast.LENGTH_LONG).show()
                    }
                    is Resource.Loading ->{
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(token: CancellationToken){
        if(hasLocationPermission()){
            fusedLocationClient
                .getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER,token)
                .addOnSuccessListener {
                    Log.d("Location",it.latitude.toString())
//                    viewModel.getWeatherReport("london")
                viewModel.getLocationReport(it.latitude,it.longitude)
                }.addOnFailureListener{
                    Log.d("Location",it.message!!)
                Toast.makeText(this,"Error in location "+it.message,Toast.LENGTH_LONG).show()
            }
        }else{
            requestPermission()
        }

    }



    private fun hasLocationPermission() = ActivityCompat
        .checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0){
//            Snackbar.make(binding.root,"Permission not granted",Snackbar.LENGTH_LONG).show()
            getLocation(CancellationTokenSource().token)
        }
    }


    private fun search(){
        var job : Job? = null
        binding.searchView.addTextChangedListener{ editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(1000L)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.getWeatherReport(editable.toString())
                    }
                }
            }
        }
    }





}