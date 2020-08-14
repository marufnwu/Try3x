package com.try3x.uttam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.try3x.uttam.Models.PayMethodInfo;
import com.try3x.uttam.R;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.MyViewHolder> {

    Context ctx;
    List<PayMethodInfo> payMethodInfoList;
    OnPayMethodEditClickListener onPayMethodEditClickListener;

    public interface OnPayMethodEditClickListener{
        void onClick(int pos, PayMethodInfo payMethodInfo);
    }

    public OnPayMethodEditClickListener getOnPayMethodEditClickListener() {
        return onPayMethodEditClickListener;
    }

    public void setOnPayMethodEditClickListener(OnPayMethodEditClickListener onPayMethodEditClickListener) {
        this.onPayMethodEditClickListener = onPayMethodEditClickListener;
    }

    public PaymentMethodAdapter(Context ctx, List<PayMethodInfo> payMethodInfoList) {
        this.ctx = ctx;
        this.payMethodInfoList = payMethodInfoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.layout_pay_method_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PayMethodInfo payMethodInfo = payMethodInfoList.get(position);
        holder.txtMethodName.setText(payMethodInfo.getPay_method_name());
        holder.txtMethodNum.setText(payMethodInfo.getPay_number());
    }

    @Override
    public int getItemCount() {
        return payMethodInfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMethodName, txtMethodNum;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMethodName = itemView.findViewById(R.id.txtMethodName);
            txtMethodNum = itemView.findViewById(R.id.txtMethodNum);
        }
    }
}
