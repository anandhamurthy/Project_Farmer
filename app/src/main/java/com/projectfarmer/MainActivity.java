package com.projectfarmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kwabenaberko.openweathermaplib.constant.Languages;
import com.kwabenaberko.openweathermaplib.constant.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callback.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.model.currentweather.CurrentWeather;
import com.projectfarmer.Adapters.CropAdapter;
import com.projectfarmer.Login.StartActivity;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.Models.Users;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FloatingActionButton Add;

    private OpenWeatherMapHelper helper;

    private String mCurrentUserId;
    private RelativeLayout No_Layout;

    private RecyclerView Crop_List;

    private CropAdapter cropAdapter;
    List<Crops> cropsList;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LottieAnimationView anim = findViewById(R.id.animationView);

        Add = findViewById(R.id.add_crop);

        No_Layout = findViewById(R.id.no_layout);

        Crop_List = findViewById(R.id.crop_list);

        anim.setAnimation(R.raw.plant);

        if (FirebaseAuth.getInstance().getCurrentUser()!=null){

            mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            layoutManager = new LinearLayoutManager(MainActivity.this);
            Crop_List.setLayoutManager(layoutManager);
            Crop_List.setHasFixedSize(false);
            cropsList = new ArrayList<>();
            cropAdapter = new CropAdapter(MainActivity.this, cropsList);
            Crop_List.setAdapter(cropAdapter);

            readCrops();

        }

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewCropActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            sendToLogin();
        }else {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            db.collection("Users").document(auth.getCurrentUser().getUid().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.getString("verify").equals("false")) {
                                Intent setupIntent = new Intent(MainActivity.this, ProfileActivity.class);
                                setupIntent.putExtra("access", "false");
                                setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(setupIntent);
                                finish();
                            }
                        }
                    } else {
                        Log.d("TAG", "Failed with: ", task.getException());
                    }
                }
            });
        }
    }


    private void readCrops() {

        db.collection("Crops").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    cropsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Crops crops = document.toObject(Crops.class);
                        if (mCurrentUserId.equals(crops.getUser_id()))
                        cropsList.add(crops);
                    }
                    cropAdapter.notifyDataSetChanged();
                    if (cropsList.isEmpty()){
                        No_Layout.setVisibility(View.VISIBLE);
                        Crop_List.setVisibility(View.GONE);
                    }else{
                        No_Layout.setVisibility(View.GONE);
                        Crop_List.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                break;
            case R.id.profile:
                Intent setupIntent = new Intent(MainActivity.this, ProfileActivity.class);
                setupIntent.putExtra("access", "visit");
                startActivity(setupIntent);
                break;
            case R.id.reports:
                Intent reportsIntent = new Intent(MainActivity.this, MyReportsActivity.class);
                startActivity(reportsIntent);
                break;
            case R.id.sold:
                Intent sellIntent = new Intent(MainActivity.this, MyProductsActivity.class);
                startActivity(sellIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}