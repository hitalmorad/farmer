package com.example.farmer.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherApi {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
