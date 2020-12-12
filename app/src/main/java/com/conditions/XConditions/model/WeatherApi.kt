package com.conditions.XConditions.model

import com.conditions.XConditions.model.WeatherDataModel
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    fun getCurrentWeather(@Query("lat") lat:String, @Query("lon") lon:String, @Query("APPID") app_id:String): Call<WeatherDataModel>

}