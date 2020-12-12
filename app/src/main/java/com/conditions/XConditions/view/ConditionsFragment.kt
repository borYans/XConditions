package com.conditions.XConditions.view

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

    fun updateUI(dayType: DayType) {
        description = dayType.xcPotential()
        if (null != information) { // vo momentot koga ke se povika updateUI fragmentot MOZHE da ne mu e povikano onCreateView
            information!!.text = description
        }
    }

    fun updateUISearch(dayType: DayType) {
        description = dayType.searchMode()
        if (null != information) {
            information!!.text = description
        }
    }

    fun updateUIClimb(dayType: DayType) {
       description = dayType.climbMode()
        if (null != information) {
            information!!.text = description
        }
    }
}