package com.projectfarmer.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.projectfarmer.R;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity{

    private TextInputEditText Login_Phone_Number;
    private Button Login_Verify;
    private String Code="+91", Type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        Type = intent.getStringExtra("type");
        

        Login_Phone_Number = findViewById(R.id.login_phone_number);
        Login_Verify= findViewById(R.id.next);

        Login_Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String number = Login_Phone_Number.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    Login_Phone_Number.setError(getString(R.string.validation_number));
                    Login_Phone_Number.requestFocus();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                intent.putExtra("phonenumber", Code+number);
                intent.putExtra("type", Type);
                startActivity(intent);


            }
        });
    }

    private void updateConfig(String locale) {
        Resources res = getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();
        configuration.setLocale(new Locale(locale.toLowerCase()));
        res.updateConfiguration(configuration, displayMetrics);
    }

}

