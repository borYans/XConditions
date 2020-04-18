package com.example.myconditions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemoFragment extends Fragment {

    private TextView information;
    private String description;

    public DemoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_demo, container, false);
        information = view.findViewById(R.id.displayMessage);
        if (null != description) { // ako updateUI bilo povikano *pred onCreateView* togash da go staivme tekstot

            information.setText(description);


        }
        return view;
    }

    public void updateUI(WeatherDataModel weatherDataModel) {

        description = weatherDataModel.getXcPotential();

        if (null != information) { // vo momentot koga ke se povika updateUI fragmentot MOZHE da ne mu e povikano onCreateView
            information.setText(description);

        }
    }

    public void updateUISearch(WeatherDataModel weatherDataModel) {
        description = weatherDataModel.getSearchMode();

        if (null != information) {
            information.setText(description);
        }
    }

    public void updateUIClimb(WeatherDataModel weatherDataModel) {

        description = weatherDataModel.getClimbMode();

        if (null != information) {
            information.setText(description);
        }

    }
}



