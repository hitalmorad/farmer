package com.example.farmer.data.network

import com.example.farmer.data.model.WeatherData
import com.example.farmer.data.network.WeatherResponse
import com.example.farmer.data.network.WeatherApiService

class WeatherDataRepository(private val weatherApiService: WeatherApiService) {


    suspend fun getWeatherData(cityName: String, apiKey: String): WeatherData? {
        try {
            val response = weatherApiService.getWeather(cityName, apiKey)
            return mapWeatherResponseToWeatherData(response)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun mapWeatherResponseToWeatherData(response: WeatherResponse): WeatherData {
        return WeatherData(
            cityName = response.name,
            temperature = response.main.temp,
            humidity = response.main.humidity,
            pressure = response.main.pressure,
            weatherDescription = response.weather.firstOrNull()?.description ?: "Unknown",
            weatherIcon = response.weather.firstOrNull()?.icon ?: ""
        )
    }
}
