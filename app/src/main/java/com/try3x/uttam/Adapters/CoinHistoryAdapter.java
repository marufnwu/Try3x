package com.try3x.uttam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.try3x.uttam.Models.CoinHistory;
import com.try3x.uttam.R;

import java.util.List;

public class CoinHistoryAdapter extends RecyclerView.Adapter<CoinHistoryAdapter.MyViewHolder> {
    Context context;
    List<CoinHistory> coinHistories;

    public CoinHistoryAdapter(Context context, List<CoinHistory> coinHistories) {
        this.context = context;
        this.coinHistories = coinHistories;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_coin_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CoinHistory coinHistory = coinHistories.get(position);
        if (coinHistory.trans_type==0){
            //debited
            holder.imgPosNeg.setImageResource(R.drawable.coin_remove);
        }else {
            //credited
            holder.imgPosNeg.setImageResource(R.drawable.coin_add);

        }

        holder.txtComment.setText(coinHistory.comment);
        holder.txtAmount.setText(String.valueOf(coinHistory.amount));
        holder.txtDate.setText(coinHistory.getDate());
        holder.txtTime.setText(coinHistory.getTime());
    }

    @Override
    public int getItemCount() {
        return coinHistories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPosNeg;
        TextView txtDate, txtTime, txtAmount, txtComment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPosNeg = itemView.findViewById(R.id.imgTransType);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtComment = itemView.findViewById(R.id.txtComment);

        }
    }
}
