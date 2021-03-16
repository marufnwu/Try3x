package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.try3x.uttam.Adapters.CommissionHistoryAdapter;
import com.try3x.uttam.Adapters.ReferUserListAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Models.ActivityBanner;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.ReferUser;
import com.try3x.uttam.Models.Response.MyCoinResponse;
import com.try3x.uttam.Models.Response.MyCommissionsResponse;
import com.try3x.uttam.Models.Response.ReferUserListResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private  TextView txtAddToMycoin;
    int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;

    private int PAGE = 1, PAGE_SIZE = 30, TOTAL_PAGE = 0;
    boolean isLoading = true;
    private ProgressBar postListProgress;
    private LinearLayoutManager layoutManager;
    private MyCommissionsResponse commissionsResponse;
    private CommissionHistoryAdapter commissionHistoryAdapter;
    private ImageView imgBanner,imgLiveChat;
    private boolean isActivityCreatedByNoti;
    List<ReferUser> referUserList = new ArrayList<>();
    private ReferUserListAdapter referUserListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commision);
        isActivityCreatedByNoti = getIntent().getBooleanExtra(Common.ACTIVITY_CREATED_BY_NOTI, false);
        initviews();
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();
        createDialog();
        //initRecyclerPagination();
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);


        //getCommissionList();
        //sendReferClaim(" ", pos);
        getReferUserLsit();
        getBanner();
    }

    private void getReferUserLsit() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getReferUserList(
                        Common.getKeyHash(MyCommisionActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id

                )
                .enqueue(new Callback<ReferUserListResponse>() {
                    @Override
                    public void onResponse(Call<ReferUserListResponse> call, Response<ReferUserListResponse> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body()!=null){
                            ReferUserListResponse referUserResponse = response.body();
                            if (!referUserResponse.error) {
                                List<ReferUser> referUser = referUserResponse.users;
                                referUserList.addAll(referUser);
                                referUserListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReferUserListResponse> call, Throwable t) {
                        dismissWaitingDialog();
                    }
                });

    }

    private void getBanner() {
        imgBanner.setVisibility(View.GONE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getActivityBanner("MyCommisionActivity")
                .enqueue(new Callback<ActivityBanner>() {
                    @Override
                    public void onResponse(Call<ActivityBanner> call, Response<ActivityBanner> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            final ActivityBanner activityBanner = response.body();
                            if (!activityBanner.error){
                                if (activityBanner.imageUrl!=null){
                                    int val = new Random().nextInt(6-1)+1;
                                    int drawable = R.drawable.place1;
                                    switch (val){
                                        case 2:
                                            drawable = R.drawable.place2;
                                            break;
                                        case 3:
                                            drawable = R.drawable.place3;
                                            break;
                                        case 4:
                                            drawable = R.drawable.place4;
                                            break;
                                        case 5:
                                            drawable = R.drawable.place5;
                                            break;
                                        case 6:
                                            drawable = R.drawable.place6;
                                            break;
                                        default:
                                            drawable = R.drawable.place1;

                                    }
                                    imgBanner.setVisibility(View.VISIBLE);
                                    Glide.with(MyCommisionActivity.this)
                                            .load(activityBanner.imageUrl)
                                            .thumbnail(Glide.with(MyCommisionActivity.this).load(drawable))
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

                    @Override
                    public void onFailure(Call<ActivityBanner> call, Throwable t) {

                    }
                });
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
        txtAddToMycoin  = findViewById(R.id.txtAddToMycoin);
        imgReload = findViewById(R.id.imgReload);
        imgBanner = findViewById(R.id.imgBanner);
        imgLiveChat = findViewById(R.id.imgLiveChat);
        reloadLay = findViewById(R.id.layoutReload);
        recyclerCoin = findViewById(R.id.recyclerCoin);
        recyclerCoin.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerCoin.setLayoutManager(layoutManager);

        postListProgress = findViewById(R.id.progress);

        referUserListAdapter = new ReferUserListAdapter( getApplicationContext(), referUserList);
        recyclerCoin.setAdapter(referUserListAdapter);

        txtAddToMycoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float commission = Float.parseFloat(txtCoin.getText().toString());
                if (commission>0){
                    showWaitingDialog();
                    RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                            .commissionToMyCoin(
                                    Common.getKeyHash(MyCommisionActivity.this),
                                    gmailInfo.gmail,
                                    gmailInfo.user_id,
                                    gmailInfo.access_token,
                                    commission
                            )
                            .enqueue(new Callback<ServerResponse>() {
                                @Override
                                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                    dismissWaitingDialog();
                                    if (response.isSuccessful() && response.body()!=null){
                                        ServerResponse serverResponse = response.body();
                                        Toast.makeText(MyCommisionActivity.this, ""+serverResponse.error_description, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ServerResponse> call, Throwable t) {
                                    Log.d("Retrofit", t.getMessage());
                                    dismissWaitingDialog();
                                    Toast.makeText(MyCommisionActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(MyCommisionActivity.this, "Not Enough Coin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Common.setShakeAnimation(imgLiveChat, getApplicationContext());
        imgLiveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.openLiveChat(getApplicationContext());
            }
        });

        referUserListAdapter.setOnRedemReferClickListener(new ReferUserListAdapter.OnRedemReferClickListener() {
            @Override
            public void OnClick(String referId, int pos) {
                sendReferClaim(referId, pos);
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
        if (!isFinishing() &&dialog!=null && !dialog.isShowing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (!isFinishing() &&dialog!=null && dialog.isShowing()){
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
        if (reloadImage!=null && reloadImage.getAnimation()!=null ) {
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
        if (reloadImage!=null && reloadImage.getAnimation()!=null ){
            reloadImage.getAnimation().cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isActivityCreatedByNoti){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    private void sendReferClaim(final String referId, final int pos){
        showWaitingDialog();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getIdToken();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                String token  = task.getResult().getIdToken();
                Log.d("idToken", task.getResult().getIdToken());

                RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                        .redemRefer(
                                Common.getKeyHash(getApplicationContext()),
                                gmailInfo.gmail,
                                gmailInfo.user_id,
                                token,
                                referId
                        )
                        .enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                dismissWaitingDialog();
                                if (response.isSuccessful() && response.body()!=null){
                                    ServerResponse serverResponse = response.body();
                                   final Dialog noticeDialog = new Dialog(MyCommisionActivity.this);
                                    noticeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    noticeDialog.setCancelable(false);
                                    noticeDialog.setContentView(R.layout.dialog_notice);

                                    noticeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                    Window window = noticeDialog.getWindow();
                                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    TextView txtTitle = noticeDialog.findViewById(R.id.txtTitle);
                                    TextView txtMsg = noticeDialog.findViewById(R.id.txtMsg);
                                    Button btnOk = noticeDialog.findViewById(R.id.btnOk);

                                    if (!serverResponse.error){
                                        txtTitle.setText("Congratulation!!");
                                        referUserList.get(pos).claim = true;
                                        referUserListAdapter.notifyDataSetChanged();
                                    }else {
                                        txtTitle.setText("Attention!!");
                                    }
                                    txtMsg.setText(serverResponse.error_description);
                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            noticeDialog.dismiss();
                                        }
                                    });
                                    noticeDialog.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                dismissWaitingDialog();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissWaitingDialog();
            }
        });
    }
}