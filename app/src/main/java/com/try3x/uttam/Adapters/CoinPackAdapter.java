package com.try3x.uttam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.try3x.uttam.Listener.OnCoinPackClickListener;
import com.try3x.uttam.Models.CoinPackage;
import com.try3x.uttam.R;

import java.util.List;

public class CoinPackAdapter extends RecyclerView.Adapter<CoinPackAdapter.MyViewHolder> {

    int selected = -1;

    Context ctx;
    List<CoinPackage> coinPackageList;
    OnCoinPackClickListener coinPackClickListener;

    public CoinPackAdapter(Context ctx, List<CoinPackage> coinPackageList, OnCoinPackClickListener coinPackClickListener) {
        this.ctx = ctx;
        this.coinPackageList = coinPackageList;
        this.coinPackClickListener = coinPackClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.layout_coin_pack_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final CoinPackage coinPackage = coinPackageList.get(position);
        holder.txtName.setText(coinPackage.name);
        holder.txtCoin.setText(String.valueOf(coinPackage.coin));
        holder.txtPrice.setText(String.valueOf(coinPackage.price));

        if (selected == position){
            holder.layoutParrent.setBackground(ctx.getDrawable(R.drawable.bg_recy_item_select));
        }else {
            holder.layoutParrent.setBackground(ctx.getDrawable(R.drawable.bg_gra_buy_coin));
        }

        holder.layoutParrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinPackClickListener.onClick(coinPackage, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coinPackageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtPrice, txtCoin;
        LinearLayout layoutParrent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCoin = itemView.findViewById(R.id.txtCoin);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtName = itemView.findViewById(R.id.txtName);
            layoutParrent = itemView.findViewById(R.id.layoutParrent);
        }
    }

    public void selectItem(int pos){
        selected = pos;
        notifyDataSetChanged();
    }
}
