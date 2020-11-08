package com.try3x.uttam.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Listener.OnClaimClickListener;
import com.try3x.uttam.Models.Baji;
import com.try3x.uttam.R;

import java.util.List;

public class MyBajiListAdapter extends RecyclerView.Adapter<MyBajiListAdapter.MyViewHolder> {

    Context ctx;
    List<Baji> bajiList;
    OnClaimClickListener onClaimClickListener;

    public MyBajiListAdapter(Context ctx, List<Baji> bajiList, OnClaimClickListener onClaimClickListener) {
        this.ctx = ctx;
        this.bajiList = bajiList;
        this.onClaimClickListener = onClaimClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.layout_baji_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Baji baji = bajiList.get(position);
        Log.d("Price", ""+baji.package_price);

        holder.txtDate.setText(Common.date(baji.date));
        holder.txtBajiNo.setText(""+baji.baji_no);

        if (baji.result_published){
            holder.imgResultPub.setImageResource(R.drawable.ic_baseline_check_circle_24);

           if (baji.win_btn!=null){
               if (baji.win_btn.equals(baji.btn)){
                   //win
                   holder.imgWin.setImageResource(R.drawable.ic_baseline_check_circle_24);
                   if (!baji.claim){
                       holder.imgClaim.setVisibility(View.GONE);
                       holder.txtClaim.setVisibility(View.VISIBLE);

                   }else {
                       holder.imgClaim.setImageResource(R.drawable.ic_baseline_check_circle_24);

                       holder.imgClaim.setVisibility(View.VISIBLE);

                       holder.txtClaim.setVisibility(View.GONE);
                   }
               }else {
                   holder.imgWin.setImageResource(R.drawable.ic_baseline_cancel_24);

                   holder.imgClaim.setImageResource(R.drawable.ic_baseline_cancel_24);

                   holder.imgClaim.setVisibility(View.VISIBLE);

                   holder.txtClaim.setVisibility(View.GONE);
               }
           }else {
               holder.imgWin.setImageResource(R.drawable.ic_baseline_cancel_24);

               holder.imgClaim.setImageResource(R.drawable.ic_baseline_cancel_24);

               holder.imgClaim.setVisibility(View.VISIBLE);

               holder.txtClaim.setVisibility(View.GONE);

           }
        }else {
            holder.imgResultPub.setImageResource(R.drawable.ic_baseline_cancel_24);
            holder.imgWin.setImageResource(R.drawable.ic_baseline_cancel_24);
            holder.imgClaim.setImageResource(R.drawable.ic_baseline_cancel_24);

            holder.imgClaim.setVisibility(View.VISIBLE);

            holder.txtClaim.setVisibility(View.GONE);


        }

        holder.txtPrice.setText(String.valueOf(baji.package_price));

        holder.txtClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClaimClickListener.onClick(Integer.parseInt(baji.id), position);
                Log.d("idddd", baji.id);
            }
        });

        if (baji.btn.equals("btn1")){
            holder.txtBtn.setImageDrawable(ctx.getDrawable(R.drawable.heart));
        }else if (baji.btn.equals("btn2")){
            holder.txtBtn.setImageDrawable(ctx.getDrawable(R.drawable.speads));
        }else if (baji.btn.equals("btn3")){
            holder.txtBtn.setImageDrawable(ctx.getDrawable(R.drawable.diamond));
        }else if (baji.btn.equals("btn4")){
            holder.txtBtn.setImageDrawable(ctx.getDrawable(R.drawable.club));
        }else {
            if (baji.btn_icon!=null){
                Glide.with(ctx).load(baji.btn_icon).into(holder.txtBtn);
            }
        }
        //holder.txtPack.setText(baji.package_name);

    }

    @Override
    public int getItemCount() {
        return bajiList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtPrice, txtBajiNo, txtClaim, txtPack;
        ImageView imgWin, imgResultPub, imgClaim, txtBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtDate);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtBajiNo = itemView.findViewById(R.id.txtBajiNo);
            txtClaim = itemView.findViewById(R.id.txtClaim);
            txtBtn = itemView.findViewById(R.id.txtBtn);

            imgWin = itemView.findViewById(R.id.imgWin);
            imgResultPub = itemView.findViewById(R.id.imgResultPub);
            imgClaim = itemView.findViewById(R.id.imgClaim);
            //txtPack = itemView.findViewById(R.id.txtPack);
        }
    }
}
