package com.try3x.uttam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.try3x.uttam.Models.ReferUser;
import com.try3x.uttam.R;

import java.util.List;

public class ReferUserListAdapter extends RecyclerView.Adapter<ReferUserListAdapter.MyViewHolder> {
    Context context;
    List<ReferUser> referUsers;
    OnRedemReferClickListener onRedemReferClickListener;
    public ReferUserListAdapter(Context context, List<ReferUser> referUsers) {
        this.context = context;
        this.referUsers = referUsers;
    }

    public OnRedemReferClickListener getOnRedemReferClickListener() {
        return onRedemReferClickListener;
    }

    public void setOnRedemReferClickListener(OnRedemReferClickListener onRedemReferClickListener) {
        this.onRedemReferClickListener = onRedemReferClickListener;
    }

    public interface OnRedemReferClickListener{
        void OnClick(String referId, int pos);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_refer_users, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ReferUser referUser = referUsers.get(position);
        holder.txtDate.setText(referUser.date);
        holder.txtName.setText(referUser.name);
        holder.txtAmount.setText(String.valueOf(referUser.amount+(" (Max)")));
        if (referUser.claim){
            holder.txtClaim.setVisibility(View.GONE);
            holder.imgClaim.setVisibility(View.VISIBLE);
        }else {
            holder.txtClaim.setVisibility(View.VISIBLE);
            holder.imgClaim.setVisibility(View.GONE);

            holder.txtClaim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRedemReferClickListener.OnClick(referUser.id, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return referUsers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtName, txtAmount, txtClaim;
        ImageView imgClaim;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtName = itemView.findViewById(R.id.txtName);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtClaim = itemView.findViewById(R.id.txtClaim);
            imgClaim = itemView.findViewById(R.id.imgClaim);
        }
    }
}
