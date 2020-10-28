package com.try3x.uttam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.try3x.uttam.Adapters.PaymentMethodAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.PayMethodInfo;
import com.try3x.uttam.Models.PaymentMethod;
import com.try3x.uttam.Models.Response.PaymentMethodResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.Models.Response.SinglePayMethodResponse;
import com.try3x.uttam.Models.Response.UserPayMethodListResponse;
import com.try3x.uttam.Models.User;
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

public class UserProfileActivity extends AppCompatActivity {

    ImageView imgBack, imgProfilePic,imgAddPayMethod,imgReferCopy;

    Button btnInvite;
    TextView txtReaload,txtUserName, txtUserEmail, txtUserNumber, txtUserCountry, txtUserGender, txtName, txtEmail, txtUserJoin, txtUserReferCode;
    RecyclerView recyclerPayMethod;
    ProgressBar payMethodProgress;
    LinearLayout layoutReload;
    User userProfile;
    private ACProgressPie dialog;
    private Dialog payMethodDialog;
    GmailInfo gmailInfo;
    FirebaseAuth mAuth;
    private PayMethodInfo payMethodInfo;
    private List<PaymentMethod> paymentMethodList = new ArrayList<>();
    private int methodSelected = 0;
    private List<PayMethodInfo> payMethodInfoList =new ArrayList<>();
    private PaymentMethodAdapter methodAdapter;
    private boolean isActivityCreatedByNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        isActivityCreatedByNoti = getIntent().getBooleanExtra(Common.ACTIVITY_CREATED_BY_NOTI, false);
        initViews();
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);
        createDialog();
        setProfile();
        loadPaymMethodList();
    }

    private void loadPaymMethodList() {
        payMethodProgress.setVisibility(View.VISIBLE);
        layoutReload.setVisibility(View.GONE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPayMethodList(
                        Common.getKeyHash(UserProfileActivity.this),
                        gmailInfo.gmail,
                        gmailInfo.user_id,
                        gmailInfo.access_token
                )
                .enqueue(new Callback<UserPayMethodListResponse>() {
                    @Override
                    public void onResponse(Call<UserPayMethodListResponse> call, Response<UserPayMethodListResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            payMethodProgress.setVisibility(View.GONE);
                            UserPayMethodListResponse userPayMethodListResponse = response.body();

                            if (!userPayMethodListResponse.isError()){
                                payMethodInfoList.clear();
                                payMethodInfoList = userPayMethodListResponse.methodList;
                                methodAdapter = new PaymentMethodAdapter(getApplicationContext(), payMethodInfoList);
                                recyclerPayMethod.setAdapter(methodAdapter);
                                if (payMethodInfoList.size()<1){
                                    layoutReload.setVisibility(View.VISIBLE);
                                }

                            }else {
                                layoutReload.setVisibility(View.VISIBLE);
                                Toast.makeText(UserProfileActivity.this, ""+userPayMethodListResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserPayMethodListResponse> call, Throwable t) {
                        payMethodProgress.setVisibility(View.GONE);
                        layoutReload.setVisibility(View.VISIBLE);

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
            startActivity(new Intent(UserProfileActivity.this, SplashActivity.class));
            finish();
        }
        dismissWaitingDialog();
    }

    private void setProfile() {
        userProfile = Paper.book().read(PaperDB.USER_PROFILE);
        if (userProfile!=null){
            Glide.with(this)
                    .load(userProfile.photo_url)
                    .into(imgProfilePic);

            txtUserName.setText(userProfile.name);
            txtName.setText(userProfile.name);

            txtUserEmail.setText(userProfile.email);
            txtEmail.setText(userProfile.email);
            txtUserNumber.setText(userProfile.phone);
            txtUserCountry.setText(userProfile.country);

            txtUserGender.setText(Common.getGender(userProfile.gender));
            txtUserJoin.setText(Common.date(userProfile.acc_created_at));
            txtUserReferCode.setText(userProfile.referCode);
        }
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgProfilePic = findViewById(R.id.imgProfilePic);
        btnInvite = findViewById(R.id.btnInvite);
        txtReaload = findViewById(R.id.txtReaload);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtUserNumber = findViewById(R.id.txtUserNumber);
        txtUserCountry = findViewById(R.id.txtUserCountry);
        txtUserGender = findViewById(R.id.txtUserGender);
        txtUserReferCode = findViewById(R.id.txtUserReferCode);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        recyclerPayMethod = findViewById(R.id.recyclerPayMethod);
        payMethodProgress = findViewById(R.id.payMethodProgress);
        layoutReload = findViewById(R.id.layoutReload);
        txtUserJoin = findViewById(R.id.txtUserJoin);
        imgAddPayMethod = findViewById(R.id.imgAddPayMethod);

        imgReferCopy = findViewById(R.id.imgReferCopy);

        imgAddPayMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPayMethodDialog();
            }
        });

        recyclerPayMethod.setLayoutManager(new LinearLayoutManager(this));
        recyclerPayMethod.setHasFixedSize(true);


        txtReaload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPaymMethodList();
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = Paper.book().read(PaperDB.USER_PROFILE);
                if (user!=null && user.referCode!=null){
                    invite(UserProfileActivity.this, user.referCode);
                }else {
                    Toast.makeText(UserProfileActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgReferCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringYouExtracted = txtUserReferCode.getText().toString();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Refer Code", stringYouExtracted);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(UserProfileActivity.this, "Refer Code Copied", Toast.LENGTH_SHORT).show();
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


    private void showAddPayMethodDialog() {
        payMethodDialog = new Dialog(UserProfileActivity.this);
        payMethodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payMethodDialog.setCancelable(true);
        payMethodDialog.setContentView(R.layout.dialog_add_pay_method);

        payMethodDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = payMethodDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Spinner spinner = payMethodDialog.findViewById(R.id.spinnerPaymethod);
        Button btnCancel = payMethodDialog.findViewById(R.id.btnCancel);
        final Button btnAdd = payMethodDialog.findViewById(R.id.btnPayout);

        final EditText edtPhn1 = payMethodDialog.findViewById(R.id.payNum1);
        final EditText edtPhn2 = payMethodDialog.findViewById(R.id.payNum2);
        final TextView txtName1 = payMethodDialog.findViewById(R.id.txtName1);
        final TextView txtName2 = payMethodDialog.findViewById(R.id.txtName2);

        payMethodDialog.show();
        showWaitingDialog();
        paymentMethodList.clear();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPaymentMethod()
                .enqueue(new Callback<PaymentMethodResponse>() {
                    @Override
                    public void onResponse(Call<PaymentMethodResponse> call, Response<PaymentMethodResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            PaymentMethodResponse PaymentMethodResponse = response.body();
                            if (!PaymentMethodResponse.isError()){
                                paymentMethodList = PaymentMethodResponse.payMethodList;
                                setMethodToSpinner(PaymentMethodResponse.payMethodList);
                            }else {
                                Toast.makeText(UserProfileActivity.this, PaymentMethodResponse.getError_description(), Toast.LENGTH_SHORT).show();
                            }
                        }
                       dismissWaitingDialog();
                    }

                    private void setMethodToSpinner(final List<PaymentMethod> payMethodList) {
                        ArrayAdapter arrayAdapter = new ArrayAdapter(UserProfileActivity.this, android.R.layout.simple_spinner_dropdown_item);
                        arrayAdapter.add("Select Payment Method");
                        for (PaymentMethod method : payMethodList){
                            arrayAdapter.add(method.name);
                        }

                        spinner.setAdapter(arrayAdapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                                payMethodInfo = null;
                                methodSelected = pos;
                                if (pos>0){
                                    String text = spinner.getSelectedItem().toString();
                                    txtName1.setText("Enter "+text+" Number");
                                    txtName2.setText("Enter "+text+" Number Again");
                                    showWaitingDialog();
                                    edtPhn1.setEnabled(true);
                                    edtPhn2.setEnabled(true);
                                    int methodPos = pos-1;
                                    int methodId = payMethodList.get(methodPos).getId();

                                    RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                                            .getMyPayMethodById(
                                                    Common.getKeyHash(UserProfileActivity.this),
                                                    gmailInfo.gmail,
                                                    gmailInfo.user_id,
                                                    gmailInfo.access_token,
                                                    methodId
                                            )
                                            .enqueue(new Callback<SinglePayMethodResponse>() {
                                                @Override
                                                public void onResponse(Call<SinglePayMethodResponse> call, Response<SinglePayMethodResponse> response) {
                                                    if (response.isSuccessful() && response.body()!=null){
                                                        SinglePayMethodResponse singlePayMethodResponse = response.body();
                                                        if (!singlePayMethodResponse.isError()){
                                                            btnAdd.setText("UPDATE");
                                                            payMethodInfo = singlePayMethodResponse.getInfo();
                                                            edtPhn1.setText(payMethodInfo.pay_number);
                                                            edtPhn2.setText(payMethodInfo.pay_number);
                                                        }else {
                                                            btnAdd.setText("ADD");
                                                            payMethodInfo = null;
                                                            edtPhn1.setText("");
                                                            edtPhn2.setText("");
                                                        }
                                                    }

                                                    dismissWaitingDialog();
                                                }



                                                @Override
                                                public void onFailure(Call<SinglePayMethodResponse> call, Throwable t) {
                                                    dismissWaitingDialog();
                                                    Toast.makeText(UserProfileActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }else {
                                    edtPhn1.setEnabled(false);
                                    edtPhn2.setEnabled(false);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                payMethodInfo = null;
                            }
                        });

                    }


                    @Override
                    public void onFailure(Call<PaymentMethodResponse> call, Throwable t) {
                        dismissWaitingDialog();
                        Toast.makeText(UserProfileActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (methodSelected>0){
                        String num1 = edtPhn1.getText().toString();
                        String num2 = edtPhn2.getText().toString();
                        PaymentMethod paymentMethod = paymentMethodList.get(methodSelected-1);

                        if (num1.equals(num2)){
                            String regx = "^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$";
                            if (num1.matches(regx)){

                                PayMethodInfo payMethodInfo = new PayMethodInfo();
                                payMethodInfo.pay_method_id = paymentMethod.getId();
                                payMethodInfo.pay_method_name = paymentMethod.getName();
                                payMethodInfo.pay_number = num1;
                                payMethodInfo.user_email = gmailInfo.gmail;
                                payMethodInfo.user_id = gmailInfo.user_id;
                                showWaitingDialog();
                                RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                                        .addPayMethod(
                                                Common.getKeyHash(getApplicationContext()),
                                                gmailInfo.gmail,
                                                gmailInfo.user_id,
                                                gmailInfo.access_token,
                                                payMethodInfo.pay_method_id,
                                                payMethodInfo.pay_number
                                        )
                                        .enqueue(new Callback<ServerResponse>() {
                                            @Override
                                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                                if (response.isSuccessful() && response.body()!=null){
                                                    dismissWaitingDialog();
                                                    if (!response.body().error){
                                                        Toast.makeText(UserProfileActivity.this, ""+response.body().error_description, Toast.LENGTH_SHORT).show();

                                                    }else {
                                                        Toast.makeText(UserProfileActivity.this, ""+response.body().error_description, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                                Toast.makeText(UserProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d("RetrofitError", t.getMessage());
                                                dismissWaitingDialog();
                                            }
                                        });
                            }else {
                                Toast.makeText(UserProfileActivity.this, "Number Not Valid", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(UserProfileActivity.this, "Number Not Matched", Toast.LENGTH_SHORT).show();

                        }

                        Log.d("Paymethod", paymentMethod.name+" "+paymentMethod.id);
                    }else {

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
        if (!isFinishing() && dialog!=null && dialog.isShowing()){
            dialog.dismiss();
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
}