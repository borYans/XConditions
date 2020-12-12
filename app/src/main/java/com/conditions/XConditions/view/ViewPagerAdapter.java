package com.conditions.XConditions.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<ConditionsFragment> fragments = new ArrayList();

    public ViewPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);

        fragments.add(new ConditionsFragment());
        fragments.add(new ConditionsFragment());
        fragments.add(new ConditionsFragment());
    }


    @NotNull
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {

       switch (position) {
           case 0: return "Conditions";
           case 1: return "Search";
           case 2: return  "Climb";
           default: return null;
       }
    }



    public void updateFragments(DayType dayType) {

            this.fragments.get(0).updateUI(dayType);
            this.fragments.get(1).updateUISearch(dayType);
            this.fragments.get(2).updateUIClimb(dayType);

        }
    }

