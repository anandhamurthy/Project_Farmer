package com.projectfarmer.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.projectfarmer.R;

public class StartActivity extends AppCompatActivity {

    private Button Farmer, User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Farmer = findViewById(R.id.farmer);
        User = findViewById(R.id.user);

        Farmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                intent.putExtra("type", "Farmer");
                startActivity(intent);
            }
        });

        User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StartActivity.this, "Yet to Build..", Toast.LENGTH_SHORT).show();
            }
        });
    }
}