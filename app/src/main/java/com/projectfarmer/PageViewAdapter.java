package com.projectfarmer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageViewAdapter extends FragmentPagerAdapter {

    String Crop_Key, Crop_Model;
    public PageViewAdapter(@NonNull FragmentManager fm, String crop_Key, String crop_Model) {
        super(fm);
        Crop_Key = crop_Key;
        Crop_Model = crop_Model;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                AboutFragment aboutFragment = new AboutFragment(Crop_Key);
                return aboutFragment;
            case 1:
                WeatherFragment weatherFragment = new WeatherFragment(Crop_Key);
                return weatherFragment;
            case 2:
                DiseaseFragment diseaseFragment = new DiseaseFragment(Crop_Key, Crop_Model);
                return diseaseFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
