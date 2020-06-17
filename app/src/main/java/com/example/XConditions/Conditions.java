package com.example.XConditions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Conditions extends AppCompatActivity {

    //Declare constants here:
    String APP_ID = "d9abacb346617d1b58974df664c01ee7";
    // Constants:
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;
    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;

    //Declare member variables here
    private TextView temperature;
    private TextView name;
    private TextView date;
    private TextView feelsLike;
    private TextView wind;
    private TextView pressure;
    private TextView humidity;
    private TextView cloudCover;
    private ViewPager viewPager;
    private TextView descriptionTxt;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayoutMode;


    //Delcare location manager and location listener:
    LocationManager locationManager;
    LocationListener locationListener;

    @SuppressLint({"ClickableViewAccessibility", "WrongViewCast", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);


        // Cast elements from layout here:
        temperature = findViewById(R.id.tempText);
        name = findViewById(R.id.nameTxt);
        date = findViewById(R.id.dateTxt);
        feelsLike = findViewById(R.id.feelsLikeTxt);
        wind = findViewById(R.id.windPTxt);
        pressure = findViewById(R.id.pressurePTxt);
        humidity = findViewById(R.id.humidityPTxt);
        cloudCover = findViewById(R.id.cloudsPTxt);
        viewPager = findViewById(R.id.viewPager);
        tabLayoutMode = findViewById(R.id.tabLayout);
        descriptionTxt = findViewById(R.id.instructionsTxt);


// Setting tabLayout and adapter
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayoutMode.setupWithViewPager(viewPager);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Clima", "onResume() called");
        Log.d("Clima", "Getting weather for current location");
        isGpsEnabled();
        getWeatherForCurrentLocation();


    }

    private void doNetworking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Clima", "Success! JSON: " + response.toString());

                WeatherDataModel weatherDataModel = WeatherDataModel.fromJson(response);
                updateUI(weatherDataModel);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                Log.d("Clima", "Fail: " + e.toString());
                Log.d("Clima", "Status code: " + statusCode);
                Toast.makeText(Conditions.this, "No data available. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(final WeatherDataModel weatherDataModel) {

        this.adapter.updateFragments(weatherDataModel);

        int windSpeedtoInt = (int) Math.rint(weatherDataModel.getmSpeed());
        String windSpeed_txt = String.valueOf(windSpeedtoInt);
        String windDirection_txt = weatherDataModel.getDegrees();
        String feelsLike_txt = weatherDataModel.getFeels_like();
        String name_txt = weatherDataModel.getName();
        String date_txt = weatherDataModel.getThisDate();
        String pressure_txt = String.valueOf(weatherDataModel.getmPressure());
        String humidity_txt = String.valueOf(weatherDataModel.getHumidity());
        String cloudCover_txt = String.valueOf(weatherDataModel.getCloudCover());
        String temperature_txt = weatherDataModel.getTemperature();
        String instructions_txt = weatherDataModel.getXcPotential();


        name.setText(name_txt);
        date.setText(date_txt);
        temperature.setText(temperature_txt + "c");
        feelsLike.setText("Feels like " + feelsLike_txt + "°c");
        wind.setText(windSpeed_txt + "m/s " + windDirection_txt);
        humidity.setText(humidity_txt + "%");
        cloudCover.setText(cloudCover_txt + "%");
        pressure.setText(pressure_txt + "hPa");
        descriptionTxt.setText(instructions_txt);


    }


    public void getWeatherForCurrentLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("myConditions", "OnLocationChanged() callback recieved");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d("myConditions", "Longitude: " + longitude);
                Log.d("myConditions", "Latitude: " + latitude);


                RequestParams params = new RequestParams();
                params.put("lon", longitude);
                params.put("lat", latitude);
                params.put("appid", APP_ID);
                doNetworking(params);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("myConditions", "OnProviderDisabled callback recieved");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima", "OnPermissionResult() Permission Granted");
                getWeatherForCurrentLocation();

            }

        } else {
            Log.d("Clima", "OnPermissionResult() Permission Denied");
        }

    }

    public void isGpsEnabled() {

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert service != null;
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Toast.makeText(getApplicationContext(), "Turn ON location.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}



