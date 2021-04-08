package com.projectfarmer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CropDetailsActivity extends AppCompatActivity {

    private TextView About, Disease, Weather;
    private ViewPager mViewPager;

    private PageViewAdapter pageViewAdapter;

    private String Crop_Key, Crop_Model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_details);

        Intent intent = getIntent();
        Crop_Key = intent.getStringExtra("crop_key");
        Crop_Model = intent.getStringExtra("crop_model");

        About = findViewById(R.id.about);
        Disease = findViewById(R.id.disease);
        Weather = findViewById(R.id.weather);
        mViewPager = findViewById(R.id.crop_details_viewpager);


        pageViewAdapter = new PageViewAdapter(getSupportFragmentManager(), Crop_Key, Crop_Model);
        mViewPager.setAdapter(pageViewAdapter);

        About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        Weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });

        Disease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
            }
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changetabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changetabs(int position) {
        if (position==0){
            About.setTextSize(20);
            Weather.setTextSize(15);
            Disease.setTextSize(15);
        }else if (position==1){
            About.setTextSize(15);
            Weather.setTextSize(20);
            Disease.setTextSize(15);
        }else{
            About.setTextSize(15);
            Weather.setTextSize(15);
            Disease.setTextSize(20);
        }
    }
}