package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.try3x.uttam.Adapters.CoinHistoryAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Models.ActivityBanner;
import com.try3x.uttam.Models.Response.CoinHistoryResponse;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.Response.MyCoinResponse;
import com.try3x.uttam.Models.User;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCoinActivity extends AppCompatActivity {

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
    private CoinHistoryResponse coinHistoryResponses;
    private CoinHistoryAdapter coinHistoryAdapter;
    private TextView txtAddCoin;
    private Button btnInvite, btnCopy, btnMyTransaction;
    private boolean isActivityCreatedByNoti;
    private ImageView imgLiveChat;
    private ImageView imgBanner;
    private TextView edtReferCode;
    private User userProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coin);
        isActivityCreatedByNoti = getIntent().getBooleanExtra(Common.ACTIVITY_CREATED_BY_NOTI, false);
        initviews();

        Paper.init(this);
        userProfile = Paper.book().read(PaperDB.USER_PROFILE);
        mAuth = FirebaseAuth.getInstance();
        createDialog();
        initRecyclerPagination();
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);

        //getCoinList();


        //requestPayment();
        setReferCode();
        getBanner();


    }

    private void setReferCode() {
        if (userProfile!=null && userProfile.referCode!=null){
            edtReferCode.setText(userProfile.referCode);

            btnCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stringYouExtracted = edtReferCode.getText().toString();
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Refer Code", stringYouExtracted);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MyCoinActivity.this, "Refer Code Copied", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void getCoinList() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getCoinHistory(Common.getKeyHash(
                        MyCoinActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<CoinHistoryResponse>() {
                    @Override
                    public void onResponse(Call<CoinHistoryResponse> call, Response<CoinHistoryResponse> response) {
                        if (response.isSuccessful()&& response.body()!=null){
                            coinHistoryResponses = response.body();
                            TOTAL_PAGE = coinHistoryResponses.totalPage;
                            if (!coinHistoryResponses.error){
                                isLoading = false;

                                if (coinHistoryResponses.items.size()>0){
                                    coinHistoryAdapter = new CoinHistoryAdapter(MyCoinActivity.this, coinHistoryResponses.items);
                                    recyclerCoin.setAdapter(coinHistoryAdapter);
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
                                Toast.makeText(MyCoinActivity.this, ""+coinHistoryResponses.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            isLoading = false;
                            reloadLay.setVisibility(View.VISIBLE);
                            recyclerCoin.setVisibility(View.GONE);
                            dismissWaitingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<CoinHistoryResponse> call, Throwable t) {
                        reloadLay.setVisibility(View.VISIBLE);
                        recyclerCoin.setVisibility(View.GONE);
                        dismissWaitingDialog();
                    }
                });

    }

    private void initviews() {
        txtCoin  = findViewById(R.id.txtCoin);
        imgReload = findViewById(R.id.imgReload);
        btnInvite = findViewById(R.id.btnInvite);

        reloadLay = findViewById(R.id.layoutReload);
        recyclerCoin = findViewById(R.id.recyclerCoin);
        recyclerCoin.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerCoin.setLayoutManager(layoutManager);
        recyclerCoin.setLayoutManager(layoutManager);


        postListProgress = findViewById(R.id.progress);
        txtAddCoin = findViewById(R.id.txtAddCoin);
        imgLiveChat = findViewById(R.id.imgLiveChat);
        imgBanner = findViewById(R.id.imgBanner);
        btnCopy = findViewById(R.id.btnCopy);
        edtReferCode = findViewById(R.id.edtReferCode);
        btnMyTransaction = findViewById(R.id.btnMyTransaction);


        txtAddCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyCoinActivity.this, AddCoinActivity.class));
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = Paper.book().read(PaperDB.USER_PROFILE);
                if (user!=null && user.referCode!=null){
                    invite(MyCoinActivity.this, user.referCode);
                }else {
                    Toast.makeText(MyCoinActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnMyTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
            }
        });

        Common.setShakeAnimation(imgLiveChat, getApplicationContext());
        imgLiveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.openLiveChat(getApplicationContext());
            }
        });
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
                .getCoinHistory(
                        Common.getKeyHash(
                                MyCoinActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<CoinHistoryResponse>() {
                    @Override
                    public void onResponse(Call<CoinHistoryResponse> call, Response<CoinHistoryResponse> response) {

                        if (response.isSuccessful()&& response.body()!=null){
                            CoinHistoryResponse coinHistoryResponse1 = response.body();
                            if (!coinHistoryResponse1.error){
                                PAGE_SIZE = coinHistoryResponse1.totalPage;
                                if (coinHistoryResponse1.items.size()>0){
                                    isLoading = false;

                                    coinHistoryResponses.items.addAll(coinHistoryResponse1.items);
                                    coinHistoryAdapter.notifyDataSetChanged();
                                }

                            }else {
                                isLoading = false;

                                Toast.makeText(MyCoinActivity.this, ""+coinHistoryResponse1.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            isLoading = false;

                        }
                    }

                    @Override
                    public void onFailure(Call<CoinHistoryResponse> call, Throwable t) {
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

        getMyCoin();
    }

    private void createDialog() {
        dialog = new ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build();
    }

    private void showWaitingDialog() {
        if (!isFinishing() &&dialog!=null && !dialog.isShowing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (!isFinishing() &&dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void getMyCoin() {
        startSpinReload(imgReload);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getCoin(
                        Common.getKeyHash(MyCoinActivity.this),
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
                                Toast.makeText(MyCoinActivity.this, myCoinResponse.getError_description(), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            stopSpinReload(imgReload);
                            Toast.makeText(MyCoinActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyCoinResponse> call, Throwable t) {
                        stopSpinReload(imgReload);
                        Toast.makeText(MyCoinActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startSpinReload(ImageView reloadImage){
       if (reloadImage!=null){
           txtCoin.setText("00");
           RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                   Animation.RELATIVE_TO_SELF, 0.5f,
                   Animation.RELATIVE_TO_SELF, 0.5f);

           rotateAnimation.setInterpolator(new LinearInterpolator());
           rotateAnimation.setDuration(500);
           rotateAnimation.setRepeatCount(Animation.INFINITE);

           reloadImage.startAnimation(rotateAnimation);
       }
    }

    private void stopSpinReload(ImageView reloadImage){
        if (reloadImage!=null && reloadImage.getAnimation()!=null){
            reloadImage.getAnimation().cancel();
        }
    }

    private void getBanner() {
        imgBanner.setVisibility(View.GONE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getActivityBanner("MyCoinActivity")
                .enqueue(new Callback<ActivityBanner>() {
                    @Override
                    public void onResponse(Call<ActivityBanner> call, Response<ActivityBanner> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            final ActivityBanner activityBanner = response.body();
                            if (!activityBanner.error){
                                if (activityBanner.imageUrl!=null){
                                    imgBanner.setVisibility(View.VISIBLE);
                                    if(!MyCoinActivity.this.isFinishing()){
                                        Glide.with(MyCoinActivity.this)
                                                .load(activityBanner.imageUrl)
                                                .into(imgBanner);

                                        imgBanner.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (activityBanner.actionType==1){
                                                    //open url
                                                    if (activityBanner.actionUrl!=null){
                                                        String url = activityBanner.actionUrl;
                                                        String linkHost = Uri.parse(url).getHost();
                                                        Uri uri = Uri.parse(url);

                                                        if (linkHost==null){
                                                            return;
                                                        }

                                                        if (linkHost.equals("play.google.com")){
                                                            String appId = uri.getQueryParameter("id");

                                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                                            intent.setData(Uri.parse("market://details?id="+appId));
                                                            startActivity(intent);

                                                        }else if(linkHost.equals("www.youtube.com")){
                                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.setPackage("com.google.android.youtube");
                                                            startActivity(intent);


                                                        }else if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                            startActivity(intent);

                                                        }
                                                    }
                                                }else if (activityBanner.actionType==2){
                                                    //open activity
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ActivityBanner> call, Throwable t) {

                    }
                });
    }

    public static void invite(Context context, String referCode){
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        String shareBody = "Use My Code To Refer Box => " +referCode+"\n"+
                "রেফার কোডে আমার কোডটি ব্যবহার করুন => " +referCode+"\n"+
                "\n" +
                "Download the app now=> https://www.try3x.com";
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /*Fire!*/
        context.startActivity(Intent.createChooser(intent, "Share Using"));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isActivityCreatedByNoti){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}