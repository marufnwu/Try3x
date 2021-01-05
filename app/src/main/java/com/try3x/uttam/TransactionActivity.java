package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.try3x.uttam.Adapters.MyBajiListAdapter;
import com.try3x.uttam.Adapters.TransactionsAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.MyBajiList;
import com.try3x.uttam.Models.Response.TransactionResponse;
import com.try3x.uttam.Models.Transaction;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionActivity extends AppCompatActivity {
    int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;

    private int PAGE = 1, PAGE_SIZE = 30, TOTAL_PAGE = 0;
    boolean isLoading = true;

    private GmailInfo gmailInfo;
    RecyclerView recyclerTransactions;
    private ProgressBar postListProgress;
    LinearLayout layoutReload;
    private LinearLayoutManager layoutManager;
    private ACProgressPie dialog;
    private List<Transaction> trnsactions = new ArrayList<>();
    private TransactionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);


        Paper.init(this);
        createDialog();
        gmailInfo  = Paper.book().read(PaperDB.GMAILINFO);

        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        recyclerTransactions.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerTransactions.setLayoutManager(layoutManager);

        postListProgress = findViewById(R.id.progress);
        layoutReload = findViewById(R.id.layoutReload);
        initRecyclerPagination();
        getTransactions();

    }

    private void getTransactions() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getTransactionList(
                        Common.getKeyHash(getApplicationContext()),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<TransactionResponse>() {
                    @Override
                    public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                        isLoading = false;
                        if (response.isSuccessful()&& response.body()!=null){
                            TransactionResponse transactionResponse = response.body();
                            trnsactions = transactionResponse.transactions;
                            if (!transactionResponse.error){
                                TOTAL_PAGE = transactionResponse.totalPage;

                                adapter = new TransactionsAdapter(TransactionActivity.this,trnsactions);
                                recyclerTransactions.setAdapter(adapter);
                                dismissWaitingDialog();

                            }else {


                                dismissWaitingDialog();
                                Toast.makeText(TransactionActivity.this, ""+transactionResponse.error_description, Toast.LENGTH_SHORT).show();
                            }


                            if (trnsactions.size()<=0){
                                layoutReload.setVisibility(View.VISIBLE);
                            }else {
                                layoutReload.setVisibility(View.GONE);
                            }

                        }else {

                            dismissWaitingDialog();
                        }



                    }

                    @Override
                    public void onFailure(Call<TransactionResponse> call, Throwable t) {
                        Log.d("RetrofitError", t.getMessage());
                        isLoading = false;
                        postListProgress.setVisibility(View.GONE);
                        layoutReload.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void initRecyclerPagination() {

        recyclerTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                .getTransactionList(
                        Common.getKeyHash(TransactionActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token,
                        true,
                        PAGE,
                        PAGE_SIZE)
                .enqueue(new Callback<TransactionResponse>() {
                    @Override
                    public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {

                        if (response.isSuccessful()&& response.body()!=null){
                            TransactionResponse transactionResponse = response.body();
                            if (!transactionResponse.error){
                                PAGE_SIZE = transactionResponse.totalPage;
                                if (transactionResponse.transactions.size()>0){
                                    isLoading = false;

                                    trnsactions.addAll(transactionResponse.transactions);
                                    adapter.notifyDataSetChanged();
                                }

                            }else {
                                isLoading = false;

                                Toast.makeText(TransactionActivity.this, ""+transactionResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            isLoading = false;

                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionResponse> call, Throwable t) {
                        Log.d("RetrofitError", t.getMessage());
                        isLoading = false;
                        postListProgress.setVisibility(View.GONE);
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
        if (!isFinishing() && dialog!=null && !dialog.isShowing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (!isFinishing() && dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}