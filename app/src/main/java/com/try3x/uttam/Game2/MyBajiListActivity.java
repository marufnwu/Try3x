package com.try3x.uttam.Game2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.try3x.uttam.Adapters.MyBajiListAdapter;
import com.try3x.uttam.Common.CapthaDialog;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Listener.OnClaimClickListener;
import com.try3x.uttam.MainActivity;
import com.try3x.uttam.Models.ActivityBanner;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.MyBajiList;
import com.try3x.uttam.Models.Response.BajiInfoResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.R;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBajiListActivity extends AppCompatActivity implements OnClaimClickListener {

    int claimPos = -1;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    int currPage = 1;
    int perPage= 5;
    MyBajiListAdapter myBajiListAdapter;
    private LinearLayoutManager layoutManager;
    private ACProgressPie dialog;
    private MyBajiList myBajiList;
    int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;

    private int PAGE = 1, PAGE_SIZE = 30, TOTAL_PAGE = 0;
    boolean isLoading = true;

    private GmailInfo gmailInfo;
    RecyclerView recyclerBaji;
    private ProgressBar postListProgress;
    LinearLayout layoutReload;
    TextView txtTotalBaji, txtTotalWin, txtTodayBaji, txtToadyWin;
    ImageView imgBanner, imgLiveChat;
    private boolean isActivityCreatedByNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_baji_list);
        isActivityCreatedByNoti = getIntent().getBooleanExtra(Common.ACTIVITY_CREATED_BY_NOTI, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Paper.init(this);
        createDialog();
        gmailInfo  = Paper.book().read(PaperDB.GMAILINFO);

        recyclerBaji = findViewById(R.id.recyclerMyBaji);
        recyclerBaji.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerBaji.setLayoutManager(layoutManager);
        
        postListProgress = findViewById(R.id.progress);
        layoutReload = findViewById(R.id.layoutReload);


        txtTotalBaji = findViewById(R.id.txtTotalBaji);
        txtTotalWin = findViewById(R.id.txtTotalBajiWin);
        txtToadyWin = findViewById(R.id.txtTodayBajiWin);
        txtTodayBaji = findViewById(R.id.txtTodayBajiPlace);

        imgBanner = findViewById(R.id.imgBanner);
        imgLiveChat = findViewById(R.id.imgLiveChat);
        Common.setShakeAnimation(imgLiveChat, getApplicationContext());
        imgLiveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.openLiveChat(getApplicationContext());
            }
        });

        initRecyclerPagination();

        if (mAuth!=null && mUser!=null){
            getMyBajiList();

            //getMyBajiInfo();
        }

        getDynamicBanner(imgBanner, "MyBajiListActivity");
    }
    private void getDynamicBanner(final ImageView imgBanner, String keyword) {
        imgBanner.setVisibility(View.GONE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getActivityBanner(keyword)
                .enqueue(new Callback<ActivityBanner>() {
                    @Override
                    public void onResponse(Call<ActivityBanner> call, Response<ActivityBanner> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            final ActivityBanner activityBanner = response.body();
                            if (!activityBanner.error){
                                if (activityBanner.imageUrl!=null){
                                    imgBanner.setVisibility(View.VISIBLE);
                                    Glide.with(getApplicationContext())
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

                    @Override
                    public void onFailure(Call<ActivityBanner> call, Throwable t) {

                    }
                });
    }
    private void getMyBajiInfo() {
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getMyBajiInfo(
                        Common.getKeyHash(MyBajiListActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token

                )
                .enqueue(new Callback<BajiInfoResponse>() {
                    @Override
                    public void onResponse(Call<BajiInfoResponse> call, Response<BajiInfoResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            BajiInfoResponse bajiInfoResponse = response.body();
                            if (!bajiInfoResponse.error){
                                txtToadyWin.setText(""+bajiInfoResponse.todayWin);
                                txtTodayBaji.setText(""+bajiInfoResponse.todayBaji);
                                txtTotalBaji.setText(""+bajiInfoResponse.totalBaji);
                                txtTotalWin.setText(""+bajiInfoResponse.totalWin);
                            }else {
                               setBajiInfoZero();
                                Toast.makeText(MyBajiListActivity.this, bajiInfoResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BajiInfoResponse> call, Throwable t) {
                        setBajiInfoZero();
                        Toast.makeText(MyBajiListActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setBajiInfoZero() {
        txtToadyWin.setText("00");
        txtTodayBaji.setText("00");
        txtTotalBaji.setText("00");
        txtTotalWin.setText("00");
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (gmailInfo==null){
            Toast.makeText(this, "Login Again", Toast.LENGTH_SHORT).show();
            finish();
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

    private void initRecyclerPagination() {

        recyclerBaji.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                .getGame2MyBajiList(
                        Common.getKeyHash(MyBajiListActivity.this),
                        mUser.getEmail(),
                        gmailInfo.user_id,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<MyBajiList>() {
                    @Override
                    public void onResponse(Call<MyBajiList> call, Response<MyBajiList> response) {

                        if (response.isSuccessful()&& response.body()!=null){
                           MyBajiList myBajiList1 = response.body();
                            if (!myBajiList1.error){
                              PAGE_SIZE = myBajiList1.totalPage;
                              if (myBajiList1.bajiList.size()>0){
                                  isLoading = false;

                                  myBajiList.bajiList.addAll(myBajiList1.bajiList);
                                  myBajiListAdapter.notifyDataSetChanged();
                              }

                            }else {
                                isLoading = false;

                                Toast.makeText(MyBajiListActivity.this, ""+myBajiList.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            isLoading = false;

                        }
                    }

                    @Override
                    public void onFailure(Call<MyBajiList> call, Throwable t) {
                        Log.d("RetrofitError", t.getMessage());
                        isLoading = false;
                        postListProgress.setVisibility(View.GONE);
                    }
                });

    }

    private void getMyBajiList() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getGame2MyBajiList(Common.getKeyHash(
                        MyBajiListActivity.this),
                        mUser.getEmail(),
                        gmailInfo.user_id,
                        true, PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<MyBajiList>() {
                    @Override
                    public void onResponse(Call<MyBajiList> call, Response<MyBajiList> response) {
                        isLoading = false;
                        if (response.isSuccessful()&& response.body()!=null){
                             myBajiList = response.body();
                             TOTAL_PAGE = myBajiList.totalPage;
                           if (!myBajiList.error){


                                   myBajiListAdapter = new MyBajiListAdapter(MyBajiListActivity.this, myBajiList.bajiList, MyBajiListActivity.this);
                                   recyclerBaji.setAdapter(myBajiListAdapter);
                                   dismissWaitingDialog();

                           }else {


                               dismissWaitingDialog();
                               Toast.makeText(MyBajiListActivity.this, ""+myBajiList.error_description, Toast.LENGTH_SHORT).show();
                           }
                        }else {

                            dismissWaitingDialog();
                        }


                        if (myBajiList.bajiList.size()<0){
                            layoutReload.setVisibility(View.VISIBLE);
                        }else {
                            layoutReload.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<MyBajiList> call, Throwable t) {
                        Log.d("RetrofitError", t.getMessage());
                        isLoading = false;
                        dismissWaitingDialog();
                        layoutReload.setVisibility(View.VISIBLE);

                    }
                });

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

    private void showCaptcha(final int id, final int pos) {

        final CapthaDialog capthaDialog = new CapthaDialog(MyBajiListActivity.this);
        capthaDialog.init();
        capthaDialog.showDialog();
        capthaDialog.setOnCaptchaDialogListener(new CapthaDialog.OnCaptchaDialogListener() {
            @Override
            public void onResultOk() {
                claimBaji(id, pos);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onToast(String message) {

                Toast.makeText(MyBajiListActivity.this, message, Toast.LENGTH_LONG).show();
                capthaDialog.hideDialog();
            }
        });
    }


    @Override
    public void onClick(int id, int pos) {
       showCaptcha(id, pos);

    }

    private void claimBaji(int id, int pos){
        showWaitingDialog();
        claimPos = pos;
        if (gmailInfo==null){
            Toast.makeText(this, "Login Again", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("UserId", "paper "+gmailInfo.user_id+" user "+mUser.getUid());

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .claimGame2Baji(
                        Common.getKeyHash(getApplicationContext()),
                        mUser.getEmail(),
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        id
                )
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            ServerResponse serverResponse = response.body();
                            if (!serverResponse.isError()){
                                dismissWaitingDialog();
                                if (myBajiListAdapter!=null){
                                    if (myBajiList!=null && myBajiList.bajiList.size()>=claimPos){
                                        myBajiList.bajiList.get(claimPos).claim = true;
                                        myBajiListAdapter.notifyDataSetChanged();
                                    }
                                }
                            }else {
                                dismissWaitingDialog();
                                Toast.makeText(MyBajiListActivity.this, ""+serverResponse.getError_description(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        dismissWaitingDialog();
                    }
                });
    }


}