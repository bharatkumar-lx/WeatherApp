package com.bharat.weatherapp.apis

import androidx.room.TypeConverters
import com.bharat.weatherapp.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
//
//@TypeConverters(
//    Converter::class
//)
class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            //HttpLoggingInterceptor help to log the errors and inspect the response
            val logger = HttpLoggingInterceptor()
            logger.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logger).build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val instance by lazy {
            retrofit.create(WeatherApi::class.java)
        }

    }

}