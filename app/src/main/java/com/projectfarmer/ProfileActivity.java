package com.projectfarmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.Login.LoginActivity;
import com.projectfarmer.Models.Users;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser mFirebaseUser;
    private String mCurrentUserId, Key, locationAddress;

    private FloatingActionButton Save;
    private TextInputEditText State, City, District;
    private TextInputEditText Name, Gender, Address,Pincode, Lon, Lat;
    private TextView Phone_Number;
    private Button LongLat;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        Key = intent.getStringExtra("access");

        mProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);

        Name = findViewById(R.id.name);
        Phone_Number = findViewById(R.id.mobile_number);
        Address = findViewById(R.id.address);
        City = findViewById(R.id.city);
        State = findViewById(R.id.state);
        District = findViewById(R.id.district);
        Pincode = findViewById(R.id.pincode);
        Gender = findViewById(R.id.gender);
        Lon = findViewById(R.id.lon);
        Lat = findViewById(R.id.lat);
        LongLat = findViewById(R.id.lonlat);
        Save = findViewById(R.id.save);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();

        DocumentReference bRef = db.collection("Users").document(mCurrentUserId);
        bRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Users users = document.toObject(Users.class);

                        Name.setText(users.getName());
                        Phone_Number.setText(users.getPhone_number());
                        Gender.setText(users.getGender());
                        State.setText(users.getState());
                        City.setText(users.getCity());
                        Pincode.setText(users.getPincode());
                        Address.setText(users.getAddress());
                        District.setText(users.getDistrict());
                        Lon.setText(users.getLongitude());
                        Lat.setText(users.getLatitude());
                        locationAddress=users.getLatitude()+" "+users.getLongitude();

                        mProgressDialog.dismiss();
                    }
                }
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEmpty(Name.getText().toString(),Address.getText().toString(),
                        City.getText().toString(),
                        State.getText().toString(),
                        Pincode.getText().toString(),
                        Gender.getText().toString(),
                        District.getText().toString())) {

                    if (locationAddress!=null){
                        String laglat[] =locationAddress.split(" ");
                        UpdateProfile(Name.getText().toString(),
                                Address.getText().toString(),
                                City.getText().toString(),
                                State.getText().toString(),
                                Pincode.getText().toString(),
                                Gender.getText().toString(),District.getText().toString(), laglat[1], laglat[0]);
                    }else{
                        Toast.makeText(ProfileActivity.this, "Get Longitude & Latitude", Toast.LENGTH_SHORT).show();
                    }






                }else{
                    Toast.makeText(ProfileActivity.this, "Complete all details", Toast.LENGTH_SHORT).show();
                }

            }
        });

        LongLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Address.getText().toString().isEmpty() && !City.getText().toString().isEmpty() &&
                        !State.getText().toString().isEmpty() && !Pincode.getText().toString().isEmpty()){
                    getLocationFromAddress(Address.getText().toString()+", "+City.getText().toString()+", "+State.getText().toString()+", "+Pincode.getText().toString());
                }else{
                    Toast.makeText(ProfileActivity.this, "Fill the address, city, state, pincode", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void UpdateProfile(String name, String address, String city, String state, String pincode, String gender, String district, String longitude, String latitude) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("address", address);
        map.put("city", city);
        map.put("pincode", pincode);
        map.put("state", state);
        map.put("verify", "true");
        map.put("gender", gender);
        map.put("district", district);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("user_id", mCurrentUserId);

        db.collection("Users").document(mCurrentUserId).update(map);

        Toast.makeText(ProfileActivity.this, "Updated!", Toast.LENGTH_SHORT).show();

        finish();
    }


    private boolean isEmpty(String name, String address, String city, String state, String pincode, String gender, String district) {
        if (name.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || pincode.isEmpty() || gender.isEmpty() || district.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Complete All Details", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Key.equals("false")) {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {

                db.collection("Users").document(auth.getCurrentUser().getUid().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (document.getString("verify").equals("true")) {
                                    Intent setupIntent = new Intent(ProfileActivity.this, MainActivity.class);
                                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(setupIntent);
                                    finish();
                                }
                            }
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });

            } else if (Key.equals("true")){
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        }


    }

    private void getLocationFromAddress(String address){

        final GeocodingLocation locationAddress = new GeocodingLocation();
        locationAddress.getAddressFromLocation(address, ProfileActivity.this, new GeocoderHandler());
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Toast.makeText(ProfileActivity.this, locationAddress, Toast.LENGTH_SHORT).show();
            Lat.setText(locationAddress.split(" ")[0]);
            Lon.setText(locationAddress.split(" ")[1]);
        }
    }
}
