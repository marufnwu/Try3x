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
import com.try3x.uttam.Models.Commission;
import com.try3x.uttam.R;

import java.util.List;

public class CommissionHistoryAdapter extends RecyclerView.Adapter<CommissionHistoryAdapter.MyViewHolder> {
    Context context;
    List<Commission> commissionList;

    public CommissionHistoryAdapter(Context context, List<Commission> commissionList) {
        this.context = context;
        this.commissionList = commissionList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_coin_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Commission commission = commissionList.get(position);
        if (commission.trans_type==0){
            //debited
            holder.imgPosNeg.setImageResource(R.drawable.coin_remove);
        }else {
            //credited
            holder.imgPosNeg.setImageResource(R.drawable.coin_add);

        }

        holder.txtComment.setText(commission.coinByUserName+" "+commission.comment);
        holder.txtAmount.setText(String.valueOf(commission.amount));
        holder.txtDate.setText(commission.getDate());
        holder.txtTime.setText(commission.getTime());
    }

    @Override
    public int getItemCount() {
        return commissionList.size();
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
