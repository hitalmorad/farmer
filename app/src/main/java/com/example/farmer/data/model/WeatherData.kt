package com.example.farmer.data.model

data class WeatherData(
    val cityName: String,
    val temperature: Float,
    val humidity: Int,
    val pressure: Int,
    val weatherDescription: String,
    val weatherIcon: String
)
