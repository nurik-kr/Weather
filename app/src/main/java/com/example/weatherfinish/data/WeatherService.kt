package com.example.weatherfinish.data

import com.example.weatherfinish.model.forecast.ForecastModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/onecall")
    suspend fun getForecastByCoordinates(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String,
        @Query("appid") appId: String,
        @Query("units") units: String
    ): ForecastModel

}