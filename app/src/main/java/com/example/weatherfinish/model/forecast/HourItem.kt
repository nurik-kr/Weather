package com.example.weatherfinish.model.forecast

data class HourItem(
    val dt: Int,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val pop: Double,
    val weather: List<WeatherItem>
)