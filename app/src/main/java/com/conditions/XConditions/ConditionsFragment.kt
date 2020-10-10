package com.conditions.XConditions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.XConditions.R

/**
 * A simple [Fragment] subclass.
 */
class ConditionsFragment : Fragment() {
    private var information: TextView? = null
    private var description: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_demo, container, false)
        information = view.findViewById(R.id.displayMessage)
        if (null != description) { // ako updateUI bilo povikano *pred onCreateView* togash da go staivme tekstot
            information!!.text = description
        }
        return view
    }

    fun updateUI(weatherDataModel: WeatherDataModel) {
        description = weatherDataModel.xcPotential
        if (null != information) { // vo momentot koga ke se povika updateUI fragmentot MOZHE da ne mu e povikano onCreateView
            information!!.text = description
        }
    }

    fun updateUISearch(weatherDataModel: WeatherDataModel) {
        description = weatherDataModel.searchMode
        if (null != information) {
            information!!.text = description
        }
    }

    fun updateUIClimb(weatherDataModel: WeatherDataModel) {
        description = weatherDataModel.climbMode
        if (null != information) {
            information!!.text = description
        }
    }
}