package com.try3x.uttam.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Models.Response.TransactionResponse;
import com.try3x.uttam.Models.Transaction;
import com.try3x.uttam.R;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;
import com.try3x.uttam.TransactionActivity;

import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    Context context;
    List<Transaction> transactions;
    private ACProgressPie dialog;

    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
        createDialog(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_transaction_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Transaction transaction = transactions.get(position);
        if (transaction.status==1){
            holder.imgRecheck.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_done_24));
        }else if (transaction.status==2){
            holder.imgRecheck.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_cancel_24));
        }else {
            holder.imgRecheck.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_refresh_24));
        }

        holder.txtDate.setText(transaction.date);
        holder.txtTime.setText(transaction.time);
        holder.txtRupee.setText(String.valueOf(transaction.price));
        holder.txtCoin.setText(transaction.coin);
        holder.txtOrderid.setText(transaction.id);

        holder.txtMsg.setText(transaction.statusMsg);

        holder.imgRecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (transaction.status==1){
                    Toast.makeText(context, "Coin Already Added", Toast.LENGTH_SHORT).show();
                }else if(transaction.status==2){
                    Toast.makeText(context, "Order Closed", Toast.LENGTH_SHORT).show();
                }else {
                    showWaitingDialog();
                    RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                            .paytmPaymentStatus(
                                    transaction.id
                            ).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            dismissWaitingDialog();
                            Toast.makeText(context, "Issue Submitted, If payment success coin will add shortly", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            dismissWaitingDialog();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtTime, txtRupee, txtCoin, txtOrderid, txtMsg;
        ImageView imgRecheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtRupee = itemView.findViewById(R.id.txtRupee);
            txtCoin = itemView.findViewById(R.id.txtCoin);
            txtOrderid = itemView.findViewById(R.id.txtOrderid);
            txtMsg = itemView.findViewById(R.id.txtMsg);
            imgRecheck = itemView.findViewById(R.id.imgRecheck);
        }
    }

    private void createDialog(Context context) {
        dialog = new ACProgressPie.Builder(context)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build();
    }

    private void showWaitingDialog() {
        if (dialog!=null && !dialog.isShowing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
