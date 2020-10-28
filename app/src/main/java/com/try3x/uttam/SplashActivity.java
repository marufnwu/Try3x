package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.FileUtils;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Models.ActivityBanner;
import com.try3x.uttam.Models.Apk;
import com.try3x.uttam.Models.AsyncParam;
import com.try3x.uttam.Models.Response.AppUpdateResponse;
import com.try3x.uttam.Models.Response.addUserResponse;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.Response.ReferUserResponse;
import com.try3x.uttam.Models.UserLogin;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;
import com.try3x.uttam.Services.UpdateServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final int READ_STORAGE_REQUEST_CODE = 2522;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private static final int GMAIL_SIGN_IN = 1001;
    ProgressBar progressBar;
    Button btnSignIn;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Dialog dialog;
    private String phone;
    private boolean phonIsValid;
    private int gender = 0;
    private boolean isGenderSelected = false;
    private int paymentMethod;
    private boolean isPayMethodSelected  = false;
    private String fullname;
    private String email;
    private boolean fieldError = true;
    private String[] error_desc;
    private String payid;
    private String deepLinkReferCode;
    private ReferUserResponse referUser;
    private boolean haveReferCode = false;
    private Dialog regDialog;
    private Dialog updateDialog;
    private UpdateServices updateServices;
    LocalBroadcastManager localBroadcastManager;
    private ProgressDialog mPDialog;
    String activity = null;
    boolean isActivityCreatedByNoti = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity = getIntent().getStringExtra(Common.ACTIVITY);
        isActivityCreatedByNoti = getIntent().getBooleanExtra(Common.ACTIVITY_CREATED_BY_NOTI, false);
        mAuth = FirebaseAuth.getInstance();
        Paper.init(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);



        initViews();
        createDialog();
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

    private void initViews() {
        btnSignIn = findViewById(R.id.btnSignIn);
        progressBar = findViewById(R.id.progress);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gmailSignIn();
            }
        });
    }
    private void gmailSignIn() {
        showWaitingDialog();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GMAIL_SIGN_IN);
    }



    @Override
    protected void onStart() {
        super.onStart();

        checkUpdate();


    }

    @Override
    protected void onResume() {
        super.onResume();
        localBroadcastManager.registerReceiver(appUpdateReceiver, new IntentFilter(Common.APP_UPDATE_REEIVER));
        localBroadcastManager.registerReceiver(updateProgressReceiver, new IntentFilter(Common.APP_UPDATE_PROGRESS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(appUpdateReceiver);
        localBroadcastManager.unregisterReceiver(updateProgressReceiver);
    }

    private void checkUpdate() {
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getAppUpdate()
                .enqueue(new Callback<AppUpdateResponse>() {
                    @Override
                    public void onResponse(Call<AppUpdateResponse> call, Response<AppUpdateResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            AppUpdateResponse appUpdateResponse = response.body();
                            if (!appUpdateResponse.isError()){
                                Apk apk = appUpdateResponse.getApk();
                                PackageInfo pInfo = null;
                                try {
                                    pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                    String version = pInfo.versionName;
                                    int versionCode = pInfo.versionCode;

                                    Log.d("versionCode", "App "+versionCode+" Server "+apk.vCode);
                                    if (apk.vCode<=versionCode){
                                        moveForward();
                                    }else {
                                        showUpdateDialog(apk);
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                Toast.makeText(SplashActivity.this, appUpdateResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AppUpdateResponse> call, Throwable t) {

                    }
                });
    }

    private void showUpdateDialog(final Apk apk) {

        updateDialog = new Dialog(SplashActivity.this);
        updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (apk.mandatory){
            updateDialog.setCancelable(false);
        }else {
            updateDialog.setCancelable(true);
        }

        updateDialog.setContentView(R.layout.dialog_update_layout);

        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = updateDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button update = updateDialog.findViewById(R.id.btnUpdate);
        TextView txtDesc = updateDialog.findViewById(R.id.txtDesc);
        ImageView imgLiveChat = updateDialog.findViewById(R.id.imgLiveChat);
        ImageView imgDynamic = updateDialog.findViewById(R.id.imgDynamic);

        getDynamicBanner(imgDynamic, "UpdateDialog");
        imgLiveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.openLiveChat(getApplicationContext());
            }
        });

        if (apk.desc!=null && apk.desc.length()>5){
            txtDesc.setText(apk.desc);
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };

                if (!hasPermissions(SplashActivity.this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else {
                    startDownload(apk);
                }

//                if (checkPermission()){
//                    startDownload(apk);
//                }else {
//                    requestPermission();
//                }
            }
        });
        updateDialog.show();
    }

    private void startDownload(Apk apk) {
//       downloadApp downloadApp = new downloadApp();
//       downloadApp.setContext(SplashActivity.this);
//       downloadApp.execute(apk);
        createUpdateProgress();
        updateServices = new UpdateServices();

        Intent intent = new Intent(SplashActivity.this, updateServices.getClass());
        intent.putExtra("apk", apk);
        if (!isMyServiceRunning(updateServices.getClass())) {
            startService(intent);
        }else {
            Toast.makeText(this, "App Is Updating", Toast.LENGTH_SHORT).show();
        }



    }

    private void createUpdateProgress() {
        mPDialog = new ProgressDialog(SplashActivity.this);
        mPDialog.setMessage("Updating...");
        mPDialog.setIndeterminate(true);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPDialog.setCancelable(false);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    private void moveForward() {

        if (mAuth.getCurrentUser()!=null){
            btnSignIn.setVisibility(View.GONE);
            //checkLogin(mAuth.getCurrentUser());
            gmailSignIn();
        }else {
            progressBar.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void checkLogin(final FirebaseUser user, final String fcmToken) {
        GmailInfo gmailInfo = Paper.book().read(PaperDB.GMAILINFO);
        if (gmailInfo==null){
            Toast.makeText(this, "Login Again", Toast.LENGTH_SHORT).show();
            return;
        }
        String sha1 = Common.getKeyHash(SplashActivity.this);
        IRetrofitApiCall iRetrofitApiCall = RetrofitClient.getRetrofit().create(IRetrofitApiCall.class);
        iRetrofitApiCall.isUserExits(
                sha1,
                user.getEmail(),
                gmailInfo.user_id,
                fcmToken
        )
                .enqueue(new Callback<UserLogin>() {
                    @Override
                    public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            dismissWaitingDialog();
                            UserLogin userLogin = response.body();
                            if (!userLogin.isError()){
                                if (userLogin.isAccountExits()){
                                    //account already created, so we mwy now go to main activity
                                    if (userLogin.user==null){
                                        Toast.makeText(SplashActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Paper.book().write(PaperDB.USER_PROFILE, userLogin.user);
                                    dismissWaitingDialog();
                                    gotoMainActivity();
                                }else {
                                    //account not created, so now we show information dialog
                                    Toast.makeText(SplashActivity.this, "User not Exits", Toast.LENGTH_SHORT).show();


                                    showInfoDialog(user, fcmToken);
                                }
                            }else {
                                mAuth.signOut();
                                dismissWaitingDialog();
                                Toast.makeText(SplashActivity.this, userLogin.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserLogin> call, Throwable t) {
                        Log.d("ErrorRetrofit", t.getMessage());
                        mAuth.signOut();
                        dismissWaitingDialog();
                    }
                });
    }

    private void showInfoDialog(final FirebaseUser user, final String fcmToken) {
        regDialog = new Dialog(SplashActivity.this);
        regDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        regDialog.setCancelable(false);
        regDialog.setContentView(R.layout.layout_reg_info_dialog);

        regDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = regDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText edtFullName = regDialog.findViewById(R.id.edtName);
        final EditText edtEmail = regDialog.findViewById(R.id.edtEmail);
        final EditText edtPayId = regDialog.findViewById(R.id.edtPayId);
        final EditText edtPhone = regDialog.findViewById(R.id.edtPhone);
        final CountryCodePicker countryCodePicker = regDialog.findViewById(R.id.cpp);

        final Button btnSelectGender = regDialog.findViewById(R.id.btnSelectGender);
        final Button btnPayMethod = regDialog.findViewById(R.id.btnSelectPayMethod);
        final LinearLayout layoutRefer = regDialog.findViewById(R.id.layoutRefer);
        final TextView txtReferBy = regDialog.findViewById(R.id.txtReferBy);

        CheckBox chckBoxRefer = regDialog.findViewById(R.id.chckBoxRefer);
        final EditText edtReferBy = regDialog.findViewById(R.id.edtReferBy);

        Button btnContinue = regDialog.findViewById(R.id.btnContinue);
        ImageView imgLiveChat = regDialog.findViewById(R.id.imgLiveChat);
        ImageView imgDynamic = regDialog.findViewById(R.id.imgDynamic);

        getDynamicBanner(imgDynamic, "SignupDialog");
        imgLiveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.openLiveChat(getApplicationContext());
            }
        });

        edtEmail.setText(user.getEmail());

        edtFullName.setText(user.getDisplayName());

        regDialog.show();






        edtFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fullname = s.toString();
                if (fullname.length()<4){
                    fieldError = true;
                    edtFullName.setError("Please enter valid name");
                }else {
                    fieldError = false;
                }
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString();
                if (s.length()>0 && email.matches(emailPattern)){
                    fieldError = false;
                }else {
                    fieldError = true;
                    edtEmail.setError("Email Pattern Not Matched");
                }
            }
        });


        countryCodePicker.registerPhoneNumberTextView(edtPhone);

        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString();
                if (!countryCodePicker.isValid()){
                    phonIsValid = false;
                    edtPhone.setError("Phone Num Not Valid");
                    fieldError = true;
                }else {
                    fieldError = false;
                    phonIsValid = true;

                }
            }
        });

        edtPayId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                payid = s.toString();
                if (s.length()>0){
                    fieldError = false;
                }else {
                    fieldError = true;

                }
            }
        });

        btnSelectGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceGender();
            }

            private void choiceGender() {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(SplashActivity.this);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SplashActivity.this, android.R.layout.simple_list_item_1);

                arrayAdapter.add("Male");
                arrayAdapter.add("Female");
                arrayAdapter.add("Other");
                builderSingle.setTitle("Select Gender");


                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        isGenderSelected = true;

                        if (which==0){
                            gender = 1;
                        }else if (which==1){
                            gender = 2;
                        }else if (which==2){
                            gender = 0;
                        }else {
                            gender = 0;
                        }

                        btnSelectGender.setText("Gender: "+ Common.getGender(gender));
                    }
                });

                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                AlertDialog d = builderSingle.create();
                d.show();
            }
        });

        btnPayMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choicePayMethod();
            }

            private void choicePayMethod() {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(SplashActivity.this);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SplashActivity.this, android.R.layout.simple_list_item_1);

                arrayAdapter.add("Paytm");
                arrayAdapter.add("Gpay");
                arrayAdapter.add("PhonPay");
                builderSingle.setTitle("Select Payment Method");


                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        isPayMethodSelected = true;
                        edtPayId.setVisibility(View.VISIBLE);

                        if (which==0){
                            paymentMethod = 1;
                        }else if (which==1){
                            paymentMethod = 2;
                        }else if (which==2){
                            paymentMethod = 3;
                        }else {
                            paymentMethod = 0;
                        }

                        btnPayMethod.setText("Pay. Method: "+ Common.getPaymentMethod(paymentMethod));
                    }
                });

                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                AlertDialog d = builderSingle.create();
                d.show();
            }
        });

        chckBoxRefer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    edtReferBy.setVisibility(View.VISIBLE);
                    haveReferCode = true;
                }else{
                    edtReferBy.setVisibility(View.GONE);
                    haveReferCode =false;
                }
            }
        });


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean error = false;

                if (edtFullName.getText().toString().length()<4){
                    error = true;
                    edtFullName.setError("Enter Valid Name");
                }


                if (edtPayId.getText().toString().length()<4){
                    error = true;
                    edtPayId.setError("Enter Valid Payid");
                }
                if (!isGenderSelected){
                    error =true;
                    Toast.makeText(SplashActivity.this, "Select Gender", Toast.LENGTH_SHORT).show();
                }

                if (!isPayMethodSelected){
                    error = true;
                    Toast.makeText(SplashActivity.this, "Select Payment Method", Toast.LENGTH_SHORT).show();
                }

                if (!phonIsValid){
                    error = true;
                    edtPhone.setError("Enter Valid Phone");
                }
                String referBy = edtReferBy.getText().toString();
                if (haveReferCode){
                    if (edtReferBy.getText().toString().length()<6){
                        error = true;
                        edtReferBy.setError("Enter Refer Code");
                    }
                }

                if (!error){
                    addUserToDB(user, edtFullName.getText().toString(), edtPhone.getText().toString(), gender, paymentMethod, payid, fcmToken, haveReferCode, referBy);
                }
            }
        });



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

    private void addUserToDB(final FirebaseUser user, final String name, final String phone, final int gender, final int paymentMethod, final String payid, final String fcmToken, boolean haveRefer, String referCode) {
        showWaitingDialog();

        final GmailInfo gmailInfo = Paper.book().read(PaperDB.GMAILINFO);
        showWaitingDialog();

        IRetrofitApiCall iRetrofitApiCall = RetrofitClient.getRetrofit().create(IRetrofitApiCall.class);
        iRetrofitApiCall.addUser(
                Common.getKeyHash(SplashActivity.this),
                user.getEmail(),
                gmailInfo.user_id,
                name,
                user.getPhotoUrl().toString(),
                phone,
                gender,
                paymentMethod,
                payid,
                haveRefer,
                fcmToken,
                referCode
        )
                .enqueue(new Callback<addUserResponse>() {
                    @Override
                    public void onResponse(Call<addUserResponse> call, Response<addUserResponse> response) {

                        if (response.isSuccessful() && response.body()!=null){

                            addUserResponse addUserResponse = response.body();
                            if (!addUserResponse.error){

                               checkLogin(user, fcmToken);
                            }else {
                                dismissWaitingDialog();
                                Toast.makeText(SplashActivity.this, addUserResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            dismissWaitingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<addUserResponse> call, Throwable t) {
                        dismissWaitingDialog();
                        Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });





    }

    private void gotoMainActivity() {
        Intent intent = null;
        Context context = SplashActivity.this;
        if (activity!=null){
            if (activity.equals("MainActivity")){
                intent = new Intent(context, MainActivity.class);
            }else if (activity.equals("MyBajiListActivity")){
                intent = new Intent(context, MyBajiListActivity.class);
            }else if (activity.equals("MyCoinActivity")){
                intent = new Intent(context, MyCoinActivity.class);
            }else if (activity.equals("MyCommisionActivity")){
                intent = new Intent(context, MyCommisionActivity.class);
            }else if (activity.equals("MyWithrawbleActivity")){
                intent = new Intent(context, MyWithrawbleActivity.class);
            }else if (activity.equals("PayoutActivity")){
                intent = new Intent(context, PayoutActivity.class);
            }else if (activity.equals("UserProfileActivity")){
                intent = new Intent(context, UserProfileActivity.class);
            }else if (activity.equals("AddCoinActivity")){
                intent = new Intent(context, AddCoinActivity.class);
            }else if (activity.equals("ResultListActivity")){
                intent = new Intent(context, ResultListActivity.class);
            }else {
                intent = new Intent(context, MainActivity.class);
            }

        }else {
            intent = new Intent(context, MainActivity.class);
        }
        intent.putExtra(Common.ACTIVITY_CREATED_BY_NOTI, isActivityCreatedByNoti);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GMAIL_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);



            } catch (ApiException e) {
                dismissWaitingDialog();
                // Google Sign In failed, update UI appropriately
                Log.w("SplashActivity", "Google sign in fail", e);
                // [START_EXCLUDE]
                // updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }


    // [END onactivityresult]

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("IdToken", acct.getIdToken());
                            GmailInfo gmailInfo = new GmailInfo();
                            gmailInfo.access_token = acct.getIdToken();
                            gmailInfo.gmail = acct.getEmail();
                            gmailInfo.user_id = acct.getId();


                            Paper.book().write(PaperDB.GMAILINFO, gmailInfo);
                            Log.d("SplashActivity", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            getFcmToken(user);



                        } else {
                            dismissWaitingDialog();
                            // If sign in fails, display a message to the user.
                            Log.w("SplashActivity", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SplashActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();


                        }

                        // [START_EXCLUDE]
                        //  hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void getFcmToken(final FirebaseUser user) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("SplashActivity", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("TOken", token);
                        checkLogin(user, token);

                    }
                });


    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(SplashActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST_CODE);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    public BroadcastReceiver appUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status", -1);
            if (status==1){
                if (mPDialog!=null && !mPDialog.isShowing()){
                    mPDialog.show();
                }
                //start updating
                Log.d("UpdateStatus", "Started");
            }else if (status==2){
                //start updating

                Log.d("UpdateStatus", "Completed");
            }else if (status==0){
                //start updating
                Log.d("UpdateStatus", "Failed");
            }
        }
    };

    public BroadcastReceiver updateProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            if (mPDialog != null) {
                if (!mPDialog.isShowing()){
                    Log.d("UpdateStatus", "dialog  not showing"+progress);
                    mPDialog.show();


                }else {
                    Log.d("UpdateStatus", "dialog  showing"+progress);

                }
                mPDialog.setIndeterminate(false);
                mPDialog.setMax(100);
                mPDialog.setProgress(progress);
            }else {
                Log.d("UpdateStatus", "dialog null"+progress);

                createUpdateProgress();
            }
        }
    };
}