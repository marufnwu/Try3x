package com.try3x.uttam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.try3x.uttam.Listener.OnPackageItemClickListener;
import com.try3x.uttam.Models.PackageList;
import com.try3x.uttam.Models.Packages;
import com.try3x.uttam.R;

import java.util.List;

public class BajiPlaceListAdapter extends RecyclerView.Adapter<BajiPlaceListAdapter.MyViewHolder> {

    Context ctx;
    List<Packages> packageList;
    OnPackageItemClickListener onPackageItemClickListener;

    public BajiPlaceListAdapter(Context ctx, List<Packages> packageList, OnPackageItemClickListener onPackageItemClickListener) {
        this.ctx = ctx;
        this.packageList = packageList;
        this.onPackageItemClickListener = onPackageItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.layout_baji_final_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Packages packages = packageList.get(position);
        int pos = position+1;
        holder.txtSl.setText(""+pos);
        holder.txtName.setText(packages.btn);
        holder.txtPrice.setText(packages.price+" Rp.");

       holder.imgRemove.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               onPackageItemClickListener.onItemClick(position, packages.id, packages.price, "name", 0);
           }
       });

    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtSl, txtName, txtPrice;
        LinearLayout layoutParrent;
        ImageView imgRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSl = itemView.findViewById(R.id.txtSl);
            txtName = itemView.findViewById(R.id.txtPackName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            layoutParrent = itemView.findViewById(R.id.layoutParrent);

            imgRemove = itemView.findViewById(R.id.imgRemove);
        }
    }
}
