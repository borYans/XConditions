package com.conditions.XConditions.view

import androidx.lifecycle.AndroidViewModel
import java.util.*

class DayType(wind: Int, humidity: Int, pressure: Int, clouds: Int) {

    private val windSpeed = wind
    private val airHumidity = humidity
    private val airPressure = pressure
    private val cloudCover = clouds


    private val sticky = 1021..1035
    private val fizzy = 1011..1021
    private val earlyMorning = 0..5
    private val lateMorning = 6..8
    private val afternoon = 9..21
    private val evening = 22..23

    companion object {
        private const val stickyDay = "Expect strong hard edged thermals with bullet cores, strong sink and most likely blue sky."
        private const val fizzyDay = "Expect softer thermals with cumulus cloud development. Should be pleasant to fly, head for base and follow the clouds!"
        private const val soupDay = "Not likely possible for big XC distance. Warm moist stable air is thick and soup like, thermals can’t move through it well."
        private const val windySticky = "May be turbulent! Hard edged thermals with strong micro cores. Weak lifts gets blown away and expect descent turbulence."
        private const val windyFizzy = "Windy unstable day. Possibility for cloud street formation."
    }


    fun xcPotential(): String {

        val c: Calendar = Calendar.getInstance()

        when (c.get(Calendar.HOUR_OF_DAY)) {
            in earlyMorning -> return "Night XC flying? Try on the ground for change."
            in lateMorning -> return "Good morning! Later you will get your info. First thing, coffee!"
            in afternoon -> {
                return if (windSpeed < 4 && airPressure in sticky && airHumidity <= 42 && cloudCover <= 70) {
                    stickyDay
                } else if (windSpeed < 4 && airPressure in fizzy && airHumidity in 20..67 && cloudCover <= 70) {
                    fizzyDay
                } else if (windSpeed < 4 && airPressure in sticky && airHumidity > 45 && cloudCover <= 60) {
                    soupDay
                } else if (windSpeed in 4..6 && airPressure in sticky && airHumidity <= 42 && cloudCover <= 70) {
                    windySticky
                } else if (windSpeed in 4..6 && airPressure <= 1021 && airHumidity in 20..67 && cloudCover <= 60) {
                    windyFizzy
                } else {
                    "No potential for XC flying in the moment."
                }
            }
            in evening -> return "It's getting late. Except you want to catch moonlight thermals?"
        }
        return "Something went wrong :("
    }

    fun searchMode(): String {
        return when (xcPotential()) {
            fizzyDay -> "Thermals will trigger on slightest change in terrain. Usually best and strongest thermals are away from the mountain."
            stickyDay -> "Search wide and slow covering as much ground as possible with the best sink rate. Thermal will trigger on the most heated sources and fly over the mountain peaks."
            soupDay -> "Try to search for very good heated source on day like this and hope that there will be any lift."
            windyFizzy -> "The best lift you will find on the upwind side of the cloud. If you are low, then search downwind from heated source on the ground."
            windySticky -> "Search downwind of any good heated source on the ground. Towns and villages works good on a blue day."
            else -> "Search for cold beer. Why not?"
        }
    }

    fun climbMode(): String {
        return when (xcPotential()) {
            fizzyDay -> "Don't turn immediately, relax and feel the glider. Listen to your vario and work your 360° turns."
            stickyDay -> "When you hit micro cores crank hard and tight as possible. You might get half turn in lift, but for sure you can climb like this. Fight for every meter and never give up!"
            soupDay -> "Try to listen your vario and hang for any climb rate you get."
            windyFizzy -> "Find the strongest lift by working the upwind side of the thermal."
            windySticky -> "Stronger cores punch trough and stay upwind. That is your goal, center your 360s on the upwind side of thermals."
            else -> "No thermals, no climbing."
        }
    }
}



