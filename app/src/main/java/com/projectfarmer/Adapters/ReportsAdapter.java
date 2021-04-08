package com.projectfarmer.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.Models.Products;
import com.projectfarmer.Models.Reports;
import com.projectfarmer.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.CropHolder> {

    private Context mContext;
    private List<Reports> mReports;

    private ProgressDialog mProgressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ReportsAdapter(Context context, List<Reports> reports){
        mContext = context;
        mReports = reports;
    }

    @Override
    public CropHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_report_layout, parent, false);
        return new CropHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CropHolder holder, final int position) {

        final Reports reports = mReports.get(position);


        holder.Name.setText(reports.getName());
        holder.Price.setText("\u20B9"+reports.getBudget());
        Glide.with(mContext).load(reports.getImage()).into(holder.Image);



    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }


    public class CropHolder extends RecyclerView.ViewHolder {

        public TextView Name, Price;
        public CircleImageView Image;

        public CropHolder(View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.name);
            Price = itemView.findViewById(R.id.desc);
            Image = itemView.findViewById(R.id.image);
        }
    }
}