package com.projectfarmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.Adapters.FertilizerAdapter;
import com.projectfarmer.Adapters.MultiDaysAdapter;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.Models.Fertilizer;

import java.util.ArrayList;
import java.util.List;

public class PHResultActivity extends AppCompatActivity {

    private ImageView Image;
    private TextView PH_Value, Description;
    private RecyclerView Fertilizer_List;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String Crop_Key;

    private ProgressDialog mProgressDialog ;

    private FertilizerAdapter fertilizerAdapter;
    List<Fertilizer> fertilizerList;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_h_result);

        Intent intent = getIntent();
        Crop_Key = intent.getStringExtra("crop_key");

        Image = findViewById(R.id.image);
        PH_Value = findViewById(R.id.ph_value);
        Description = findViewById(R.id.desc);
        Fertilizer_List = findViewById(R.id.fertilizer_list);

        layoutManager = new LinearLayoutManager(PHResultActivity.this);
        Fertilizer_List.setLayoutManager(layoutManager);
        Fertilizer_List.setHasFixedSize(false);
        fertilizerList = new ArrayList<>();
        fertilizerAdapter = new FertilizerAdapter(PHResultActivity.this, fertilizerList);
        Fertilizer_List.setAdapter(fertilizerAdapter);

        mProgressDialog = new ProgressDialog(PHResultActivity.this);
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

                        fertilizerList.clear();
                        final Crops crops = document1.toObject(Crops.class);
                        for (Fertilizer fertilizer : crops.getFertilizers()){
                            fertilizerList.add(fertilizer);
                        }
                        fertilizerAdapter.notifyDataSetChanged();
                        PH_Value.setText(crops.getPh_value());
                        Description.setText(crops.getPh_description());
                        Glide.with(PHResultActivity.this).load(crops.getPh_land_image()).into(Image);

                        mProgressDialog.dismiss();

                    }
                }
            }
        });
    }
}