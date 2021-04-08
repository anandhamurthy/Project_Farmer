package com.projectfarmer.Models;

import com.projectfarmer.Models.common.Temp;
import com.projectfarmer.Models.common.WeatherItem;

import java.util.ArrayList;

public class MultiDays {

    Temp temp;
    int pressure, humidity;
    double wind_speed;
    long dt;

    ArrayList<WeatherItem> weatherItems;


    public MultiDays() {
    }

    public MultiDays(Temp temp, int pressure, int humidity, double wind_speed, long dt, ArrayList<WeatherItem> weatherItems) {
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind_speed = wind_speed;
        this.dt = dt;
        this.weatherItems = weatherItems;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(double wind_speed) {
        this.wind_speed = wind_speed;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public ArrayList<WeatherItem> getWeatherItems() {
        return weatherItems;
    }

    public void setWeatherItems(ArrayList<WeatherItem> weatherItems) {
        this.weatherItems = weatherItems;
    }
}
