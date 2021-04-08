package com.projectfarmer;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.kwabenaberko.openweathermaplib.constant.Languages;
import com.kwabenaberko.openweathermaplib.constant.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callback.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.model.currentweather.CurrentWeather;
import com.projectfarmer.Adapters.AlertsAdapter;
import com.projectfarmer.Adapters.CropResponseAdapter;
import com.projectfarmer.Adapters.MultiDaysAdapter;
import com.projectfarmer.Models.Alerts;
import com.projectfarmer.Models.CropResponse;
import com.projectfarmer.Models.MultiDays;
import com.projectfarmer.Models.Users;
import com.projectfarmer.Models.common.Temp;
import com.projectfarmer.Models.common.WeatherItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class WeatherFragment extends Fragment {

    private TextView Temperature, Desc, Humidity, Wind, Alert_Title;
    private LottieAnimationView Weather_Icon;
    private RecyclerView Next_List, Alerts_List;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MultiDaysAdapter multiDaysAdapter;
    private AlertsAdapter alertsAdapter;
    List<MultiDays> multiDaysList;
    List<Alerts> alertsList;
    ArrayList<Integer> alerts_ids = new ArrayList<>();
    LinearLayoutManager layoutManager;

    private String Crop_Key, url="https://api.openweathermap.org/data/2.5/onecall?";

    private String mCurrentUserId;

    private ProgressDialog mProgressDialog ;

    private OpenWeatherMapHelper helper;

    private LottieAnimationView No_Alerts;

    public WeatherFragment() {
    }

    public WeatherFragment(String crop_key) {
        Crop_Key=crop_key;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        mProgressDialog = new ProgressDialog(getContext());

        helper = new OpenWeatherMapHelper(getString(R.string.open_weather_map_api));
        helper.setUnits(Units.METRIC);
        helper.setLanguage(Languages.ENGLISH);

        Temperature = view.findViewById(R.id.current_temperature);
        Desc = view.findViewById(R.id.current_desc);
        Humidity = view.findViewById(R.id.current_humidity);
        Wind = view.findViewById(R.id.current_wind);
        Alert_Title = view.findViewById(R.id.alerts_title);
        Alerts_List = view.findViewById(R.id.alerts_list);
        Weather_Icon = view.findViewById(R.id.current_weather_icon);
        No_Alerts = view.findViewById(R.id.no_alerts);

        Next_List = view.findViewById(R.id.next_weather_list);

        alerts_ids.add(600);
        alerts_ids.add(600);
        alerts_ids.add(600);
        alerts_ids.add(600);
        alerts_ids.add(600);
        alerts_ids.add(600);
        alerts_ids.add(600);
        alerts_ids.add(600);
        alerts_ids.add(600);
        alerts_ids.add(600);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        Next_List.setLayoutManager(layoutManager);
        Next_List.setHasFixedSize(false);
        multiDaysList = new ArrayList<>();
        alertsList = new ArrayList<>();
        multiDaysAdapter = new MultiDaysAdapter(getContext(), multiDaysList);
        alertsAdapter = new AlertsAdapter(getContext(), alertsList);
        Next_List.setAdapter(multiDaysAdapter);
        Alerts_List.setAdapter(alertsAdapter);

        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {

            mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("Users").document(mCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        final DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Users users = document.toObject(Users.class);
                            getCurrentWeather(users.getCity());
                            get7DaysWeather(users.getLongitude(), users.getLatitude());
                            getAlerts(users.getLongitude(), users.getLatitude());

                        }
                    }
                }
            });
        }

        return view;
    }

    private void getCurrentWeather(String city) {


        helper.getCurrentWeatherByCityName(city, new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {

                Temperature.setText(String.format(Locale.getDefault(), "%.0f°", currentWeather.getMain().getTemp()));
                Desc.setText(currentWeather.getWeather().get(0).getDescription());
                Humidity.setText("Humidity "+currentWeather.getMain().getHumidity()+"%");
                Wind.setText("Wind "+currentWeather.getWind().getSpeed()+"km/hr");
                Weather_Icon.setAnimation(getWeatherAnimation(currentWeather.getWeather().get(0).getId()));
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v("TAG", throwable.getMessage());
            }
        });
    }


    public static int getWeatherAnimation(Long weatherCode) {
        if (weatherCode / 100 == 2) {
            return R.raw.storm_weather;
        } else if (weatherCode / 100 == 3) {
            return R.raw.rainy_weather;
        } else if (weatherCode / 100 == 5) {
            return R.raw.rainy_weather;
        } else if (weatherCode / 100 == 6) {
            return R.raw.snow_weather;
        } else if (weatherCode / 100 == 7) {
            return R.raw.unknown;
        } else if (weatherCode == 800) {
            return R.raw.clear_day;
        } else if (weatherCode == 801) {
            return R.raw.few_clouds;
        } else if (weatherCode == 803) {
            return R.raw.broken_clouds;
        } else if (weatherCode / 100 == 8) {
            return R.raw.cloudy_weather;
        }
        return R.raw.unknown;
    }

    private void getAlerts(String lon, String lat) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url+"lat="+lat+"&lon="+lon+"&exclude=hourly,minutely,current&appid=008871078b73cfae8189d0762f460c36",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    JSONArray weather = response.getJSONArray("daily");

                    multiDaysList.clear();

                    for(int i=1;i<weather.length();i++){
                        MultiDays multiDays = new MultiDays();
                        ArrayList<WeatherItem> weatherItems = new ArrayList<>();
                        JSONObject object = weather.getJSONObject(i);
                        Temp temp = new Temp();
                        JSONObject temp_object = object.getJSONObject("temp");
                        temp.setDay(temp_object.getDouble("day"));
                        temp.setMax(temp_object.getDouble("max")/10);
                        temp.setMin(temp_object.getDouble("min"));
                        temp.setMorn(temp_object.getDouble("morn"));
                        temp.setNight(temp_object.getDouble("night"));
                        temp.setEve(temp_object.getDouble("eve"));
                        JSONArray weather_array = object.getJSONArray("weather");
                        for(int w=0;w<weather_array.length();w++) {
                            WeatherItem weatherItem = new WeatherItem();
                            JSONObject weather_object = weather_array.getJSONObject(w);
                            weatherItem.setDescription(weather_object.getString("description"));
                            weatherItem.setId(weather_object.getInt("id"));
                            weatherItem.setIcon(weather_object.getString("icon"));
                            weatherItem.setMain(weather_object.getString("main"));
                            weatherItems.add(weatherItem);
                        }
                        multiDays.setTemp(temp);
                        multiDays.setWeatherItems(weatherItems);
                        multiDays.setHumidity(object.getInt("humidity"));
                        multiDays.setPressure(object.getInt("pressure"));
                        multiDays.setWind_speed(object.getDouble("wind_speed"));
                        multiDays.setDt(object.getLong("dt"));
                        multiDaysList.add(multiDays);
                    }
                    multiDaysAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        requestQueue.add(jsonObjectRequest);
    }

    private void get7DaysWeather(String lon, String lat) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url+"lat="+lat+"&lon="+lon+"&exclude=hourly,minutely,current&appid=008871078b73cfae8189d0762f460c36",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    JSONArray weather = response.getJSONArray("daily");

                    alertsList.clear();

                    for(int i=1;i<weather.length();i++){
                        Alerts alerts= new Alerts();
                        ArrayList<WeatherItem> weatherItems = new ArrayList<>();
                        JSONObject object = weather.getJSONObject(i);
                        Temp temp = new Temp();
                        JSONObject temp_object = object.getJSONObject("temp");
                        temp.setDay(temp_object.getDouble("day"));
                        temp.setMax(temp_object.getDouble("max")/10);
                        temp.setMin(temp_object.getDouble("min"));
                        temp.setMorn(temp_object.getDouble("morn"));
                        temp.setNight(temp_object.getDouble("night"));
                        temp.setEve(temp_object.getDouble("eve"));
                        JSONArray weather_array = object.getJSONArray("weather");
                        for(int w=0;w<weather_array.length();w++) {
                            WeatherItem weatherItem = new WeatherItem();
                            JSONObject weather_object = weather_array.getJSONObject(w);
                            weatherItem.setDescription(weather_object.getString("description"));
                            weatherItem.setId(weather_object.getInt("id"));
                            weatherItem.setIcon(weather_object.getString("icon"));
                            weatherItem.setMain(weather_object.getString("main"));
                            if (alerts_ids.contains(weather_object.getInt("id")))
                            weatherItems.add(weatherItem);
                        }
                        if (weatherItems.size()!=0){
                            alerts.setTemp(temp);
                            alerts.setWeatherItems(weatherItems);
                            alerts.setHumidity(object.getInt("humidity"));
                            alerts.setPressure(object.getInt("pressure"));
                            alerts.setWind_speed(object.getDouble("wind_speed"));
                            alerts.setDt(object.getLong("dt"));
                            alerts.setAlert("No need to Irrigate the Crops.");
                            alertsList.add(alerts);
                        }


                    }
                    if (alertsList.isEmpty()){
                        No_Alerts.setVisibility(View.VISIBLE);
                        Alerts_List.setVisibility(View.GONE);
                        Alert_Title.setVisibility(View.VISIBLE);
                        No_Alerts.setAnimation(R.raw.alert);
                    }else{
                        Alerts_List.setVisibility(View.VISIBLE);
                        Alert_Title.setVisibility(View.VISIBLE);
                        No_Alerts.setVisibility(View.GONE);
                    }
                    alertsAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        requestQueue.add(jsonObjectRequest);
    }

}