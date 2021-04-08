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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.AddCropActivity;
import com.projectfarmer.CropDetailsActivity;
import com.projectfarmer.CropResultActivity;
import com.projectfarmer.MainActivity;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.Models.Products;
import com.projectfarmer.PHResultActivity;
import com.projectfarmer.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.CropHolder> {

    private Context mContext;
    private List<Products> mProducts;

    private ProgressDialog mProgressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProductsAdapter(Context context, List<Products> products){
        mContext = context;
        mProducts = products;
    }

    @Override
    public CropHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_product_layout, parent, false);
        return new CropHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CropHolder holder, final int position) {

        final Products products = mProducts.get(position);


        holder.Name.setText(products.getName());
        holder.Price.setText("\u20B9"+products.getPrice());
        Glide.with(mContext).load(products.getImage()).into(holder.Image);



    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }


    public class CropHolder extends RecyclerView.ViewHolder {

        public TextView Name, Price;
        public CircleImageView Image;

        public CropHolder(View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.name);
            Price = itemView.findViewById(R.id.price);
            Image = itemView.findViewById(R.id.image);
        }
    }
}