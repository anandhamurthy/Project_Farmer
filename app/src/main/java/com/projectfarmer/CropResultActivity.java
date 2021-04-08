package com.projectfarmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.Models.Crops;

import de.hdodenhof.circleimageview.CircleImageView;

public class CropResultActivity extends AppCompatActivity {

    private CircleImageView Crop_Image;
    private TextView Crop_Name, Temperature, Growth_Pattern, Irrigation_Pattern, Disease;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String Crop_Key;

    private ProgressDialog mProgressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_result);

        Intent intent = getIntent();
        Crop_Key = intent.getStringExtra("crop_key");

        Crop_Image = findViewById(R.id.crop_image);
        Crop_Name = findViewById(R.id.crop_name);
        Temperature = findViewById(R.id.crop_temperature);
        Growth_Pattern = findViewById(R.id.crop_growth);
        Irrigation_Pattern = findViewById(R.id.crop_irrigation);
        Disease = findViewById(R.id.crop_disease);

        mProgressDialog = new ProgressDialog(CropResultActivity.this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);

        DocumentReference bRef = db.collection("Crops").document(Crop_Key);
        bRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document1 = task.getResult();
                    if (document1.exists()) {

                        final Crops crops = document1.toObject(Crops.class);

                        Glide.with(CropResultActivity.this).load(crops.getCrop_image()).into(Crop_Image);

                        Crop_Name.setText(crops.getCrop_name());
                        Temperature.setText(crops.getCrop_temperature());
                        Growth_Pattern.setText(crops.getCrop_growth_period());
                        Irrigation_Pattern.setText(crops.getCrop_irrigation_pattern());
                        Disease.setText(crops.getCrop_disease().replaceAll(", ","\n"));

                        mProgressDialog.dismiss();

                    }
                }
            }
        });


    }
}