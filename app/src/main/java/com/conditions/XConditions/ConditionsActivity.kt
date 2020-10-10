package com.conditions.XConditions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.example.XConditions.R
import com.google.android.material.tabs.TabLayout
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class ConditionsActivity : AppCompatActivity() {
    //Declare constants here:
    var APP_ID = "d9abacb346617d1b58974df664c01ee7"

    // Constants:
    private val REQUEST_CODE = 123
    private val WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather"

    // Time between location updates (5000 milliseconds or 5 seconds)
    private val MIN_TIME: Long = 5000

    // Distance between location updates (1000m or 1km)
    private val MIN_DISTANCE = 1000f

    // TODO: Set LOCATION_PROVIDER here:
    private var LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER

    //Declare member variables here
    private var temperature: TextView? = null
    private var name: TextView? = null
    private var date: TextView? = null
    private var feelsLike: TextView? = null
    private var wind: TextView? = null
    private var pressure: TextView? = null
    private var humidity: TextView? = null
    private var cloudCover: TextView? = null
    private var viewPager: ViewPager? = null
    private var descriptionTxt: TextView? = null
    private var adapter: ViewPagerAdapter? = null
    private var tabLayoutMode: TabLayout? = null

    //Delcare location manager and location listener:
    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null

    @SuppressLint("ClickableViewAccessibility", "WrongViewCast", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conditions)


        // Cast elements from layout here:
        temperature = findViewById(R.id.tempText)
        name = findViewById(R.id.nameTxt)
        date = findViewById(R.id.dateTxt)
        feelsLike = findViewById(R.id.feelsLikeTxt)
        wind = findViewById(R.id.windPTxt)
        pressure = findViewById(R.id.pressurePTxt)
        humidity = findViewById(R.id.humidityPTxt)
        cloudCover = findViewById(R.id.cloudsPTxt)
        viewPager = findViewById(R.id.viewPager)
        tabLayoutMode = findViewById(R.id.tabLayout)
        descriptionTxt = findViewById(R.id.instructionsTxt)


// Setting tabLayout and adapter
        adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager?.adapter = adapter
        tabLayoutMode?.setupWithViewPager(viewPager)
    }

    public override fun onResume() {
        super.onResume()
        isGpsEnabled
        weatherForCurrentLocation
    }

    private fun doNetworking(params: RequestParams) {
        val client = AsyncHttpClient()
        client[WEATHER_URL, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                val weatherDataModel = WeatherDataModel.fromJson(response)
                updateUI(weatherDataModel)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, e: Throwable, errorResponse: JSONObject) {
                Toast.makeText(this@ConditionsActivity, "No data available. Check your internet connection.", Toast.LENGTH_SHORT).show()
            }
        }]
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(weatherDataModel: WeatherDataModel) {
        adapter!!.updateFragments(weatherDataModel)
        val windSpeedtoInt = Math.rint(weatherDataModel.getmSpeed()).toInt()
        val windSpeed_txt = windSpeedtoInt.toString()
        val windDirection_txt = weatherDataModel.degrees
        val feelsLike_txt = weatherDataModel.feels_like
        val name_txt = weatherDataModel.name
        val date_txt = weatherDataModel.thisDate
        val pressure_txt = weatherDataModel.getmPressure().toString()
        val humidity_txt = weatherDataModel.humidity.toString()
        val cloudCover_txt = weatherDataModel.cloudCover.toString()
        val temperature_txt = weatherDataModel.temperature
        val instructions_txt = weatherDataModel.xcPotential
        name!!.text = name_txt
        date!!.text = date_txt
        temperature!!.text = temperature_txt + "c"
        feelsLike!!.text = "Feels like $feelsLike_txt°c"
        wind!!.text = windSpeed_txt + "m/s " + windDirection_txt
        humidity!!.text = "$humidity_txt%"
        cloudCover!!.text = "$cloudCover_txt%"
        pressure!!.text = pressure_txt + "hPa"
        descriptionTxt!!.text = instructions_txt
    }

    private val weatherForCurrentLocation: Unit
        get() {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {

                    val longitude = location.longitude.toString()
                    val latitude = location.latitude.toString()

                    val params = RequestParams()
                    params.put("lon", longitude)
                    params.put("lat", latitude)
                    params.put("appid", APP_ID)
                    doNetworking(params)
                }

                override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
                override fun onProviderEnabled(s: String) {}
                override fun onProviderDisabled(s: String) {
                }
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                return
            }
            locationManager!!.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener)
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima", "OnPermissionResult() Permission Granted")
                weatherForCurrentLocation
            }
        } else {
            Log.d("Clima", "OnPermissionResult() Permission Denied")
        }
    }

    private val isGpsEnabled: Unit
        get() {
            val service = (getSystemService(LOCATION_SERVICE) as LocationManager)
            val enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!enabled) {
                Toast.makeText(applicationContext, "Turn ON location.", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}