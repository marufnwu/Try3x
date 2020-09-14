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
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.phonepe.intent.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.PhonePeInitException;
import com.phonepe.intent.sdk.api.TransactionRequest;
import com.phonepe.intent.sdk.api.TransactionRequestBuilder;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.PaymentApp;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;
import com.try3x.uttam.Adapters.CoinPackAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Listener.OnCoinPackClickListener;
import com.try3x.uttam.Models.CoinPackage;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.Paytm.PaytmHash;
import com.try3x.uttam.Models.Paytm.Root;
import com.try3x.uttam.Models.Response.BuyCoinTransResponse;
import com.try3x.uttam.Models.Response.CoinPackageResponse;
import com.try3x.uttam.Models.Response.PaymentInfoResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.Models.User;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

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

public class AddCoinActivity extends AppCompatActivity implements OnCoinPackClickListener, PaymentStatusListener, View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coin);
        PhonePe.init(this);
        initView();
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
                        coinPackage.coin
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

                                paytmAllInAll(buyCoinTransResponse);
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

    private void paytmAllInAll(final BuyCoinTransResponse buyCoinTransResponse) {
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
                        PaytmOrder paytmOrder = new PaytmOrder(buyCoinTransResponse.transaction_id, "sUqxHf07917146716576", paytmHash.getBody().txnToken, String.valueOf(buyCoinTransResponse.amount), callback);

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


    private void startPhonPe(BuyCoinTransResponse response) {
        String salt = "8289e078-be0b-484d-ae60-052f117f8deb";
        int saltIndex = 1;


        String apiEndPoint = "/v4/debit";
        HashMap<String, Object> data = new HashMap<>();
        data.put("merchantId", "M2306160483220675579140"); //String. Mandatory
        data.put("transactionId", response.transaction_ref); //String. Mandatory.
        data.put("amount", 1000); //Long. Mandatory
        data.put("merchantOrderId", response.transaction_id);
        data.put("message", "Messeage from merchant");
        data.put("paymentScope", "ALL_UPI_APPS");

        //Note: Don't pass this if you are passing userAuthToken.
        data.put("merchantUserId", "U123456789"); //String. Mandatory. Needed for linking

        byte[] byteStr = new byte[0];
        try {
            byteStr = new Gson().toJson(data).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(byteStr, Base64.DEFAULT);

        String checksum = sha256(base64 + apiEndPoint + salt) + "###" + saltIndex;

        HashMap<String, String> headers = new HashMap();
        headers.put("X-CALLBACK-URL","https://www.demoMerchant.com");  // Merchant server URL
        headers.put("X-CALL-MODE","POST");

        Log.d("Hash Server", response.phonpe.checksum);
        Log.d("Hash Client", checksum);
        TransactionRequest debitRequest = new TransactionRequestBuilder()
                .setData(base64)
                .setChecksum(checksum)
                .setUrl(apiEndPoint)
                .setHeaders(headers)
                .build();

       /* TransactionRequest debitRequest = new TransactionRequestBuilder()
                .setData(response.phonpe.base64)
                .setChecksum(response.phonpe.checksum)
                .setUrl(response.phonpe.apiEndPoint)
                .setHeaders(headers)
                .build();*/

        try {
            if(PhonePe.isAppInstalled()) {
                try {
                    startActivityForResult(PhonePe.getTransactionIntent(debitRequest), DEBIT_REQUEST_CODE);
                } catch(PhonePeInitException e){
                   Log.d("PhonPeErr", e.getMessage());
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                startActivityForResult(PhonePe.getImplicitIntent(/* Context */ this, debitRequest), DEBIT_REQUEST_CODE);
            }
        } catch (PhonePeInitException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_LONG).show();


        }
        dismissWaitingDialog();

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

    private void getHash(BuyCoinTransResponse buyCoinTransResponse) {
        //String hashSequence = key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||salt;
        final String txnId = buyCoinTransResponse.transaction_ref;
        final float amount = buyCoinTransResponse.amount;
        final String productinfo = coinPackage.name;
        final String firstname = gmailInfo.getUser_id();
        final String email = gmailInfo.getGmail();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .payRequestHash(
                        txnId, amount, productinfo, firstname, email
                )
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            setupPayment(response.body(), txnId, amount, productinfo, firstname, email);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                            dismissWaitingDialog();
                            Log.d("Retrofit", t.getMessage());
                    }
                });

    }

    private void setupPayment(String hash, String txnId, float amount, String productinfo, String firstname, String email) {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();


        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(String.valueOf(amount))                          // Payment amount
                .setTxnId(txnId)                                             // Transaction ID
                .setPhone("9062686255")                                           // User Phone number
                .setProductName(productinfo)                   // Product Name or description
                .setFirstName(firstname)                              // User First name
                .setEmail(email)                                            // User Email ID
                .setsUrl("https://try3x.xyz/api/paymentSuccess.php")                    // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")                     //Failure URL (furl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(true)                              // Integration environment - true (Debug)/ false(Production)
                .setKey("HTS9ppAl")                        // Merchant key
                .setMerchantId("5960507");             // Merchant ID


        //declare paymentParam object
        PayUmoneySdkInitializer.PaymentParam paymentParam = null;
        try {
            paymentParam = builder.build();
           // paymentParam.setMerchantHash(hash);
            Log.d("Sha512 Server", hash);
            calculateServerSideHashAndInitiatePayment1(paymentParam, txnId, amount, productinfo, firstname, email);
          paymentParam = calculateServerSideHashAndInitiatePayment1(paymentParam, txnId, amount, productinfo, firstname, email);
            dismissWaitingDialog();
            PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam,
                    this, R.style.AppTheme_default, false);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Fuck", e.getMessage()+user.phone);
        }
        //set the hash


    }



    private void fetchPaymnetInfo(final BuyCoinTransResponse buyCoinTransResponse) {

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPaymentInfo(
                        Common.getKeyHash(this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token
                )
                .enqueue(new Callback<PaymentInfoResponse>() {
                    @Override
                    public void onResponse(Call<PaymentInfoResponse> call, Response<PaymentInfoResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            PaymentInfoResponse paymentInfoResponse = response.body();
                            if (!paymentInfoResponse.isError()){
                                startPayment(buyCoinTransResponse, paymentInfoResponse);
                            }else {
                                Toast.makeText(AddCoinActivity.this, ""+paymentInfoResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }

                        dismissWaitingDialog();
                    }

                    @Override
                    public void onFailure(Call<PaymentInfoResponse> call, Throwable t) {
                        dismissWaitingDialog();
                        Log.e("RetrofitError", t.getMessage());
                    }
                });
    }

    private void startPayment(BuyCoinTransResponse buyCoinTransResponse, PaymentInfoResponse paymentInfoResponse) {
        dismissWaitingDialog();


        trans_ref = null;
        amount = 0;

        if (paymentInfoResponse.getPayment_info().size()<1){
            return;
        }
        Toast.makeText(this, buyCoinTransResponse.transaction_ref, Toast.LENGTH_SHORT).show();

        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(paymentInfoResponse.getPayment_info().get(0).getAccount_id())
                .setPayeeName("Try3x")
                .setTransactionId(buyCoinTransResponse.transaction_id)
                .setTransactionRefId(buyCoinTransResponse.getTransaction_ref())
                .setDescription("Buy Coin")
                .setAmount(String.valueOf(buyCoinTransResponse.amount))
                .build();

        easyUpiPayment.setPaymentStatusListener(this);
        easyUpiPayment.setDefaultPaymentApp(PaymentApp.PAYTM);
        trans_ref = buyCoinTransResponse.transaction_ref;
        amount = buyCoinTransResponse.amount;
        easyUpiPayment.startPayment();
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
        if (dialog!=null && !dialog.isShowing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (dialog!=null && dialog.isShowing()){
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

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        coinPackage = null;
        String response = new Gson().toJson(transactionDetails);
        Toast.makeText(this, "onCompleted", Toast.LENGTH_SHORT).show();
        Log.d("UpiPayment", "OnComplete");
        Log.d("UpiPayment", "OnComplete: "+response);
       /* Toast.makeText(this, "Status: "+transactionDetails.getStatus()+" amount: "+transactionDetails.getAmount(), Toast.LENGTH_LONG).show();
        if (transactionDetails.getStatus().equals("success")){
            addBuyCoin(transactionDetails.getTransactionRefId(), Float.parseFloat(transactionDetails.getAmount()));
        }*/
    }

    @Override
    public void onTransactionSuccess() {
        clearSelectItem();

        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

        if (trans_ref!=null && amount>0){
            addBuyCoin(trans_ref, amount);
        }

    }

    @Override
    public void onTransactionSubmitted() {
        clearSelectItem();

        Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionFailed() {
        clearSelectItem();

        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionCancelled() {

        Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAppNotFound() {
        clearSelectItem();
        Toast.makeText(this, "App not found", Toast.LENGTH_SHORT).show();

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
        }
        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );
            String bal = new Gson().toJson(transactionResponse);
            Log.d("PayMentResult", bal);
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){
                    //Success Transaction
                    Log.d("PayMentResult", "Success");
                } else{
                    //Failure Transaction
                    Log.d("PayMentResult", "Faild");
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
            }  /*else if (resultModel != null && resultModel.getError() != null) {
                Log.d("AddCoinActivity", "Error response : " + resultModel.getError().getTransactionResponse());
            }*/ else {
                Log.d("AddCoinActivity", "Both objects are null!");
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


    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam, String txid,
                                                                                            float amount, String productinfo, String firstname, String email) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append("HTS9ppAl" + "|");
        stringBuilder.append(txid + "|");
        stringBuilder.append(amount + "|");
        stringBuilder.append(productinfo + "|");
        stringBuilder.append(firstname + "|");
        stringBuilder.append(email + "|");
        stringBuilder.append("" + "|");
        stringBuilder.append("" + "|");
        stringBuilder.append("" + "|");
        stringBuilder.append("" + "|");
        stringBuilder.append("" + "||||||");


        stringBuilder.append("W4aG3jVEHf");

        String hash = hashCal(stringBuilder.toString());
        Log.d("Sha512 Client", hash);
        Log.d("Sha512 Client", stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
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
                getTransRef();
            }else {
                Toast.makeText(AddCoinActivity.this, "Please Select Any Package", Toast.LENGTH_SHORT).show();
            }
        }
    }
}