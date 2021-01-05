package com.try3x.uttam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.try3x.uttam.Models.Btn;
import com.try3x.uttam.R;

import java.util.List;

public class BajiBtnAdapter extends RecyclerView.Adapter<BajiBtnAdapter.MyViewHolder> {
    Context context;
    List<Btn> btns;
    OnBtnClickListener onBtnClickListener;

    public BajiBtnAdapter(Context context, List<Btn> btns) {
        this.context = context;
        this.btns = btns;
    }

    public OnBtnClickListener getOnBtnClickListener() {
        return onBtnClickListener;
    }

    public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener) {
        this.onBtnClickListener = onBtnClickListener;
    }

    public interface OnBtnClickListener{
        void onBtnClick(int pos, Btn btn);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_baji_btn_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Btn btn = btns.get(position);
        Glide.with(context)
                .load(btn.iconUri)
                .into(holder.imgIcon);

        holder.btnAddCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnClickListener.onBtnClick(position, btn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return btns.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        Button btnAddCoin;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            btnAddCoin = itemView.findViewById(R.id.btnAddCoin);
        }
    }
}
