package com.example.weatherfinish.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

private var service : WeatherService? = null

fun getService() : WeatherService? {
    if ( service == null )
        service = buildRetrofit()
    return service }

private fun  buildRetrofit(): WeatherService {     //делает запрос в интернет

    val service =
        Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttp()) //(2)
            .build()
            .create(WeatherService:: class.java)
    return service
}

private fun buildOkHttp(): OkHttpClient {  // функц позволяет прокидывать time out (2)
    return OkHttpClient.Builder()
        .connectTimeout(5,TimeUnit.SECONDS)  // первый time  работает на соедин серв //лимит на connect 5sec
        .readTimeout(30, TimeUnit.SECONDS)  // полученн данных, в течен 30 сек не придут данные выйдет ошибка
        .writeTimeout(30,TimeUnit.SECONDS) //отправка данных
        .build()
}
}