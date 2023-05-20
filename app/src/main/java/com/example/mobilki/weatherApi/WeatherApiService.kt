package com.example.mobilki.weatherApi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String
    ): WeatherResponse

    @GET("weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getHourlyForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
    ): Response<ForecastResponse>

    @GET("forecast")
    suspend fun getHourlyForecastByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>
}


