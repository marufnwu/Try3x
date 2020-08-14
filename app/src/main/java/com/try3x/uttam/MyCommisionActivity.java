package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.try3x.uttam.Adapters.CommissionHistoryAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.Response.MyCoinResponse;
import com.try3x.uttam.Models.Response.MyCommissionsResponse;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCommisionActivity extends AppCompatActivity {

    TextView txtCoin;
    ImageView imgReload;
    GmailInfo gmailInfo;
    FirebaseAuth mAuth;
    private ACProgressPie dialog;
    private LinearLayout reloadLay;
    private RecyclerView recyclerCoin;

    int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;

    private int PAGE = 1, PAGE_SIZE = 30, TOTAL_PAGE = 0;
    boolean isLoading = true;
    private ProgressBar postListProgress;
    private LinearLayoutManager layoutManager;
    private MyCommissionsResponse commissionsResponse;
    private CommissionHistoryAdapter commissionHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commision);

        initviews();
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();
        createDialog();
        initRecyclerPagination();
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);

        getCommissionList();
    }


    private void getCommissionList() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getCommissions(Common.getKeyHash(
                        MyCommisionActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<MyCommissionsResponse>() {
                    @Override
                    public void onResponse(Call<MyCommissionsResponse> call, Response<MyCommissionsResponse> response) {
                        if (response.isSuccessful()&& response.body()!=null){
                            commissionsResponse = response.body();
                            TOTAL_PAGE = commissionsResponse.totalPage;
                            if (!commissionsResponse.error){
                                isLoading = false;

                                if (commissionsResponse.items.size()>0){
                                    Toast.makeText(MyCommisionActivity.this, "Ok", Toast.LENGTH_SHORT).show();
                                    commissionHistoryAdapter = new CommissionHistoryAdapter(MyCommisionActivity.this, commissionsResponse.items);
                                    recyclerCoin.setAdapter(commissionHistoryAdapter);
                                    reloadLay.setVisibility(View.GONE);
                                    recyclerCoin.setVisibility(View.VISIBLE);
                                }else {
                                    reloadLay.setVisibility(View.VISIBLE);
                                    recyclerCoin.setVisibility(View.GONE);
                                }
                                dismissWaitingDialog();

                            }else {
                                isLoading = false;
                                reloadLay.setVisibility(View.VISIBLE);
                                recyclerCoin.setVisibility(View.GONE);
                                dismissWaitingDialog();
                                Toast.makeText(MyCommisionActivity.this, ""+ commissionsResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            isLoading = false;
                            reloadLay.setVisibility(View.VISIBLE);
                            recyclerCoin.setVisibility(View.GONE);
                            dismissWaitingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyCommissionsResponse> call, Throwable t) {
                        reloadLay.setVisibility(View.VISIBLE);
                        recyclerCoin.setVisibility(View.GONE);
                        dismissWaitingDialog();
                    }
                });

    }

    private void initviews() {
        txtCoin  = findViewById(R.id.txtCoin);
        imgReload = findViewById(R.id.imgReload);

        reloadLay = findViewById(R.id.layoutReload);
        recyclerCoin = findViewById(R.id.recyclerCoin);
        recyclerCoin.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerCoin.setLayoutManager(layoutManager);

        postListProgress = findViewById(R.id.progress);

    }


    private void initRecyclerPagination() {

        recyclerCoin.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (dy>0){

                    Log.d("Pagination", "Scrolled");

                    if (!isLoading ){

                        if (PAGE <= TOTAL_PAGE){
                            Log.d("Pagination", "Total Page "+TOTAL_PAGE);
                            Log.d("Pagination", "Page "+PAGE);
                            if ( (visibleItemCount + pastVisibleItem) >= totalItemCount)
                            {
                                postListProgress.setVisibility(View.VISIBLE);
                                isLoading = true;
                                Log.v("...", "Last Item Wow !");
                                //Do pagination.. i.e. fetch new data
                                PAGE++;
                                if (PAGE<=TOTAL_PAGE){
                                    doPagination();
                                }else {
                                    isLoading = false;
                                    postListProgress.setVisibility(View.GONE);
                                }
                            }
                        }else{

                            //postListProgress.setVisibility(View.GONE);
                            Log.d("Pagination", "End of page");
                        }
                    }else {
                        Log.d("Pagination", "Loading");

                    }
                }
            }
        });
    }

    private void doPagination() {
        postListProgress.setVisibility(View.VISIBLE);

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getCommissions(
                        Common.getKeyHash(
                                MyCommisionActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<MyCommissionsResponse>() {
                    @Override
                    public void onResponse(Call<MyCommissionsResponse> call, Response<MyCommissionsResponse> response) {

                        if (response.isSuccessful()&& response.body()!=null){
                            MyCommissionsResponse coinHistoryResponse1 = response.body();
                            if (!coinHistoryResponse1.error){
                                PAGE_SIZE = coinHistoryResponse1.totalPage;
                                if (coinHistoryResponse1.items.size()>0){
                                    isLoading = false;

                                    commissionsResponse.items.addAll(coinHistoryResponse1.items);
                                    commissionHistoryAdapter.notifyDataSetChanged();
                                }

                            }else {
                                isLoading = false;

                                Toast.makeText(MyCommisionActivity.this, ""+coinHistoryResponse1.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            isLoading = false;

                        }
                    }

                    @Override
                    public void onFailure(Call<MyCommissionsResponse> call, Throwable t) {
                        Log.d("RetrofitError", t.getMessage());
                        isLoading = false;
                        postListProgress.setVisibility(View.GONE);
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (gmailInfo==null){
            mAuth.signOut();
            Toast.makeText(this, "Login Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }

        getMyCommission();
    }

    private void createDialog() {
        dialog = new ACProgressPie.Builder(this)
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

    private void getMyCommission() {
        startSpinReload(imgReload);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getCommission(
                        Common.getKeyHash(MyCommisionActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token
                )
                .enqueue(new Callback<MyCoinResponse>() {
                    @Override
                    public void onResponse(Call<MyCoinResponse> call, Response<MyCoinResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            MyCoinResponse myCoinResponse = response.body();
                            if (!myCoinResponse.isError()){
                                stopSpinReload(imgReload);
                                txtCoin.setText(String.valueOf(myCoinResponse.coin));
                            }else {
                                stopSpinReload(imgReload);
                                Toast.makeText(MyCommisionActivity.this, myCoinResponse.getError_description(), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            stopSpinReload(imgReload);
                            Toast.makeText(MyCommisionActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyCoinResponse> call, Throwable t) {
                        stopSpinReload(imgReload);
                        Toast.makeText(MyCommisionActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startSpinReload(ImageView reloadImage){
        txtCoin.setText("00");
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        reloadImage.startAnimation(rotateAnimation);
    }

    private void stopSpinReload(ImageView reloadImage){
        reloadImage.getAnimation().cancel();
    }
}