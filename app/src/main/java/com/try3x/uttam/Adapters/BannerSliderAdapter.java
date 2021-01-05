package com.try3x.uttam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.try3x.uttam.Models.Slide;
import com.try3x.uttam.R;

import java.util.ArrayList;
import java.util.List;

public class BannerSliderAdapter extends SliderViewAdapter<BannerSliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<Slide> mSliderItems = new ArrayList<>();


    public BannerSliderAdapter(Context context, List<Slide> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }



    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        return new SliderAdapterVH(LayoutInflater.from(context).inflate(R.layout.layout_slider_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        final Slide slide = mSliderItems.get(position);
        Glide.with(context)
                .load(slide.bannerUrl)
                .placeholder(R.drawable.imageloading)
                .into(viewHolder.imgBanner);

        viewHolder.imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAction(slide);
            }
        });

    }

    private void clickAction(Slide slide) {

        if (slide.actionType==1 && slide.actionUrl!=null ){
            String url = slide.actionUrl;
            String linkHost = Uri.parse(url).getHost();
            Uri uri = Uri.parse(url);

            if (linkHost==null){
                return;
            }

            if (linkHost.equals("play.google.com")){
                String appId = uri.getQueryParameter("id");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id="+appId));
                context.startActivity(intent);

            }else if(linkHost.equals("www.youtube.com")){
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                context.startActivity(intent);


            }else if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);

            }
        }


    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ImageView imgBanner;
        public SliderAdapterVH(View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
    }
}
