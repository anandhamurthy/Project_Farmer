package com.projectfarmer.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.projectfarmer.MainActivity;
import com.projectfarmer.ProfileActivity;
import com.projectfarmer.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class VerifyActivity extends AppCompatActivity {

    private String verificationId, Phone_Number;
    private FirebaseAuth mAuth;
    private TextInputEditText Verify_Code;
    private TextView Verify_Timer;
    private Button Verify_Done, Verify_Resend;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CountDownTimer countDownTimer;

    private boolean startTimer = false;

    private final long startTime = (120 * 1000);
    private final long interval = 1 * 1000;

    private Long UserID;
    private String Type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        Type = intent.getStringExtra("type");

        mAuth = FirebaseAuth.getInstance();

        Verify_Code = findViewById(R.id.verify_code);
        Verify_Done=findViewById(R.id.verify);
        Verify_Timer=findViewById(R.id.verify_timer);
        Verify_Resend=findViewById(R.id.verify_resend);

        countDownTimer = new MyCountDownTimer(startTime, interval);

        DocumentReference orderIdRef = db.collection("Counters").document("customers");
        orderIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentV = task.getResult();
                    assert documentV != null;
                    if (documentV.exists()) {
                        UserID = documentV.getLong("customer_counter");
                    }
                }
            }
        });

        Verify_Timer.setText(Verify_Timer.getText() + String.valueOf(startTime / (60 * 1000)));
        timerControl(true);

        Phone_Number = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(Phone_Number);

        Verify_Resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(Phone_Number);
                timerControl(true);
            }
        });

        Verify_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s=Verify_Code.getText().toString();
                if (s.isEmpty() || s.length() < 6) {
                    Verify_Code.setError("Enter code.");
                    Verify_Code.requestFocus();
                }else {
                    verifyCode(s.toString());
                }
            }
        });


    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        final ProgressDialog pd = new ProgressDialog(VerifyActivity.this);
        pd.setMessage("Verifying..");
        pd.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            final HashMap userMap = new HashMap<>();
                            userMap.put("phone_number", Phone_Number);
                            userMap.put("device_token", device_token);
                            userMap.put("name", "");
                            userMap.put("gender", "");
                            userMap.put("address", "");
                            userMap.put("city", "");
                            userMap.put("state", "");
                            userMap.put("district", "");
                            userMap.put("latitude", "");
                            userMap.put("longitude", "");
                            userMap.put("pincode", "");
                            userMap.put("verify", "false");
                            userMap.put("type", Type);
                            userMap.put("user_id", mAuth.getCurrentUser().getUid());

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            final String uid = current_user.getUid();

                            db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            if (document.getString("verify").equals("false")){
                                                pd.dismiss();
                                                Intent setupIntent = new Intent(VerifyActivity.this, ProfileActivity.class);
                                                setupIntent.putExtra("access", "false");
                                                setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(setupIntent);
                                                finish();
                                            }else{
                                                pd.dismiss();
                                                Intent mainIntent = new Intent(VerifyActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                            }
                                        }else {
                                            db.collection("Users").document(uid).set(userMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pd.dismiss();
                                                            Intent mainIntent = new Intent(VerifyActivity.this, MainActivity.class);
                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(mainIntent);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(VerifyActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, e.toString());
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.d(TAG, "Failed with: ", task.getException());
                                    }
                                }
                            });


                        }


                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                VerifyActivity.this,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Verify_Code.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    public void timerControl(Boolean startTimer) {
        if (startTimer) {
            countDownTimer.start();
            Verify_Resend.setVisibility(View.GONE);
            Verify_Done.setVisibility(View.VISIBLE);

        } else {
            countDownTimer.cancel();
            Verify_Resend.setVisibility(View.VISIBLE);
            Verify_Done.setVisibility(View.GONE);

        }

    }


    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }


        @Override
        public void onFinish() {
            Verify_Timer.setText("00 : 00");
            Verify_Resend.setVisibility(View.VISIBLE);
        }


        @Override
        public void onTick(long millisUntilFinished) {

            long currentTime = millisUntilFinished/1000 ;

            Verify_Timer.setText("" + currentTime/60 + " : " +((currentTime % 60)>=10 ? currentTime % 60:"0" +( currentTime % 60)));

        }

    }
}