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

import com.bumptech.glide.Glide;
import com.try3x.uttam.Models.Result;
import com.try3x.uttam.R;

import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {

    List<Result> results;
    Context context;

    public ResultListAdapter(List<Result> results, Context context) {
        this.results = results;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_result_item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result result = results.get(position);
        holder.txtDate.setText(result.date);
        holder.txtBajiNo.setText(result.baji_no);

        if (result.result_published){
            holder.txtPublished.setText("Yes");
            holder.layoutMain.setBackground(context.getDrawable(R.drawable.bg_gra_orange));
            if (result.win_btn!=null){
                if (result.win_btn.equals("btn1")){
                    holder.txtBtn.setImageDrawable(context.getDrawable(R.drawable.heart));
                }else if (result.win_btn.equals("btn2")){
                    holder.txtBtn.setImageDrawable(context.getDrawable(R.drawable.speads));
                }else if( result.win_btn.equals("btn3")){
                    holder.txtBtn.setImageDrawable(context.getDrawable(R.drawable.diamond));
                }else if (result.win_btn.equals("btn4")){
                    holder.txtBtn.setImageDrawable(context.getDrawable(R.drawable.club));
                }else{
                    if (result.iconUri!=null){
                        Glide.with(context)
                                .load(result.iconUri)
                                .into(holder.txtBtn);
                    }
                }
            }else {
                holder.txtBtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_cancel_24));
            }
        }else {
            holder.txtPublished.setText("No");
            holder.txtBtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_cancel_24));

        }

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtBajiNo, txtPublished; ImageView txtBtn;
        LinearLayout layoutMain;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtDate);
            txtBajiNo = itemView.findViewById(R.id.txtBajiNo);
            txtPublished = itemView.findViewById(R.id.txtPublished);
            txtBtn = itemView.findViewById(R.id.txtBtn);
            layoutMain = itemView.findViewById(R.id.layoutMain);
        }
    }
}
