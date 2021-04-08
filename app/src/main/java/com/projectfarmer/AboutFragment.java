package com.projectfarmer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.Models.Crops;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class AboutFragment extends Fragment {

    private CircleImageView Crop_Image;
    private TextView Crop_Name, Temperature, Growth_Pattern, Irrigation_Pattern, Disease;
    private Button Harvested;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String Crop_Key;

    private ProgressDialog mProgressDialog ;


    public AboutFragment() {
    }

    public AboutFragment(String crop_Key) {
        this.Crop_Key = crop_Key;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        Crop_Image = view.findViewById(R.id.crop_image);
        Crop_Name = view.findViewById(R.id.crop_name);
        Temperature = view.findViewById(R.id.crop_temperature);
        Growth_Pattern = view.findViewById(R.id.crop_growth);
        Irrigation_Pattern = view.findViewById(R.id.crop_irrigation);
        Disease = view.findViewById(R.id.crop_disease);
        Harvested = view.findViewById(R.id.harvest);

        mProgressDialog = new ProgressDialog(getContext());
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

                        Glide.with(getContext()).load(crops.getCrop_image()).into(Crop_Image);

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


        Harvested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Have you harvested the crop ?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {

                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                                View mView = getLayoutInflater().inflate(R.layout.harvest_layout, null);

                                final EditText note = mView.findViewById(R.id.note);
                                Button submit = mView.findViewById(R.id.submit);

                                mBuilder.setView(mView);
                                final AlertDialog inner_dialog = mBuilder.create();
                                inner_dialog.setCanceledOnTouchOutside(true);
                                inner_dialog.show();
                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (!note.getText().toString().isEmpty()){

                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                            builder1.setMessage("Do you want to Submit ?");
                                            builder1.setCancelable(true);
                                            builder1.setPositiveButton(
                                                    "Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(final DialogInterface dialog, int id) {

                                                            mProgressDialog.setTitle("Save");
                                                            mProgressDialog.setMessage("Saving..");
                                                            mProgressDialog.show();
                                                            mProgressDialog.setCanceledOnTouchOutside(false);


                                                            final HashMap cropMap = new HashMap<>();
                                                            cropMap.put("crop_harvest_note", note.getText().toString());
                                                            cropMap.put("level", 3);


                                                            db.collection("Crops").document(Crop_Key).update(cropMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    Toast.makeText(getContext(), "Saved Successfully.", Toast.LENGTH_SHORT).show();
                                                                    mProgressDialog.dismiss();
                                                                    dialog.dismiss();
                                                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                                                    startActivity(intent);
                                                                    getActivity().finish();

                                                                }
                                                            });

                                                        }
                                                    });
                                            builder1.setNegativeButton(
                                                    "No",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                            AlertDialog alert11 = builder1.create();
                                            alert11.show();
                                        }else{
                                            Toast.makeText(getContext(), "Enter a note.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                                dialog.cancel();
                            }
                        });
                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });



        return view;
    }
}