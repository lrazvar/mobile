package com.example.mobilki.weatherApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiClient(private val apiKey: String) {
    private val apiService: WeatherApiService


    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(WeatherApiService::class.java)
    }


    suspend fun getCurrentWeather(city: String): Result<WeatherResponse> {
        return try {
            val weatherResponse = apiService.getCurrentWeather(city, apiKey)
            Result.Success(weatherResponse)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getCurrentWeatherByCoordinates(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return try {
            val response = apiService.getCurrentWeatherByCoordinates(latitude, longitude, apiKey)
            if (response.isSuccessful) {
                val weatherResponse = response.body()
                if (weatherResponse != null) {
                    Result.Success(weatherResponse)
                } else {
                    Result.Error("Empty response body")
                }
            } else {
                Result.Error("Request failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getHourlyForecast(city: String): Result<ForecastResponse> {
        return try {
            val response = apiService.getHourlyForecast(city, apiKey)
            if (response.isSuccessful) {
                val forecastResponse = response.body()
                if (forecastResponse != null) {
                    Result.Success(forecastResponse)
                } else {
                    Result.Error("Empty response body")
                }
            } else {
                Result.Error("Request failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getHourlyForecastByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<ForecastResponse> {
        return try {
            val response = apiService.getHourlyForecastByCoordinates(latitude, longitude, apiKey)
            if (response.isSuccessful) {
                val forecastResponse = response.body()
                if (forecastResponse != null) {
                    Result.Success(forecastResponse)
                } else {
                    Result.Error("Empty response body")
                }
            } else {
                Result.Error("Request failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
