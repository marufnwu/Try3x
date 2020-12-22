package com.try3x.uttam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import com.try3x.uttam.Adapters.CoinPackAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Listener.OnCoinPackClickListener;
import com.try3x.uttam.Models.ActivityBanner;
import com.try3x.uttam.Models.CFToken;
import com.try3x.uttam.Models.CoinPackage;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.Paytm.PaytmHash;
import com.try3x.uttam.Models.Paytm.PaytmResponse;
import com.try3x.uttam.Models.Paytm.Root;
import com.try3x.uttam.Models.RazorPayOrder;
import com.try3x.uttam.Models.Response.BuyCoinTransResponse;
import com.try3x.uttam.Models.Response.CoinPackageResponse;
import com.try3x.uttam.Models.Response.PaymentInfoResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.Models.User;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddCoinActivity extends AppCompatActivity implements OnCoinPackClickListener, View.OnClickListener, PaymentResultListener {

    RecyclerView recyclerCoinPack;
    ProgressBar progress;
    LinearLayout layoutReload;

    TextView txtPackName, txtPackPrice, txtPackCoin,txtPayAmount;
    GmailInfo gmailInfo;

    User user;
    FirebaseAuth mAuth;

    CoinPackAdapter coinPackAdapter;
    CoinPackage coinPackage;
    private ACProgressPie dialog;
    private String trans_ref;
    private float amount;
    LinearLayout layPack;
    ImageView imgSale;
    ImageView imgPaytm,imgGpay,imgPhonpe,imgAmazonPay,imgBhmi,imgJiomoney;
    private int DEBIT_REQUEST_CODE = 10005;
    private PaytmPGService paytmService;
    private int requestCode = 10052;
    private ImageView imgBanner, imgLiveChat;
    private boolean isActivityCreatedByNoti = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coin);
        isActivityCreatedByNoti = getIntent().getBooleanExtra(Common.ACTIVITY_CREATED_BY_NOTI, false);
        Checkout.preload(getApplicationContext());
        initView();
        Paper.init(this);
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);
        user = Paper.book().read(PaperDB.USER_PROFILE);

        //paytmService = PaytmPGService.getStagingService("");
        paytmService = PaytmPGService.getProductionService();

        mAuth = FirebaseAuth.getInstance();
        createDialog();
        coinPackage = new CoinPackage();

    }


    private void checkIfPhonePeExists() {

    }
    private void initView() {
        recyclerCoinPack = findViewById(R.id.recyclerCoinPack);
        progress = findViewById(R.id.progress);
        layoutReload = findViewById(R.id.layoutReload);
        txtPackName = findViewById(R.id.txtPackName);
        txtPackPrice = findViewById(R.id.txtPackPrice);
        txtPackCoin = findViewById(R.id.txtPackCoin);
        txtPayAmount = findViewById(R.id.txtPayAmount);

        imgBanner = findViewById(R.id.imgBanner);
        imgLiveChat = findViewById(R.id.imgLiveChat);
        txtPayAmount = findViewById(R.id.txtPayAmount);

        layPack = findViewById(R.id.layPack);
        imgSale = findViewById(R.id.imgSale);

        imgPaytm = findViewById(R.id.imgPaytm);
        imgGpay = findViewById(R.id.imgGpay);
        imgPhonpe = findViewById(R.id.imgPhonpe);
        imgAmazonPay = findViewById(R.id.imgAmazonPay);
        imgBhmi = findViewById(R.id.imgBhmi);
        imgJiomoney = findViewById(R.id.imgJiomoney);


        recyclerCoinPack.setHasFixedSize(true);
        recyclerCoinPack.setLayoutManager(new LinearLayoutManager(this));

       /* btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coinPackage!=null && coinPackage.price>0){
                    getTransRef();
                }else {
                    Toast.makeText(AddCoinActivity.this, "Please Select Any Package", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

       imgPaytm.setOnClickListener(this);
        imgGpay.setOnClickListener(this);
        imgPhonpe.setOnClickListener(this);
        imgAmazonPay.setOnClickListener(this);
        imgBhmi.setOnClickListener(this);
        imgJiomoney.setOnClickListener(this);
        txtPayAmount.setOnClickListener(this);
        imgLiveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.openLiveChat(getApplicationContext());
            }
        });
        Common.setShakeAnimation(imgLiveChat, getApplicationContext());
        getDynamicBanner(imgBanner, "AddCoinActivity");

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
    private void getTransRef() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getBuyCoinTransRef(
                        Common.getKeyHash(this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        coinPackage.price,
                        coinPackage.id,
                        coinPackage.coin,
                        " "
                )
                .enqueue(new Callback<BuyCoinTransResponse>() {
                    @Override
                    public void onResponse(Call<BuyCoinTransResponse> call, Response<BuyCoinTransResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            BuyCoinTransResponse buyCoinTransResponse = response.body();
                            if (!buyCoinTransResponse.isError()){
                                //startPayment(buyCoinTransResponse.transaction_ref);
                               // fetchPaymnetInfo(buyCoinTransResponse);
                                //getHash(buyCoinTransResponse);
                                //startPhonPe(buyCoinTransResponse);

                                //startPaytm(buyCoinTransResponse);

                                //paytmAllInAll(buyCoinTransResponse);
                                //cashFree(buyCoinTransResponse);
                                razorPay(buyCoinTransResponse);
                            }else {
                                dismissWaitingDialog();
                                Toast.makeText(AddCoinActivity.this, ""+buyCoinTransResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            dismissWaitingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<BuyCoinTransResponse> call, Throwable t) {
                        dismissWaitingDialog();
                    }
                });
    }

    private void razorPay(BuyCoinTransResponse buyCoinTransResponse) {

    }

    /*private void cashFree(final BuyCoinTransResponse buyCoinTransResponse) {
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .generateCFToken(buyCoinTransResponse.transaction_id, buyCoinTransResponse.amount)
                .enqueue(new Callback<CFToken>() {
                    @Override
                    public void onResponse(Call<CFToken> call, Response<CFToken> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body()!=null){
                            CFToken cfToken = response.body();
                            if (cfToken.status.equals("OK")){
                                String appId = "38847674d3f43ffe3ea5c846f74883";
                                Map<String, String> params = new HashMap<>();
                                params.put(PARAM_APP_ID, appId);
                                params.put(PARAM_ORDER_ID, buyCoinTransResponse.transaction_id);
                                params.put(PARAM_ORDER_CURRENCY, "INR");
                                params.put(PARAM_ORDER_AMOUNT, String.valueOf(buyCoinTransResponse.amount));
                                params.put(PARAM_CUSTOMER_PHONE, "+8801955786556");
                                params.put(PARAM_CUSTOMER_EMAIL, "maruf.paikgacha@gmail.com");
                                params.put(PARAM_NOTIFY_URL, "https://try3x.xyz/api/cashfree/cashfree.notify.php");


                                CFPaymentService.getCFPaymentServiceInstance()
                                        .upiPayment(AddCoinActivity.this, params, cfToken.cftoken, "TEST");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CFToken> call, Throwable t) {

                    }
                });
    }*/

    private void paytmAllInAll(final BuyCoinTransResponse buyCoinTransResponse) {



        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPaytmMid()
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            ServerResponse serverResponse = response.body();
                            if (!serverResponse.isError()){
                                if (serverResponse.getError_description()!=null){
                                    final String PAYTM_MID = serverResponse.getError_description();
                                    RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                                            .paytmHash(
                                                    String.valueOf(buyCoinTransResponse.amount),
                                                    buyCoinTransResponse.transaction_id,
                                                    gmailInfo.user_id,
                                                    gmailInfo.gmail
                                            ).enqueue(new Callback<Root>() {
                                        @Override
                                        public void onResponse(Call<Root> call, Response<Root> response) {
                                            if (response.isSuccessful() && response.body()!=null){
                                                dismissWaitingDialog();
                                                String callback =   "https://try3x.xyz/api/paytm/paytm.paymentCheck.php?ORDERID="+buyCoinTransResponse.transaction_id;
                                                Root paytmHash = response.body();
                                                if (paytmHash.body.resultInfo.resultCode.equals("0000")){
                                                    PaytmOrder paytmOrder = new PaytmOrder(buyCoinTransResponse.transaction_id, PAYTM_MID, paytmHash.getBody().txnToken, String.valueOf(buyCoinTransResponse.amount), callback);

                                                    TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
                                                        @Override
                                                        public void onTransactionResponse(Bundle bundle) {
                                                            Log.d("PaytmError", new Gson().toJson(bundle));
                                                        }

                                                        @Override
                                                        public void networkNotAvailable() {
                                                            Log.d("PaytmError", "Not network");

                                                        }

                                                        @Override
                                                        public void onErrorProceed(String s) {
                                                            Log.d("PaytmError", s);

                                                        }

                                                        @Override
                                                        public void clientAuthenticationFailed(String s) {
                                                            Log.d("PaytmError", s);

                                                        }

                                                        @Override
                                                        public void someUIErrorOccurred(String s) {
                                                            Log.d("PaytmError", s);

                                                        }

                                                        @Override
                                                        public void onErrorLoadingWebPage(int i, String s, String s1) {
                                                            Log.d("PaytmError", s);

                                                        }

                                                        @Override
                                                        public void onBackPressedCancelTransaction() {
                                                            Log.d("PaytmError", "onBackPressedCancelTransaction");

                                                        }

                                                        @Override
                                                        public void onTransactionCancel(String s, Bundle bundle) {
                                                            Log.d("PaytmError", new Gson().toJson(bundle));

                                                        }
                                                    });

                                                    transactionManager.startTransaction(AddCoinActivity.this, requestCode);
                                                }else {
                                                    Log.d("Paytm", paytmHash.body.resultInfo.resultCode);
                                                    Toast.makeText(AddCoinActivity.this, "Payment Initiation Failed", Toast.LENGTH_SHORT).show();
                                                }




                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Root> call, Throwable t) {
                                            Log.d("Retrofit", t.getMessage());
                                        }
                                    });
                                }
                            }else {
                                dismissWaitingDialog();
                                Toast.makeText(AddCoinActivity.this, "Payment Not Possible Now", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        dismissWaitingDialog();
                    }
                });





    }

    private void startPaytm(final BuyCoinTransResponse buyCoinTransResponse) {

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .paytmRequestHash(buyCoinTransResponse.transaction_id)
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            dismissWaitingDialog();
                            ServerResponse serverResponse = response.body();

                            HashMap<String, String> paramMap = new HashMap<String,String>();
                            paramMap.put( "MID" , "sUqxHf07917146716576");
                            // Key in your staging and production MID available in your dashboard
                            paramMap.put( "ORDER_ID" , buyCoinTransResponse.transaction_id);
                            paramMap.put( "CUST_ID" , gmailInfo.user_id);
                            paramMap.put( "MOBILE_NO" , "7777777777");
                            paramMap.put( "EMAIL" , gmailInfo.gmail);
                            paramMap.put( "CHANNEL_ID" , "WAP");
                            paramMap.put( "TXN_AMOUNT" , String.valueOf(buyCoinTransResponse.amount));
                            paramMap.put( "WEBSITE" , "DEFAULT");
                            // This is the staging value. Production value is available in your dashboard
                            paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                            // This is the staging value. Production value is available in your dashboard
                            paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback");
                            paramMap.put( "CHECKSUMHASH" , serverResponse.error_description);
                            PaytmOrder Order = new PaytmOrder(paramMap);


                            paytmService.initialize(Order, null);
                            Toast.makeText(AddCoinActivity.this, "ok", Toast.LENGTH_SHORT).show();
                            /*paytmService.startPaymentTransaction(AddCoinActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                                @Override
                                public void onTransactionResponse(Bundle inResponse) {
                                    Log.d("PaytmResponse", new Gson().toJson(inResponse));
                                    Toast.makeText(AddCoinActivity.this, new Gson().toJson(inResponse), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void networkNotAvailable() {
                                    Toast.makeText(AddCoinActivity.this, "networkNotAvailable", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void clientAuthenticationFailed(String inErrorMessage) {
                                    Toast.makeText(AddCoinActivity.this, inErrorMessage, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void someUIErrorOccurred(String inErrorMessage) {

                                    Toast.makeText(AddCoinActivity.this, inErrorMessage, Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                    Toast.makeText(AddCoinActivity.this, inErrorMessage, Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onBackPressedCancelTransaction() {
                                    Toast.makeText(AddCoinActivity.this, "onBackPressedCancelTransaction", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

                                }
                            });*/
                        }else {
                            dismissWaitingDialog();
                            Toast.makeText(AddCoinActivity.this, "not", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        dismissWaitingDialog();
                        Log.d("Retrofit", t.getMessage());
                    }
                });



    }


    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }





    private void addBuyCoin(String ref, float price) {

        showWaitingDialog();

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .addBuyCoin(
                        Common.getKeyHash(AddCoinActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        ref,
                        price
                )
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body()!=null){
                            Toast.makeText(AddCoinActivity.this, "Coin Added", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        dismissWaitingDialog();
                    }
                });
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

        if (dialog!=null && !dialog.isShowing() && !isFinishing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (!isFinishing() && dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(AddCoinActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddCoinActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        if (gmailInfo==null || mAuth == null){
            mAuth.signOut();
            Toast.makeText(this, "Login Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddCoinActivity.this, SplashActivity.class));
            finish();
        }

        getCoinPack();
    }

    private void getCoinPack() {

        progress.setVisibility(View.VISIBLE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getCoinPack(
                        Common.getKeyHash(this)
                )
                .enqueue(new Callback<CoinPackageResponse>() {
                    @Override
                    public void onResponse(Call<CoinPackageResponse> call, Response<CoinPackageResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            CoinPackageResponse coinPackageResponse = response.body();
                            if (!coinPackageResponse.isError()){
                                progress.setVisibility(View.GONE);


                                if (coinPackageResponse.packs.size()>0){
                                    layoutReload.setVisibility(View.GONE);

                                        coinPackAdapter = new CoinPackAdapter(AddCoinActivity.this, coinPackageResponse.packs, AddCoinActivity.this);
                                        recyclerCoinPack.setAdapter(coinPackAdapter);
                                }else {
                                    layoutReload.setVisibility(View.VISIBLE);
                                }

                            }else {
                                progress.setVisibility(View.GONE);
                                layoutReload.setVisibility(View.VISIBLE);
                                Toast.makeText(AddCoinActivity.this, ""+coinPackageResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            progress.setVisibility(View.GONE);
                            layoutReload.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<CoinPackageResponse> call, Throwable t) {
                        dismissWaitingDialog();
                        Log.d("RetrofitError", t.getMessage());
                        progress.setVisibility(View.GONE);
                        layoutReload.setVisibility(View.VISIBLE);

                    }
                });
    }

    @Override
    public void onClick(CoinPackage coinPackage, int pos) {
        if (coinPackAdapter!=null){
            coinPackAdapter.selectItem(pos);
        }

        this.coinPackage = coinPackage;
       /* txtPackCoin.setText(String.valueOf(this.coinPackage.coin));
        txtPackName.setText(String.valueOf(this.coinPackage.name));
        txtPackPrice.setText(String.valueOf(this.coinPackage.price));*/
       txtPayAmount.setText(String.valueOf("Pay: ₹"+this.coinPackage.price));
        txtPayAmount.setVisibility(View.VISIBLE);

    }


    public void clearSelectItem(){
        coinPackage = null;
        txtPayAmount.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && data != null) {
            Log.d("PaytmOnActivityResponse", data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"));
            //Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
            String response = data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response");

            try{
                Gson gson = new Gson(); // Or use new GsonBuilder().create();
                PaytmResponse paytmResponse = gson.fromJson(response, PaytmResponse.class);

                if (paytmResponse!=null && paytmResponse.ORDERID!=null){
                    showWaitingDialog();
                    RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                            .paytmPaymentStatus(paytmResponse.ORDERID)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    dismissWaitingDialog();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    dismissWaitingDialog();
                                }
                            });
                }
            }catch (IllegalStateException | JsonSyntaxException exception){
                exception.printStackTrace();
            }


        }


        if (requestCode == DEBIT_REQUEST_CODE) {

      /*This callback indicates only about completion of UI flow.
            Inform your server to make the transaction
            status call to get the status. Update your app with the
            success/failure status.*/
            Toast.makeText(this, "bal", Toast.LENGTH_SHORT).show();
        }
    }



    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    @Override
    public void onClick(View view) {
        int btn = view.getId();
        if(btn == R.id.txtPayAmount|| btn == R.id.imgPaytm || btn == R.id.imgGpay || btn == R.id.imgPhonpe || btn == R.id.imgAmazonPay || btn == R.id.imgJiomoney || btn == R.id.imgBhmi ){
            if (coinPackage!=null && coinPackage.price>0){
                //getTransRef();
                createRazorOrder();
            }else {
                Toast.makeText(AddCoinActivity.this, "Please Select Any Package", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createRazorOrder() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .generateRazorpayOrder((int)coinPackage.price*100)
                .enqueue(new Callback<RazorPayOrder>() {
                    @Override
                    public void onResponse(Call<RazorPayOrder> call, Response<RazorPayOrder> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            RazorPayOrder razorPayOrder = response.body();
                            if (razorPayOrder.status.equals("created")){
                                addRazorToDB(razorPayOrder);
                            }else {
                                Toast.makeText(AddCoinActivity.this, "Order Not Created", Toast.LENGTH_SHORT).show();
                                dismissWaitingDialog();
                            }
                        }else {
                            Toast.makeText(AddCoinActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            dismissWaitingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<RazorPayOrder> call, Throwable t) {
                        Toast.makeText(AddCoinActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        dismissWaitingDialog();
                    }
                });
    }

    private void addRazorToDB(final RazorPayOrder razorPayOrder) {
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getBuyCoinTransRef(
                        Common.getKeyHash(this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        coinPackage.price,
                        coinPackage.id,
                        coinPackage.coin,
                        razorPayOrder.id
                )
                .enqueue(new Callback<BuyCoinTransResponse>() {
                    @Override
                    public void onResponse(Call<BuyCoinTransResponse> call, Response<BuyCoinTransResponse> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body()!=null){
                            BuyCoinTransResponse buyCoinTransResponse = response.body();
                            if (!buyCoinTransResponse.isError()){
                                Checkout checkout = new Checkout();
                                //Test id
                                //String app_id = "rzp_test_Hy3P4zxXBzRRSC";

                                //Live id
                                String app_id = "rzp_live_0odiffNRri6Vyz";

                                checkout.setKeyID(app_id);

                                try {
                                    JSONObject options = new JSONObject();

                                    options.put("name", "Try3x");
                                    options.put("description", "Reference No. "+buyCoinTransResponse.transaction_ref);
                                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                                    options.put("order_id", razorPayOrder.id);//from response of step 3.
                                    options.put("theme.color", "#3399cc");
                                    options.put("currency", "INR");
                                    options.put("amount", buyCoinTransResponse.amount*100);//pass amount in currency subunits
                                    options.put("prefill.email", gmailInfo.getGmail());
                                    options.put("prefill.contact",user.phone);
                                    checkout.open(AddCoinActivity.this, options);
                                } catch(Exception e) {
                                    Log.e("RazorPayError", "Error in starting Razorpay Checkout", e);
                                }
                            }else {
                                dismissWaitingDialog();
                                Toast.makeText(AddCoinActivity.this, ""+buyCoinTransResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            dismissWaitingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<BuyCoinTransResponse> call, Throwable t) {
                        Toast.makeText(AddCoinActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        dismissWaitingDialog();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isActivityCreatedByNoti){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
        Log.d("RazorPay", s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Not Success "+s, Toast.LENGTH_SHORT).show();
    }
}