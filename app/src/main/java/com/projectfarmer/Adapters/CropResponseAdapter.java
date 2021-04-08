package com.projectfarmer.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.AddCropActivity;
import com.projectfarmer.Models.CropResponse;
import com.projectfarmer.MainActivity;
import com.projectfarmer.PHResultActivity;
import com.projectfarmer.R;

import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CropResponseAdapter extends RecyclerView.Adapter<CropResponseAdapter.CropHolder> {

    private Context mContext;
    private List<CropResponse> mCropResponses;

    private ProgressDialog mProgressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private AddCropActivity activity;
    private String crop_key;

    public CropResponseAdapter(AddCropActivity activity, Context context, List<CropResponse> crops, String crop_key){
        this.activity = activity;
        mContext = context;
        mCropResponses = crops;
        this.crop_key = crop_key;
    }

    @Override
    public CropHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_crop_response_layout, parent, false);
        return new CropHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CropHolder holder, final int position) {

        final CropResponse cropResponse = mCropResponses.get(position);

        mProgressDialog = new ProgressDialog(mContext);

        holder.Crop_Name.setText(cropResponse.getCrop_name());
        holder.Crop_Probability.setText(cropResponse.getProbability()*100+"%");
        Glide.with(mContext).load(cropResponse.getCrop_image()).placeholder(R.drawable.icon).into(holder.Crop_Image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cropResponse.getProbability()>0.0){

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setMessage("Do you want grown this Crop ?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int id) {

                                    HashMap cropMap = new HashMap<>();
                                    cropMap.put("level", 2);

                                    cropMap.put("crop_name", cropResponse.getCrop_name());
                                    cropMap.put("crop_image", cropResponse.getCrop_image());
                                    cropMap.put("crop_temperature", cropResponse.getTemperature());
                                    cropMap.put("crop_growth_period", cropResponse.getGrowth_period());
                                    cropMap.put("crop_disease_model", cropResponse.getDisease_model());
                                    cropMap.put("crop_irrigation_pattern", cropResponse.getIrrigation_pattern()+"");
                                    cropMap.put("crop_disease", cropResponse.getDisease());

                                    mProgressDialog.setTitle("Save");
                                    mProgressDialog.setMessage("saving Crop Details..");
                                    mProgressDialog.setCanceledOnTouchOutside(false);
                                    mProgressDialog.show();

                                    db.collection("Crops").document(crop_key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    db.collection("Crops").document(crop_key).update(cropMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mProgressDialog.dismiss();
                                                                    dialog.cancel();
                                                                    Intent intent = new Intent(mContext, MainActivity.class);
                                                                    mContext.startActivity(intent);
                                                                    activity.finish();

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
                                                                    Log.d(TAG, e.toString());
                                                                }
                                                            });
                                                }else{
                                                    db.collection("Crops").document(crop_key).set(cropMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mProgressDialog.dismiss();
                                                                    dialog.cancel();
                                                                    Intent intent = new Intent(mContext, PHResultActivity.class);
                                                                    mContext.startActivity(intent);
                                                                    activity.finish();

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
                                                                    Log.d(TAG, e.toString());
                                                                }
                                                            });
                                                }
                                            }
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
                    Toast.makeText(mContext, "You Cannot Grown this Crop", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCropResponses.size();
    }


    public class CropHolder extends RecyclerView.ViewHolder {

        public TextView Crop_Name, Crop_Probability;
        public ImageView Crop_Image;

        public CropHolder(View itemView) {
            super(itemView);

            Crop_Name = itemView.findViewById(R.id.crop_name);
            Crop_Probability = itemView.findViewById(R.id.crop_probability);
            Crop_Image = itemView.findViewById(R.id.crop_image);
        }
    }
}