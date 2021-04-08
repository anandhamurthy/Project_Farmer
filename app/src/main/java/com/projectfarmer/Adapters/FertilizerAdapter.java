package com.projectfarmer.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.AddCropActivity;
import com.projectfarmer.CropDetailsActivity;
import com.projectfarmer.CropResultActivity;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.Models.Fertilizer;
import com.projectfarmer.PHResultActivity;
import com.projectfarmer.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FertilizerAdapter extends RecyclerView.Adapter<FertilizerAdapter.CropHolder> {

    private Context mContext;
    private List<Fertilizer> mFertilizer;

    private ProgressDialog mProgressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FertilizerAdapter(Context context, List<Fertilizer> fertilizers){
        mContext = context;
        mFertilizer = fertilizers;
    }

    @Override
    public CropHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_fertilizer_layout, parent, false);
        return new CropHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CropHolder holder, final int position) {

        final Fertilizer fertilizer = mFertilizer.get(position);
        holder.Name.setText(fertilizer.getName());
        holder.Material.setText(fertilizer.getMaterial());
        Glide.with(mContext).load(fertilizer.getImage()).into(holder.Image);

    }

    @Override
    public int getItemCount() {
        return mFertilizer.size();
    }


    public class CropHolder extends RecyclerView.ViewHolder {

        public TextView Name, Material;
        public CircleImageView Image;

        public CropHolder(View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.name);
            Material = itemView.findViewById(R.id.material);
            Image = itemView.findViewById(R.id.image);
        }
    }
}