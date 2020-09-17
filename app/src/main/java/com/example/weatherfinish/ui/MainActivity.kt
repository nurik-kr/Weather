package com.example.weatherfinish.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherfinish.R
import com.example.weatherfinish.data.RetrofitBuilder
import com.example.weatherfinish.model.forecast.ForecastModel
import com.example.weatherfinish.model.forecast.ForecastTemp
import com.example.weatherfinish.utils.ConnectionUtils
import com.example.weatherfinish.utils.PermissionUtils
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Double.max
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val adapter by lazy { RvAdapter() } //lazy отработает когда его кто то вызовит

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        formatDate()
        setupRecycler()
        showSnackBar()
    }

    private fun setupRecycler() {
        receycler.adapter = adapter
    }

    private fun formatDate() { // функция отвечает за дату
        val sfdDay = SimpleDateFormat("d", Locale.getDefault())
        val date = Date()
        val day = sfdDay.format(date)
        DateSecond.text = day
        val sfdMonth = SimpleDateFormat("MMMM\nyyyy", Locale.getDefault())
        val month = sfdMonth.format(date)
        Date.text = month
    }

    fun formatDate(date: Int?): String {        //для sunset sunrise чтоб был в часовом формате
        val newData = date?.toLong() ?: 0
        return SimpleDateFormat("H:mm", Locale.getDefault()).format(Date(newData * 1000))
    }

    private fun showSnackBar() {
        val isHasNetwork = ConnectionUtils.isNetworkAvailable(this)
        if (!isHasNetwork) {
            Snackbar.make(parentLayout, "Обновить", Snackbar.LENGTH_INDEFINITE)
                .setAction("Обновить") {
                    if (! ConnectionUtils.isNetworkAvailable(this)) {
                        showSnackBar()
                    } else {
                        checkPermissions()
                    }
                }.show()
        }
    }

    private fun loadByLocation(location: Location) {    // запрос в интернет c помощбю корутин
        GlobalScope.launch {        //доп поток
            runCatching {
                val result = RetrofitBuilder.getService()?.getForecastByCoordinates(
                    location.latitude.toString(), location.longitude.toString(), "minutely",
                    getString(R.string.api_key), "metric")
                loadWeather(result)
            }.onFailure {
                Log.e("NETWORK", "NO DATA")
            }
        }
    }

    private fun loadWeather(result: ForecastModel?) {
        runOnUiThread {
            adapter.update(result?.daily)
            LocationSecond.text = result?.timezone
            numberOne.text = getString(R.string.degreeformat, result?.current?.temp?.toInt().toString())
            numberTWo.text = getString(R.string.degreeformat, result?.current?.temp?.toInt().toString())
            Sw.text = getString(R.string.windformat, result?.current?.wind_speed?.toInt().toString())
            Percent.text = getString(R.string.percentformat, result?.current?.humidity)
            mb.text = getString(R.string.pressureformat, result?.current?.pressure.toString())
            PercentSecond.text = getString(R.string.percentformat, result?.current?.clouds)
            hour.text = formatDate(result?.current?.sunrise)
            hourSecond.text = formatDate(result?.current?.sunset)

            LittleCloud.text = result?.current?.weather?.first()?.description
            val image = result?.current?.weather?.first()?.icon
            Picasso.get().load("http://openweathermap.org/img/w/$image.png").into(cloudmain)
        }
    }

    private fun checkPermissions() {
        if (PermissionUtils.checkLocationPermission(this))
            loadLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtils.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                loadLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadLocation() {
        val fpc = LocationServices.getFusedLocationProviderClient(applicationContext)

            fpc.lastLocation.addOnSuccessListener {
                loadByLocation(it)
            }.addOnFailureListener {
                val location = Location("")
                location.latitude = 42.78
                location.longitude = 75.69
                loadByLocation(location)
            }
    }
}
