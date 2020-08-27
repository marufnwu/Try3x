package com.try3x.uttam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
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
import com.try3x.uttam.Models.Response.BuyCoinTransResponse;
import com.try3x.uttam.Models.Response.CoinPackageResponse;
import com.try3x.uttam.Models.Response.PaymentInfoResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCoinActivity extends AppCompatActivity implements OnCoinPackClickListener, PaymentStatusListener {

    RecyclerView recyclerCoinPack;
    ProgressBar progress;
    LinearLayout layoutReload;

    TextView txtPackName, txtPackPrice, txtPackCoin,txtPayAmount;
    GmailInfo gmailInfo;
    FirebaseAuth mAuth;

    CoinPackAdapter coinPackAdapter;
    CoinPackage coinPackage;
    private ACProgressPie dialog;
    private String trans_ref;
    private float amount;
    LinearLayout layPack;
    ImageView imgSale;
    ImageView imgPaytm,imgGpay,imgPhonpe,imgAmazonPay,imgBhmi,imgJiomoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coin);

        initView();
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);

        mAuth = FirebaseAuth.getInstance();
        createDialog();
        coinPackage = new CoinPackage();

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

        imgPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coinPackage!=null && coinPackage.price>0){
                    getTransRef();
                }else {
                    Toast.makeText(AddCoinActivity.this, "Please Select Any Package", Toast.LENGTH_SHORT).show();
                }
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
                        coinPackage.coin
                )
                .enqueue(new Callback<BuyCoinTransResponse>() {
                    @Override
                    public void onResponse(Call<BuyCoinTransResponse> call, Response<BuyCoinTransResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            BuyCoinTransResponse buyCoinTransResponse = response.body();
                            if (!buyCoinTransResponse.isError()){
                                //startPayment(buyCoinTransResponse.transaction_ref);
                                fetchPaymnetInfo(buyCoinTransResponse);
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
       txtPayAmount.setText(String.valueOf("Pay: "+this.coinPackage.price+" Rp."));
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
}