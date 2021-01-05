package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.try3x.uttam.Adapters.YtVideoAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Models.MyBajiList;
import com.try3x.uttam.Models.YtApi.YtPlaylist;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YtVideoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String pageToken = "";
    int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    LinearLayoutManager linearLayoutManager;
    private int PAGE = 1, PAGE_SIZE = 30, TOTAL_PAGE = 0;
    boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yt_video);

        recyclerView = findViewById(R.id.recyYtVideo);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        getYtVideo();
    }

//    private void initRecyclerPagination() {
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                visibleItemCount = linearLayoutManager.getChildCount();
//                totalItemCount = linearLayoutManager.getItemCount();
//                pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
//
//                if (dy>0){
//
//                    Log.d("Pagination", "Scrolled");
//
//                    if (!isLoading ){
//
//                        if (PAGE <= TOTAL_PAGE){
//                            Log.d("Pagination", "Total Page "+TOTAL_PAGE);
//                            Log.d("Pagination", "Page "+PAGE);
//                            if ( (visibleItemCount + pastVisibleItem) >= totalItemCount)
//                            {
//                                postListProgress.setVisibility(View.VISIBLE);
//                                isLoading = true;
//                                Log.v("...", "Last Item Wow !");
//                                //Do pagination.. i.e. fetch new data
//                                PAGE++;
//                                if (PAGE<=TOTAL_PAGE){
//                                    doPagination();
//                                }else {
//                                    isLoading = false;
//                                    postListProgress.setVisibility(View.GONE);
//                                }
//                            }
//                        }else{
//
//                            //postListProgress.setVisibility(View.GONE);
//                            Log.d("Pagination", "End of page");
//                        }
//                    }else {
//                        Log.d("Pagination", "Loading");
//
//                    }
//                }
//            }
//        });
//    }

    private void getYtVideo() {
        String apiKey = "AIzaSyD8DO87UJR5YGOsukmWnw2FIzxi0wpSG80";
        String playListid = "PLd6wr5bh4evPd_lpwDvOR1EBAsSPpXmUy";
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getYtPlaylist(playListid, apiKey, "snippet", 30, "")
                .enqueue(new Callback<YtPlaylist>() {
                    @Override
                    public void onResponse(Call<YtPlaylist> call, Response<YtPlaylist> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            YtVideoAdapter ytVideoAdapter = new YtVideoAdapter(getApplicationContext(), response.body().getItems());
                            recyclerView.setAdapter(ytVideoAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<YtPlaylist> call, Throwable t) {

                    }
                });
    }
}