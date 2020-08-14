package com.try3x.uttam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Models.PayoutHistory;
import com.try3x.uttam.R;

import java.util.List;

public class PayoutAdapter extends RecyclerView.Adapter<PayoutAdapter.ViewHolder> {
    Context ctx;
    List<PayoutHistory> payoutHistoryList;

    public PayoutAdapter(Context ctx, List<PayoutHistory> payoutHistoryList) {
        this.ctx = ctx;
        this.payoutHistoryList = payoutHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.layout_payout_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PayoutHistory payoutHistory = payoutHistoryList.get(position);
        holder.txtAmount.setText(String.valueOf(payoutHistory.getRupee()));
        holder.txtStatus.setText(Common.getPayoutStatus(payoutHistory.getStatus()));
        holder.txtComment.setText(payoutHistory.getDescription());
        holder.txtDate.setText(payoutHistory.getDate());
        holder.txtTime.setText(payoutHistory.getTime());
    }

    @Override
    public int getItemCount() {
        return payoutHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStatus;
        TextView txtDate, txtTime, txtAmount, txtComment, txtStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.imgTransType);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }

    public void updateItem(List<PayoutHistory> items){
        payoutHistoryList.addAll(items);
        notifyDataSetChanged();
    }

}
