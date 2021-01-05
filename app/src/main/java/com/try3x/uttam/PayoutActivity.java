package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.paytm.pg.merchant.PaytmChecksum;
import com.try3x.uttam.Adapters.PayoutAdapter;
import com.try3x.uttam.Common.CapthaDialog;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Models.ActivityBanner;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.PayMethodInfo;
import com.try3x.uttam.Models.PaymentMethod;
import com.try3x.uttam.Models.Paytm.Checksum;
import com.try3x.uttam.Models.Response.MyCoinResponse;
import com.try3x.uttam.Models.Response.PayoutHistoryResponse;
import com.try3x.uttam.Models.Response.PayoutInfoResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.Models.Response.UserPayMethodListResponse;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayoutActivity extends AppCompatActivity {
    TextView txtSendRequest,  txtRupee;
    TextView txtCoin;
    TextView txtMinCoin, txtMinRupee;
    private ACProgressPie dialog;
    private FirebaseAuth mAuth;
    private GmailInfo gmailInfo;
    private float withrawableCoin;
    private Dialog payMethodDialog;
    private List<PayMethodInfo> payMethodInfoList  = new ArrayList<>();
    private PayMethodInfo selectedPayMethodInfo;
    int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;

    Button btnReload;
    private int PAGE = 1, PAGE_SIZE = 30, TOTAL_PAGE = 0;
    boolean isLoading = true;
    RecyclerView recyclerPayout;
    private ProgressBar progress, progress_horizontal;
    LinearLayout layoutReload, layPayoutReload, layPayout;
    private PayoutAdapter payoutAdapter;
    private LinearLayoutManager layoutManager;
    private boolean isActivityCreatedByNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);
        isActivityCreatedByNoti = getIntent().getBooleanExtra(Common.ACTIVITY_CREATED_BY_NOTI, false);
       /* try {
            requestPayment();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);
        createDialog();
        initView();
        //getPayoutInfo();
        getPayoutList();

    }

    private void getPayoutInfo() {
        layPayout.setVisibility(View.GONE);
        layPayoutReload.setVisibility(View.GONE);
        //showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPayoutInfo(
                        Common.getKeyHash(getApplicationContext()),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token
                )
                .enqueue(new Callback<PayoutInfoResponse>() {
                    @Override
                    public void onResponse(Call<PayoutInfoResponse> call, Response<PayoutInfoResponse> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body()!=null){
                            PayoutInfoResponse  payoutInfoResponse = response.body();
                            if (!payoutInfoResponse.error){
                                layPayout.setVisibility(View.VISIBLE);
                                layPayoutReload.setVisibility(View.GONE);

                                txtMinCoin.setText("Coin (Min "+String.valueOf(payoutInfoResponse.minPayout)+")");
                                txtMinRupee.setText("Rupee (Min "+String.valueOf(payoutInfoResponse.minPayout-payoutInfoResponse.payoutFee)+")");
                                getWithrableAmount(payoutInfoResponse.minPayout, payoutInfoResponse.payoutFee);
                                initPayoutClickButton(
                                        payoutInfoResponse.minPayout,
                                        payoutInfoResponse.minPayout-payoutInfoResponse.payoutFee
                                );

                                return;
                            }
                        }
                        layPayout.setVisibility(View.GONE);
                        layPayoutReload.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<PayoutInfoResponse> call, Throwable t) {
                        dismissWaitingDialog();
                        layPayout.setVisibility(View.GONE);
                        layPayoutReload.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initPayoutClickButton(final int minCoin, final int minRupee) {
        txtSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rupeStr = txtRupee.getText().toString();
                String coinStr = txtCoin.getText().toString();

                if (rupeStr.isEmpty() || coinStr.isEmpty()){
                    Toast.makeText(PayoutActivity.this, "Enter valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                float coin = Float.parseFloat(coinStr);
                float rupee = Float.parseFloat(rupeStr);
                if (coin>=minCoin && rupee>=minRupee){
                    Log.d("PayoutError", "with: "+withrawableCoin+" coin "+coin);

                    if (withrawableCoin>=coin){
                        //selectPaytmPayMethod(coin, rupee);
                        //selectPaymethod(coin, rupee);
                        showCaptcha(coin ,rupee);
                    }else {
                        Log.d("PayoutError", "1");
                        Toast.makeText(PayoutActivity.this, "You Have Not Much Coin", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d("PayoutError", "2");

                    Toast.makeText(PayoutActivity.this, "You Have Not Much Coin", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void initView() {
        txtSendRequest = findViewById(R.id.txtSendRequest);
        txtCoin = findViewById(R.id.edtCoin);
        txtRupee = findViewById(R.id.txtRupee);
        layoutManager = new LinearLayoutManager(this);
        recyclerPayout = findViewById(R.id.recyclerPayout);
        progress = findViewById(R.id.progress);
        layoutReload  = findViewById(R.id.layoutReload);
        layPayoutReload  = findViewById(R.id.layPayoutReload);
        layPayout  = findViewById(R.id.layPayout);

        txtMinCoin  = findViewById(R.id.txtMinCoin);
        txtMinRupee  = findViewById(R.id.txtMinRupee);
        btnReload  = findViewById(R.id.btnReload);

        progress_horizontal  = findViewById(R.id.progress_horizontal);
        recyclerPayout.setLayoutManager(layoutManager);

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPayoutInfo();
            }
        });

        initRecyclerPagination();






    }

    private void showCaptcha(final float coin, final float rupee) {

        final CapthaDialog capthaDialog = new CapthaDialog(PayoutActivity.this);
        capthaDialog.init();
        capthaDialog.showDialog();
        capthaDialog.setOnCaptchaDialogListener(new CapthaDialog.OnCaptchaDialogListener() {
            @Override
            public void onResultOk() {
                selectPaytmPayMethod(coin, rupee);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onToast(String message) {

                Toast.makeText(PayoutActivity.this, message, Toast.LENGTH_LONG).show();
                capthaDialog.hideDialog();
            }
        });
    }

    private void selectPaymethod(final int coin, final int rupee) {
        showWaitingDialog();
        payMethodDialog = new Dialog(PayoutActivity.this);
        payMethodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payMethodDialog.setCancelable(true);
        payMethodDialog.setContentView(R.layout.dialog_select_pay_method);

        payMethodDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = payMethodDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Spinner spinner = payMethodDialog.findViewById(R.id.spinnerPaymethod);
        Button btnCancel = payMethodDialog.findViewById(R.id.btnCancel);
        final Button payout = payMethodDialog.findViewById(R.id.btnPayout);

        final EditText edtPhn1 = payMethodDialog.findViewById(R.id.payNum1);


        payMethodDialog.show();

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPayMethodList(
                        Common.getKeyHash(PayoutActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token
                )
                .enqueue(new Callback<UserPayMethodListResponse>() {
                    @Override
                    public void onResponse(Call<UserPayMethodListResponse> call, Response<UserPayMethodListResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                           dismissWaitingDialog();
                            UserPayMethodListResponse userPayMethodListResponse = response.body();

                            if (!userPayMethodListResponse.isError()){
                                payMethodInfoList = userPayMethodListResponse.methodList;

                                if (payMethodInfoList.size()<1){
                                    Toast.makeText(PayoutActivity.this, "No Payment Method Added", Toast.LENGTH_SHORT).show();
                                }else {
                                    setMethodToSpinner(payMethodInfoList);
                                }


                            }else {

                                Toast.makeText(PayoutActivity.this, ""+userPayMethodListResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    private void setMethodToSpinner(final List<PayMethodInfo> payMethodInfoList) {
                        ArrayAdapter arrayAdapter = new ArrayAdapter(PayoutActivity.this, android.R.layout.simple_spinner_dropdown_item);

                        for (PayMethodInfo method : payMethodInfoList){
                            arrayAdapter.add(method.pay_method_name);
                        }
                        spinner.setAdapter(arrayAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedPayMethodInfo = payMethodInfoList.get(i);
                                edtPhn1.setText(selectedPayMethodInfo.getPay_number());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                selectedPayMethodInfo = null;
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<UserPayMethodListResponse> call, Throwable t) {
                       dismissWaitingDialog();

                    }
                });

        payout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPayMethodInfo!=null){
                    showWaitingDialog();
                    RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                            .paymentRequest(
                                    Common.getKeyHash(PayoutActivity.this),
                                    gmailInfo.gmail,
                                    gmailInfo.user_id,
                                    gmailInfo.access_token,
                                    selectedPayMethodInfo.pay_method_id,
                                    coin,
                                    rupee
                            )
                            .enqueue(new Callback<ServerResponse>() {
                                @Override
                                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                    dismissWaitingDialog();
                                    if (response.isSuccessful() && response.body()!=null){
                                        ServerResponse serverResponse = response.body();
                                        Toast.makeText(PayoutActivity.this, serverResponse.error_description, Toast.LENGTH_SHORT).show();

                                       if (!serverResponse.isError()){
                                            payMethodDialog.dismiss();
                                       }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ServerResponse> call, Throwable t) {
                                    dismissWaitingDialog();
                                    Toast.makeText(PayoutActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(PayoutActivity.this, "Select Payment method", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payMethodDialog.dismiss();
            }
        });

    }

    private void selectPaytmPayMethod(final float coin, final float rupee){

        showWaitingDialog();

        final Dialog payMethodDialog = new Dialog(PayoutActivity.this);
        payMethodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payMethodDialog.setCancelable(true);
        payMethodDialog.setContentView(R.layout.dialog_paytm_pay_method);

        payMethodDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = payMethodDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText edtNum1 = payMethodDialog.findViewById(R.id.payNum1);
        final EditText edtNum2 = payMethodDialog.findViewById(R.id.payNum2);

        TextView btnPayout = payMethodDialog.findViewById(R.id.btnPayout);
        TextView btnCancel = payMethodDialog.findViewById(R.id.btnCancel);
        ImageView ImgWpContact = payMethodDialog.findViewById(R.id.ImgWpContact);

        final ImageView imgBanner = payMethodDialog.findViewById(R.id.imgBanner);

        ImgWpContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLiveChat();
            }
        });

        payMethodDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                getBanner(imgBanner);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payMethodDialog.dismiss();
            }
        });

        btnPayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String num1 = edtNum1.getText().toString().trim();
                    String num2 = edtNum2.getText().toString().trim();


                    if (num1.equals(num2) && num1.length()==10 && android.util.Patterns.PHONE.matcher(num1).matches()){

                            showWaitingDialog();
                            RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                                    .paymentPaytmRequest(
                                            Common.getKeyHash(PayoutActivity.this),
                                            gmailInfo.gmail,
                                            gmailInfo.user_id,
                                            gmailInfo.access_token,
                                            num1,
                                            coin,
                                            rupee
                                    )
                                    .enqueue(new Callback<ServerResponse>() {
                                        @Override
                                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                            dismissWaitingDialog();
                                            if (response.isSuccessful() && response.body()!=null){
                                                ServerResponse serverResponse = response.body();
                                                Toast.makeText(PayoutActivity.this, serverResponse.error_description, Toast.LENGTH_SHORT).show();

                                                if (!serverResponse.isError()){
                                                    payMethodDialog.dismiss();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                                            dismissWaitingDialog();
                                            Toast.makeText(PayoutActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                    }else {
                        Toast.makeText(PayoutActivity.this, "Number Not Matched", Toast.LENGTH_SHORT).show();

                    }

            }
        });

        payMethodDialog.show();
    }

    public void  openLiveChat(){
        String contact = "+918585807175"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(PayoutActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void getBanner(final ImageView imgBanner) {
        imgBanner.setVisibility(View.GONE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getActivityBanner("PaytmPayoutDialog")
                .enqueue(new Callback<ActivityBanner>() {
                    @Override
                    public void onResponse(Call<ActivityBanner> call, Response<ActivityBanner> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body()!=null){
                            final ActivityBanner activityBanner = response.body();
                            if (!activityBanner.error){
                                if (activityBanner.imageUrl!=null){
                                    imgBanner.setVisibility(View.VISIBLE);
                                    Glide.with(PayoutActivity.this)
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
                        dismissWaitingDialog();
                    }
                });
    }
    private void getPayoutList() {
        //showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPayoutList(Common.getKeyHash(
                        PayoutActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<PayoutHistoryResponse>() {
                    @Override
                    public void onResponse(Call<PayoutHistoryResponse> call, Response<PayoutHistoryResponse> response) {
                        isLoading = false;
                        if (response.isSuccessful()&& response.body()!=null){
                            PayoutHistoryResponse payoutHistoryResponse = response.body();
                            TOTAL_PAGE = payoutHistoryResponse.getTotalPage();
                            if (!payoutHistoryResponse.isError()){


                                payoutAdapter = new PayoutAdapter(PayoutActivity.this, payoutHistoryResponse.getItems());
                                recyclerPayout.setAdapter(payoutAdapter);
                                dismissWaitingDialog();

                            }else {


                                dismissWaitingDialog();
                                Toast.makeText(PayoutActivity.this, ""+payoutHistoryResponse.getError_description(), Toast.LENGTH_SHORT).show();
                            }

                            if (payoutHistoryResponse.getItems().size()<0){
                                layoutReload.setVisibility(View.VISIBLE);
                            }else {
                                layoutReload.setVisibility(View.GONE);
                            }
                        }else {

                            dismissWaitingDialog();
                        }


                    }

                    @Override
                    public void onFailure(Call<PayoutHistoryResponse> call, Throwable t) {
                        Log.d("RetrofitError", t.getMessage());
                        isLoading = false;
                        dismissWaitingDialog();
                        layoutReload.setVisibility(View.VISIBLE);
                    }
                });

    }
    private void initRecyclerPagination() {

        recyclerPayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                progress.setVisibility(View.VISIBLE);
                                isLoading = true;
                                Log.v("...", "Last Item Wow !");
                                //Do pagination.. i.e. fetch new data
                                PAGE++;
                                if (PAGE<=TOTAL_PAGE){
                                    doPagination();
                                }else {
                                    isLoading = false;
                                    progress.setVisibility(View.GONE);
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
        progress.setVisibility(View.VISIBLE);

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPayoutList(
                        Common.getKeyHash(PayoutActivity.this),
                       gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<PayoutHistoryResponse>() {
                    @Override
                    public void onResponse(Call<PayoutHistoryResponse> call, Response<PayoutHistoryResponse> response) {

                        if (response.isSuccessful()&& response.body()!=null){
                            PayoutHistoryResponse payoutHistoryResponse = response.body();
                            if (!payoutHistoryResponse.isError()){
                                PAGE_SIZE = payoutHistoryResponse.getTotalPage();
                                if (payoutHistoryResponse.getItems().size()>0){
                                    isLoading = false;

                                  payoutAdapter.updateItem(payoutHistoryResponse.getItems());
                                }

                            }else {
                                isLoading = false;

                                Toast.makeText(PayoutActivity.this, ""+payoutHistoryResponse.getError_description(), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            isLoading = false;

                        }
                    }

                    @Override
                    public void onFailure(Call<PayoutHistoryResponse> call, Throwable t) {
                        Log.d("RetrofitError", t.getMessage());
                        isLoading = false;
                        progress.setVisibility(View.GONE);
                    }
                });

    }
    private void getWithrableAmount(final int minPayout, final int payoutFee) {
        progress_horizontal.setVisibility(View.VISIBLE);
        txtCoin.setText("00");
        txtRupee.setText("00");
        progress_horizontal.setVisibility(View.VISIBLE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getWithdrawable(
                        Common.getKeyHash(PayoutActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token
                )
                .enqueue(new Callback<MyCoinResponse>() {
                    @Override
                    public void onResponse(Call<MyCoinResponse> call, Response<MyCoinResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            progress_horizontal.setVisibility(View.GONE);
                            MyCoinResponse myCoinResponse = response.body();
                            if (!myCoinResponse.isError()){
                                withrawableCoin = myCoinResponse.coin;
                                txtCoin.setText(String.valueOf(myCoinResponse.coin));
                                if (myCoinResponse.coin>payoutFee){
                                    txtRupee.setText(String.valueOf(myCoinResponse.coin-payoutFee));
                                }
                            }else {
                                Toast.makeText(PayoutActivity.this, myCoinResponse.getError_description(), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(PayoutActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyCoinResponse> call, Throwable t) {
                        progress_horizontal.setVisibility(View.GONE);

                        Toast.makeText(PayoutActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showWaitingDialog();
        if (mAuth== null || gmailInfo==null || gmailInfo.user_id==null || gmailInfo.access_token==null || gmailInfo.gmail==null){
            Toast.makeText(this, "Login Not Valid. Login Again", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            dismissWaitingDialog();
            startActivity(new Intent(PayoutActivity.this, SplashActivity.class));
            finish();
        }
        dismissWaitingDialog();
        withrawableCoin = 0;
        //getWithrableAmount();
        getPayoutInfo();
    }
    private void createDialog() {
        dialog = new ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build();
        dialog.setCancelable(false);
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
    private void requestPayment() throws Exception {
        JSONObject paytmParams = new JSONObject();
        paytmParams.put("subwalletGuid", "28054249-XXXX-XXXX-af8f-fa163e429e83");
        paytmParams.put("orderId", "ORDERID_98765");
        paytmParams.put("beneficiaryPhoneNo", "5555566666");
        paytmParams.put("amount", "1.00");

        final String post_data = paytmParams.toString();
        final String x_mid      = "WCubfs40908007798011";
        String order_id = "jnvjnvvvdv";

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getChecksum(x_mid, order_id)
                .enqueue(new Callback<Checksum>() {
                    @Override
                    public void onResponse(Call<Checksum> call, Response<Checksum> response) {
                        if (response.isSuccessful()){
                            Checksum checksum = response.body();


                            String x_checksum = checksum.getChecksumHash();
                            new MyAsysncTask().execute(x_checksum);

                        }
                    }

                    @Override
                    public void onFailure(Call<Checksum> call, Throwable t) {

                    }
                });


    }

    private String  getCheckSum(String MID, String ORDERID, String MERCHANT_KEY) throws Exception {
        /* initialize an hash */
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("MID", MID);
        params.put("ORDERID", ORDERID);
        /**
         * Generate checksum by parameters we have
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
         */
        String paytmChecksum = PaytmChecksum.generateSignature(params, MERCHANT_KEY);
        System.out.println("generateSignature Returns: " + paytmChecksum);

        return  paytmChecksum;
    }

    class MyAsysncTask extends AsyncTask<String, Void,Void > {


        @Override
        protected Void doInBackground(String... param) {


            String x_checksum = param[0];

            /* Solutions offered are: food, gift, gratification, loyalty, allowance, communication */

            /* for Staging */
            URL url = null;


            /* for Production */
            // URL url = new URL("https://dashboard.paytm.com/bpay/api/v1/disburse/order/wallet/{solution}");

            try {
                JSONObject paytmParams = new JSONObject();
                paytmParams.put("subwalletGuid", "28054249-XXXX-XXXX-af8f-fa163e429e83");
                paytmParams.put("orderId", "ORDERID_98765");
                paytmParams.put("beneficiaryPhoneNo", "5555566666");
                paytmParams.put("amount", "1.00");

                final String post_data = paytmParams.toString();
                final String x_mid      = "WCubfs40908007798011";
                String order_id = "jnvjnvvvdv";

                url = new URL("https://staging-dashboard.paytm.com/bpay/api/v1/disburse/order/wallet/loyalty");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("x-mid", x_mid);
                connection.setRequestProperty("x-checksum", x_checksum);
                connection.setDoOutput(true);

                DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
                requestWriter.writeBytes(post_data);
                requestWriter.close();

                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));

                responseReader.close();


            } catch (Exception exception) {
                exception.printStackTrace();


            }

            return null;
        }


    }

    private void startSpinReload(ImageView reloadImage){

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isActivityCreatedByNoti){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

}