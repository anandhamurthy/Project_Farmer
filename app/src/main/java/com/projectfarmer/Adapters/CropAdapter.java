package com.projectfarmer.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.projectfarmer.AddCropActivity;
import com.projectfarmer.AddProductActivity;
import com.projectfarmer.AddReportActivity;
import com.projectfarmer.CropDetailsActivity;
import com.projectfarmer.CropResultActivity;
import com.projectfarmer.MainActivity;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.PHResultActivity;
import com.projectfarmer.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropHolder> {

    private Context mContext;
    private List<Crops> mCrop;

    private ProgressDialog mProgressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CropAdapter(Context context, List<Crops> crops){
        mContext = context;
        mCrop = crops;
    }

    @Override
    public CropHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_crop_layout, parent, false);
        return new CropHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CropHolder holder, final int position) {

        final Crops crops = mCrop.get(position);


        mProgressDialog = new ProgressDialog(mContext);

        if (crops.getLevel()==1){
            holder.Crop_Name.setText("pH Value");
            holder.Desc.setText("Your land has \n"+crops.getPh_value());
            Glide.with(mContext).load(crops.getPh_land_image()).into(holder.Crop_Image);
        }else if (crops.getLevel()==2){
            holder.Crop_Name.setText(crops.getCrop_name());
            holder.Desc.setText("Cultivate the crop & have a good Harvest!");
            Glide.with(mContext).load(crops.getCrop_image()).into(holder.Crop_Image);
        }else{
            holder.Crop_Name.setText(crops.getCrop_name());
            holder.Desc.setText("Harvested!");
            Glide.with(mContext).load(crops.getCrop_image()).into(holder.Crop_Image);
        }

        if (crops.getLevel()==4 || crops.isFailure()){
            holder.More.setVisibility(View.GONE);
        }else{
            holder.More.setVisibility(View.VISIBLE);
        }

        holder.More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sell:
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setMessage("Do you want to sell the crop ?");
                                builder.setCancelable(true);
                                builder.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog, int id) {

                                                mProgressDialog.setTitle("Save");
                                                mProgressDialog.setMessage("Saving..");
                                                mProgressDialog.show();
                                                mProgressDialog.setCanceledOnTouchOutside(false);


                                                final HashMap cropMap = new HashMap<>();
                                                cropMap.put("level", 4);


                                                db.collection("Crops").document(crops.getCrop_id()).update(cropMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        db.collection("Crops").document(crops.getCrop_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    final DocumentSnapshot document1 = task.getResult();
                                                                    if (document1.exists()) {

                                                                        final Crops crops = document1.toObject(Crops.class);


                                                                        Toast.makeText(mContext, "Saved Successfully.", Toast.LENGTH_SHORT).show();
                                                                        mProgressDialog.dismiss();
                                                                        dialog.dismiss();
                                                                        Intent intent = new Intent(mContext, AddProductActivity.class);
                                                                        intent.putExtra("name", crops.getCrop_name());
                                                                        mContext.startActivity(intent);

                                                                    }
                                                                }
                                                            }
                                                        });



                                                    }
                                                });
                                            }
                                        });
                                builder.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();
                                return true;
                            case R.id.report:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                                builder1.setMessage("Do you want to report failure of crop ?");
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
                                                cropMap.put("failure", true);


                                                db.collection("Crops").document(crops.getCrop_id()).update(cropMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        db.collection("Crops").document(crops.getCrop_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    final DocumentSnapshot document1 = task.getResult();
                                                                    if (document1.exists()) {

                                                                        final Crops crops = document1.toObject(Crops.class);


                                                                        Toast.makeText(mContext, "Saved Successfully.", Toast.LENGTH_SHORT).show();
                                                                        mProgressDialog.dismiss();
                                                                        dialog.dismiss();
                                                                        Intent intent = new Intent(mContext, AddReportActivity.class);
                                                                        intent.putExtra("name", crops.getCrop_name());
                                                                        mContext.startActivity(intent);

                                                                    }
                                                                }
                                                            }
                                                        });



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
                                return true;
                            case R.id.delete:
                                AlertDialog.Builder builderd = new AlertDialog.Builder(mContext);
                                builderd.setMessage("Do you want to delete this Product ?");
                                builderd.setCancelable(true);

                                builderd.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog, int id) {

                                                mProgressDialog = new ProgressDialog(mContext);
                                                mProgressDialog.setTitle("Delete");
                                                mProgressDialog.setMessage("deleting...");
                                                mProgressDialog.show();

                                                db.collection("Crops").document(crops.getCrop_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){

                                                            Toast.makeText(mContext, "Crop Deleted!", Toast.LENGTH_SHORT).show();
                                                            mContext.startActivity(new Intent(mContext, MainActivity.class));
                                                            mProgressDialog.dismiss();
                                                        }
                                                    }
                                                });

                                            }
                                        });

                                builderd.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert1d = builderd.create();
                                alert1d.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.crop_menu);
                if (crops.getLevel()!=0) {
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                if (crops.getLevel()==4){
                    popupMenu.getMenu().findItem(R.id.sell).setVisible(false);
                }
                if (crops.isFailure()){
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                }
                popupMenu.show();
            }
        });

        if (crops.getLevel()==1){
            holder.Level_1.setCompoundDrawablesWithIntrinsicBounds(null,null,mContext.getDrawable(R.drawable.level_done),null);
        }else if (crops.getLevel()==2){
            holder.Level_1.setCompoundDrawablesWithIntrinsicBounds(null,null,mContext.getDrawable(R.drawable.level_done),null);
            holder.Level_2.setCompoundDrawablesWithIntrinsicBounds(null,null,mContext.getDrawable(R.drawable.level_done),null);
        }else{
            holder.Level_1.setCompoundDrawablesWithIntrinsicBounds(null,null,mContext.getDrawable(R.drawable.level_done),null);
            holder.Level_2.setCompoundDrawablesWithIntrinsicBounds(null,null,mContext.getDrawable(R.drawable.level_done),null);
            holder.Level_3.setCompoundDrawablesWithIntrinsicBounds(null,null,mContext.getDrawable(R.drawable.level_done),null);
        }
        holder.Level_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PHResultActivity.class);
                intent.putExtra("crop_key", crops.getCrop_id());
                mContext.startActivity(intent);
            }
        });

        holder.Level_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CropResultActivity.class);
                intent.putExtra("crop_key", crops.getCrop_id());
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (crops.getLevel()==1){
                    Intent intent = new Intent(mContext, AddCropActivity.class);
                    intent.putExtra("ph_value", crops.getPh_value());
                    intent.putExtra("crop_key", crops.getCrop_id());
                    mContext.startActivity(intent);
                }else if (crops.getLevel()==2){
                    Intent intent = new Intent(mContext, CropDetailsActivity.class);
                    intent.putExtra("crop_key", crops.getCrop_id());
                    intent.putExtra("crop_model", crops.getCrop_disease_model());
                    mContext.startActivity(intent);
                }else{
                    Toast.makeText(mContext, "You have Harvested the crop!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mCrop.size();
    }


    public class CropHolder extends RecyclerView.ViewHolder {

        public TextView Crop_Name, Desc, Level_1, Level_2, Level_3;
        public CircleImageView Crop_Image;
        public ImageView More;

        public CropHolder(View itemView) {
            super(itemView);

            Crop_Name = itemView.findViewById(R.id.crop_name);
            Desc = itemView.findViewById(R.id.description);
            Level_1 = itemView.findViewById(R.id.level_1);
            Level_2 = itemView.findViewById(R.id.level_2);
            Level_3 = itemView.findViewById(R.id.level_3);
            Crop_Image = itemView.findViewById(R.id.crop_image);
            More = itemView.findViewById(R.id.crop_more);
        }
    }
}