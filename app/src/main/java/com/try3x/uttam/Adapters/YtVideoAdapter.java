package com.try3x.uttam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.try3x.uttam.Models.YtApi.ItemsItem;
import com.try3x.uttam.R;

import java.util.List;

public class YtVideoAdapter extends RecyclerView.Adapter<YtVideoAdapter.MyViewHolder> {
    Context context;
    List<ItemsItem> items;

    public YtVideoAdapter(Context context, List<ItemsItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_yt_video, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ItemsItem item = items.get(position);

        if(item.getSnippet().getThumbnails().getHigh()!=null){
            Glide.with(context)
                    .load(item.getSnippet().getThumbnails().getHigh().getUrl())
                    .into(holder.thumbnail);
        }

        holder.txtTitle.setText(item.getSnippet().getTitle());

        if(item.getSnippet().getResourceId().getVideoId()!=null){
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://www.youtube.com/watch?v="+item.getSnippet().getResourceId().getVideoId());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.google.android.youtube");
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView txtTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
