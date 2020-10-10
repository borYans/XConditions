package com.conditions.XConditions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<ConditionsFragment> fragments = new ArrayList();

    public ViewPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);

        fragments.add(new ConditionsFragment());
        fragments.add(new ConditionsFragment());
        fragments.add(new ConditionsFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Conditions";
        } else if (position == 1) {
            return "Search";
        } else if (position == 2) {
            return "Climb";
        } else
            return null;
    }

    public void updateFragments(WeatherDataModel weatherDataModel) {

            this.fragments.get(0).updateUI(weatherDataModel);
            this.fragments.get(1).updateUISearch(weatherDataModel);
            this.fragments.get(2).updateUIClimb(weatherDataModel);

        }
    }

