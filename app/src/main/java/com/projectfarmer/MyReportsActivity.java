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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectfarmer.Adapters.CropAdapter;
import com.projectfarmer.Adapters.ReportsAdapter;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.Models.Reports;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyReportsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String mCurrentUserId;
    private RelativeLayout No_Layout;

    private RecyclerView Reports_List;

    private ReportsAdapter reportsAdapter;
    List<Reports> reportsList;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LottieAnimationView anim = findViewById(R.id.animationView);


        No_Layout = findViewById(R.id.no_layout);

        Reports_List = findViewById(R.id.reports_list);

        anim.setAnimation(R.raw.happy);

        if (FirebaseAuth.getInstance().getCurrentUser()!=null){

            mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            layoutManager = new LinearLayoutManager(MyReportsActivity.this);
            Reports_List.setLayoutManager(layoutManager);
            Reports_List.setHasFixedSize(false);
            reportsList = new ArrayList<>();
            reportsAdapter = new ReportsAdapter(MyReportsActivity.this, reportsList);
            Reports_List.setAdapter(reportsAdapter);

            readReports();

        }
    }

    private void readReports() {

        db.collection("Reports").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    reportsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Reports reports = document.toObject(Reports.class);
                        if (mCurrentUserId.equals(reports.getUser_id()))
                            reportsList.add(reports);
                    }
                    reportsAdapter.notifyDataSetChanged();
                    if (reportsList.isEmpty()){
                        No_Layout.setVisibility(View.VISIBLE);
                        Reports_List.setVisibility(View.GONE);
                    }else{
                        No_Layout.setVisibility(View.GONE);
                        Reports_List.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}