package com.projectfarmer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.kwabenaberko.openweathermaplib.constant.Languages;
import com.kwabenaberko.openweathermaplib.constant.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callback.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.model.currentweather.CurrentWeather;
import com.projectfarmer.Adapters.CropResponseAdapter;
import com.projectfarmer.Models.CropResponse;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.Models.Users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddCropActivity extends AppCompatActivity {

    private Button Suggest;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView Crop_Response_List;

    private OpenWeatherMapHelper helper;

    private CropResponseAdapter cropResponseAdapter;
    List<CropResponse> cropResponseList;
    LinearLayoutManager layoutManager;

    private String postUrl = "https://crop-suggest-api.herokuapp.com/predict/?";

    private String crop_key, mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        Intent intent = getIntent();
        crop_key = intent.getStringExtra("crop_key");

        Suggest = findViewById(R.id.suggest);

        helper = new OpenWeatherMapHelper(getString(R.string.open_weather_map_api));
        helper.setUnits(Units.METRIC);
        helper.setLanguage(Languages.ENGLISH);

        Crop_Response_List = findViewById(R.id.crop_response_list);

        layoutManager = new LinearLayoutManager(AddCropActivity.this);
        Crop_Response_List.setLayoutManager(layoutManager);
        Crop_Response_List.setHasFixedSize(false);
        cropResponseList = new ArrayList<>();
        cropResponseAdapter = new CropResponseAdapter(AddCropActivity.this, AddCropActivity.this, cropResponseList, crop_key);
        Crop_Response_List.setAdapter(cropResponseAdapter);

        Suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db.collection("Users").document(mCurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Users users = document.toObject(Users.class);


                                db.collection("Crops").document(crop_key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            final DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                Crops crops = document.toObject(Crops.class);

                                                helper.getCurrentWeatherByCityName(users.getCity(), new CurrentWeatherCallback() {
                                                    @Override
                                                    public void onSuccess(CurrentWeather currentWeather) {
                                                        SuggestCrop(crops.getPh_value(),
                                                                currentWeather.getMain().getTemp(),
                                                                currentWeather.getMain().getHumidity(),
                                                                users.getDistrict());
                                                    }

                                                    @Override
                                                    public void onFailure(Throwable throwable) {
                                                        Log.v("TAG", throwable.getMessage());
                                                    }
                                                });


                                            }
                                        }
                                    }
                                });

                            }
                        }
                    }
                });

            }
        });



    }


    private void SuggestCrop(String ph_value, double temp, double humidity, String district) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl+"ph_value="+ph_value+"&temperature="+temp+"&humidity="+humidity+"&rainfall=110",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);

                try {
                    cropResponseList.clear();
                    for (int i=1;i<=5;i++){

                        JSONObject crop = response.getJSONObject(String.format("crop_%d", i));
                        Log.i("fr",crop.toString());
                        cropResponseList.add(new CropResponse(crop.getString("crop_name"), crop.getString("crop_image"),
                                crop.getString("disease"),crop.getString("temperature"), crop.getString("disease_model"), crop.getString("growth_period"),
                                crop.getDouble("probability"), crop.getInt("irrigation_pattern")));
                    }
                    cropResponseAdapter.notifyDataSetChanged();

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