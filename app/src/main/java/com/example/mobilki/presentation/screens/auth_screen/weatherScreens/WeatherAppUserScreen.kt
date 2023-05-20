package com.example.mobilki.presentation.screens.auth_screen.weatherScreens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobilki.weatherApi.WeatherApiClient
import com.example.mobilki.weatherApi.WeatherResponse
import com.example.mobilki.ui.theme.typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.mobilki.weatherApi.Result
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.graphics.BitmapFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.location.LocationServices
import java.net.URL
import java.time.*


@SuppressLint("DiscouragedApi", "UnrememberedMutableState", "CoroutineCreationDuringComposition",
    "SimpleDateFormat"
)
@Composable
fun WeatherAppUserScreen() {
    val weatherResponseState = remember { mutableStateOf<WeatherResponse?>(null) }
    val cityState = remember { mutableStateOf("") }
    val locationState = remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val PERMISSION_REQUEST_CODE = 1001
    val coroutineScope = rememberCoroutineScope()
    val hourlyForecastState = remember { mutableStateOf<List<WeatherResponse>>(emptyList()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Прогноз погоды",
            style = typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            var city by cityState
            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Введите город") },
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            )
            Button(
                onClick = {
                    weatherResponseState.value = null
                    hourlyForecastState.value = emptyList()

                    val weatherApiClient = WeatherApiClient("d87d2f8e4941f9a0c6dddf490d5780c8")

                    coroutineScope.launch {
                        val currentWeatherResult = withContext(Dispatchers.IO) {
                            weatherApiClient.getCurrentWeather(city)
                        }

                        when (currentWeatherResult) {
                            is Result.Success -> {
                                weatherResponseState.value = currentWeatherResult.data
                                val forecastResult = weatherApiClient.getHourlyForecast(city)

                                when (forecastResult) {
                                    is Result.Success -> {
                                        val forecastResponse = forecastResult.data
                                        println(forecastResponse)
                                        hourlyForecastState.value = forecastResponse.hourlyForecasts
                                        println(hourlyForecastState.value)

                                    }
                                    is Result.Error -> {
                                        Log.e(
                                            "WeatherAppUserScreen",
                                            "Ошибка при получении прогноза погоды на 24 часа: ${forecastResult.message}"
                                        )
                                    }
                                }
                            }
                            is Result.Error -> {
                                Log.e(
                                    "WeatherAppUserScreen",
                                    "Ошибка при получении погоды: ${currentWeatherResult.message}"
                                )
                            }
                            else -> {
                                Log.e(
                                    "WeatherAppUserScreen",
                                    "Unexpected result type: $currentWeatherResult"
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Прогноз",
                    style = typography.body1,
                    color = Color.White
                )
            }
        }

        Text("Или")

        Button(
            onClick = {
                weatherResponseState.value = null
                hourlyForecastState.value = emptyList()
                if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context)) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            locationState.value = location

                            location?.let { currentLocation ->
                                val weatherApiClient =
                                    WeatherApiClient("d87d2f8e4941f9a0c6dddf490d5780c8")

                                coroutineScope.launch {
                                    val weatherResult = withContext(Dispatchers.IO) {
                                        weatherApiClient.getCurrentWeatherByCoordinates(
                                            currentLocation.latitude,
                                            currentLocation.longitude
                                        )
                                    }

                                    when (weatherResult) {
                                        is Result.Success -> {
                                            weatherResponseState.value = weatherResult.data
                                        }
                                        is Result.Error -> {
                                            Log.e(
                                                "WeatherAppUserScreen",
                                                "Ошибка при получении погоды: ${weatherResult.message}"
                                            )
                                        }
                                        else -> {
                                            Log.e(
                                                "WeatherAppUserScreen",
                                                "Unexpected result type: $weatherResult"
                                            )
                                        }
                                    }

                                    val forecastResult = withContext(Dispatchers.IO) {
                                        weatherApiClient.getHourlyForecastByCoordinates(
                                            currentLocation.latitude,
                                            currentLocation.longitude
                                        )
                                    }

                                    when (forecastResult) {
                                        is Result.Success -> {
                                            hourlyForecastState.value =
                                                forecastResult.data.hourlyForecasts
                                        }
                                        is Result.Error -> {
                                            Log.e(
                                                "WeatherAppUserScreen",
                                                "Ошибка при получении прогноза погоды на 24 часа: ${forecastResult.message}"
                                            )
                                        }
                                        else -> {
                                            Log.e(
                                                "WeatherAppUserScreen",
                                                "Unexpected result type: $forecastResult"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        .addOnFailureListener { exception: Exception ->
                            Log.e(
                                "WeatherAppUserScreen",
                                "Ошибка при получении геопозиции: ${exception.message}"
                            )
                        }
                } else {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        PERMISSION_REQUEST_CODE,
                        context
                    )
                }
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Прогноз по текущему местоположению",
                style = typography.body1,
                color = Color.White
            )
        }


        weatherResponseState.value?.let { weatherResponse ->
            val temperatureInCelsius = weatherResponse.main.temp - 273.15
            val feelsLikeTemperatureInCelsius = weatherResponse.main.feels_like - 273.15

            val formattedTemperature = temperatureInCelsius.toInt().toString()
            val formattedFeelsLikeTemperature = feelsLikeTemperatureInCelsius.toInt().toString()

            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weatherResponse.name,
                    style = typography.h6,
                    modifier = Modifier.padding(bottom = 4.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    val iconCode = weatherResponse.weather.firstOrNull()?.icon
                    if (iconCode != null) {

                        val url = "https://openweathermap.org/img/w/$iconCode.png"

                        val imageBitmap = loadImage(url)

                        if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "Weather Icon",
                                modifier = Modifier
                                    .size(100.dp)
                            )
                        } else {
                            Text(text = "Image not found", style = typography.body2)
                        }

                    }
                }
                Column {

                    Text(
                        text = "$formattedTemperature°C",
                        style = typography.h4,
                        modifier = Modifier.padding(bottom = 4.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ощущается ${formattedFeelsLikeTemperature}°C",
                        style = typography.h6,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }


    hourlyForecastState.value?.let { hourlyForecasts ->
        val currentDateTime = LocalDateTime.now()
        val endDateTime = currentDateTime.plusHours(24)
        val filteredForecasts = hourlyForecasts.filter { forecast ->
            val forecastDateTime =
                Instant.ofEpochSecond(forecast.dt).atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            forecastDateTime in currentDateTime..endDateTime
        }

        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(filteredForecasts) { forecast ->
                val forecastTemperature = forecast.main.temp - 273.15
                val formattedForecastTemperature = forecastTemperature.toInt().toString()
                val forecastIconCode = forecast.weather.firstOrNull()?.icon
                val timestamp = forecast.dt
                val dateTime =
                    Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault())
                        .toLocalDateTime()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "$dateTime",
                        style = typography.body2,
                        modifier = Modifier.width(85.dp)
                    )

                    forecastIconCode?.let { iconCode ->
                        val url = "https://openweathermap.org/img/w/$iconCode.png"

                        val imageBitmap = loadImage(url)

                        if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "Weather Icon",
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(end = 16.dp)
                            )
                        } else {
                            Text(text = "Image not found", style = typography.body2)
                        }
                    }


                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Температура: $formattedForecastTemperature°C",
                            style = typography.body2
                        )
                        Text(
                            text = "Влажность: ${forecast.main.humidity}%",
                            style = typography.body2
                        )
                    }
                }
            }
        }
    }
}

private fun checkPermission(permission: String, context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

private fun requestPermission(permission: String, requestCode: Int, context: Context) {
    ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), requestCode)
}

@Composable
fun loadImage(url: String): ImageBitmap? {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val stream = URL(url).openStream()
                val bitmap = BitmapFactory.decodeStream(stream)
                imageBitmap = bitmap.asImageBitmap()
                //print("imgInfo$imageBitmap")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return imageBitmap
}





