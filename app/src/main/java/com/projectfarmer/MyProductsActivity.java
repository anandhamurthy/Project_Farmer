package com.projectfarmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.projectfarmer.Adapters.CropAdapter;
import com.projectfarmer.Adapters.ProductsAdapter;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.Models.Products;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyProductsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String mCurrentUserId;
    private RelativeLayout No_Layout;

    private RecyclerView Crop_List;

    private ProductsAdapter productsAdapter;
    List<Products> productsList;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LottieAnimationView anim = findViewById(R.id.animationView);


        No_Layout = findViewById(R.id.no_layout);

        Crop_List = findViewById(R.id.crops_list);

        anim.setAnimation(R.raw.no_product);

        if (FirebaseAuth.getInstance().getCurrentUser()!=null){

            mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            layoutManager = new LinearLayoutManager(MyProductsActivity.this);
            Crop_List.setLayoutManager(layoutManager);
            Crop_List.setHasFixedSize(false);
            productsList = new ArrayList<>();
            productsAdapter = new ProductsAdapter(MyProductsActivity.this, productsList);
            Crop_List.setAdapter(productsAdapter);

            readCrops();

        }
    }

    private void readCrops() {

        db.collection("Products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    productsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Products products = document.toObject(Products.class);
                        if (mCurrentUserId.equals(products.getUser_id()))
                            productsList.add(products);
                    }
                    productsAdapter.notifyDataSetChanged();
                    if (productsList.isEmpty()){
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
}