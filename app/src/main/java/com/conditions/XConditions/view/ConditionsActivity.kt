package com.conditions.XConditions.view


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.XConditions.R
import kotlinx.android.synthetic.main.activity_conditions.*
import com.conditions.XConditions.model.WeatherApi
import com.conditions.XConditions.model.WeatherDataModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt


class ConditionsActivity : AppCompatActivity() {


    companion object {
        private const val MIN_TIME: Long = 5000
        private const val MIN_DISTANCE = 1000f
        private const val REQUEST_CODE = 123
        private const val BASE_URL = "https://api.openweathermap.org/"
        private const val APP_ID = "d9abacb346617d1b58974df664c01ee7"
    }

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var latitude: String
    private lateinit var longitude: String

    private var LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER

    private var adapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conditions)


        // Setting tabLayout and adapter
        adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        swipeRefresh.setOnRefreshListener  {
            getLocationCoordinates()
            swipeRefresh.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()

        isGpsEnabled()
        getLocationCoordinates()

    }

    private fun fetchFromApi() {
        val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


        val service = api.create(WeatherApi::class.java)
        val call = service.getCurrentWeather(latitude, longitude, APP_ID)
        call.enqueue(object : Callback<WeatherDataModel> {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<WeatherDataModel>, response: Response<WeatherDataModel>) {

                instructionsTxt.visibility = View.VISIBLE
                loading.visibility = View.GONE
                val weatherResponse = response.body()
                val cityName = weatherResponse?.name
                val windSpeed = weatherResponse?.wind?.speed
                val windDirection = weatherResponse?.wind?.deg
                val temp = weatherResponse?.main?.temp
                val feelsLikeTemp = weatherResponse?.main?.feelsLike
                val airHumidity = weatherResponse?.main?.humidity
                val airPressure = weatherResponse?.main?.pressure
                val cloudLayer = weatherResponse?.clouds?.all
                prepareUI(feelsLikeTemp, temp, airHumidity, airPressure, cloudLayer, windSpeed, windDirection, cityName!!)

            }

            override fun onFailure(call: Call<WeatherDataModel>, t: Throwable) {
                Toast.makeText(applicationContext, "Something went wrong, check your internet connection.", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })
    }


    private fun getLocationCoordinates() {

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {

                if (location != null) {
                    longitude = location.longitude.toString()
                    latitude = location.latitude.toString()
                }
                fetchFromApi()

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            return
        }
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener)
    }


    private fun isGpsEnabled() {
        val service = (getSystemService(LOCATION_SERVICE) as LocationManager)
        val enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!enabled) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima", "OnPermissionResult() Permission Granted")
                getLocationCoordinates()
            } else {
                Log.d("Clima", "OnPermissionResult() Permission Denied")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareUI(feelsLike: Float?, temperature: Float?, humidity: Float?, pressure: Float?, clouds: Float?, wind: Float?, windDirection: Float?, cityName: String) {

        val feelsLike = (feelsLike!! - 273.15).roundToInt()
        val temperature = (temperature!! - 273.15).roundToInt()
        val humidity = humidity?.roundToInt()
        val pressure = pressure?.roundToInt()
        val clouds = clouds?.roundToInt()
        val wind = wind?.roundToInt()
        val windDirection = windDirection?.let { convertWindDirection(it) }

        updateUI(feelsLike.toString(), temperature.toString(), humidity.toString(), pressure.toString(), clouds.toString(), wind.toString(), windDirection!!, cityName)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUI(feelsLike: String, temperature: String, humidity: String, pressure: String, clouds: String, wind: String, windDirection: String, cityName: String) {

        val dayType = DayType(wind.toInt(), humidity.toInt(), pressure.toInt(), clouds.toInt())
        adapter?.updateFragments(dayType)
        dateTxt.text = getDate()
        nameTxt.text = cityName
        feelsLikeTxt.text = "Feels like $feelsLike°c"
        tempText.text = "$temperature°c"
        humidityPTxt.text = "$humidity%"
        pressurePTxt.text = "$pressure hPa"
        cloudsPTxt.text = "$clouds%"
        windPTxt.text = "$wind m/s $windDirection"
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(): String {
        val currentDateTime = LocalDate.now()
            return currentDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        }

    private fun convertWindDirection(directionInDegrees: Float): String {

        val cardinalDirection: String

        if ((directionInDegrees >= 348.75) && (directionInDegrees <= 360) || (directionInDegrees >= 0) && (directionInDegrees <= 11.25)) {
            cardinalDirection = "N"
        } else if ((directionInDegrees >= 11.25) && (directionInDegrees <= 33.75)) {
            cardinalDirection = "NNE"
        } else if ((directionInDegrees >= 33.75) && (directionInDegrees <= 56.25)) {
            cardinalDirection = "NE"
        } else if ((directionInDegrees >= 56.25) && (directionInDegrees <= 78.75)) {
            cardinalDirection = "ENE"
        } else if ((directionInDegrees >= 78.75) && (directionInDegrees <= 101.25)) {
            cardinalDirection = "E"
        } else if ((directionInDegrees >= 101.25) && (directionInDegrees <= 123.75)) {
            cardinalDirection = "ESE"
        } else if ((directionInDegrees >= 123.75) && (directionInDegrees <= 146.25)) {
            cardinalDirection = "SE"
        } else if ((directionInDegrees >= 146.25) && (directionInDegrees <= 168.75)) {
            cardinalDirection = "SSE"
        } else if ((directionInDegrees >= 168.75) && (directionInDegrees <= 191.25)) {
            cardinalDirection = "S"
        } else if ((directionInDegrees >= 191.25) && (directionInDegrees <= 213.75)) {
            cardinalDirection = "SSW"
        } else if ((directionInDegrees >= 213.75) && (directionInDegrees <= 236.25)) {
            cardinalDirection = "SW"
        } else if ((directionInDegrees >= 236.25) && (directionInDegrees <= 258.75)) {
            cardinalDirection = "WSW"
        } else if ((directionInDegrees >= 258.75) && (directionInDegrees <= 281.25)) {
            cardinalDirection = "W"
        } else if ((directionInDegrees >= 281.25) && (directionInDegrees <= 303.75)) {
            cardinalDirection = "WNW"
        } else if ((directionInDegrees >= 303.75) && (directionInDegrees <= 326.25)) {
            cardinalDirection = "NW"
        } else if ((directionInDegrees >= 326.25) && (directionInDegrees <= 348.75)) {
            cardinalDirection = "NNW"
        } else {
            cardinalDirection = "?*"
        }

        return cardinalDirection
    }

    override fun onBackPressed() {
        finish()
    }

}