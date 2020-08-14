package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Models.Response.addUserResponse;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.Response.ReferUserResponse;
import com.try3x.uttam.Models.UserLogin;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        Paper.init(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
        if (dialog!=null && !dialog.isShowing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (dialog!=null && dialog.isShowing()){
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


        if (mAuth.getCurrentUser()!=null){
            btnSignIn.setVisibility(View.GONE);
            //checkLogin(mAuth.getCurrentUser());
            gmailSignIn();
        }else {
            progressBar.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);

        }

    }

    private void checkLogin(final FirebaseUser user) {
        GmailInfo gmailInfo = Paper.book().read(PaperDB.GMAILINFO);
        if (gmailInfo==null){
            Toast.makeText(this, "Login Again", Toast.LENGTH_SHORT).show();
            return;
        }
        String sha1 = Common.getKeyHash(SplashActivity.this);
        IRetrofitApiCall iRetrofitApiCall = RetrofitClient.getRetrofit().create(IRetrofitApiCall.class);
        iRetrofitApiCall.isUserExits(sha1, user.getEmail(), gmailInfo.user_id)
                .enqueue(new Callback<UserLogin>() {
                    @Override
                    public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                        if (response.isSuccessful() && response.body()!=null){
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

                                    dismissWaitingDialog();
                                    showInfoDialog(user);
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

    private void showInfoDialog(final FirebaseUser user) {
        dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_reg_info_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText edtFullName = dialog.findViewById(R.id.edtName);
        final EditText edtEmail = dialog.findViewById(R.id.edtEmail);
        final EditText edtPayId = dialog.findViewById(R.id.edtPayId);
        final EditText edtPhone = dialog.findViewById(R.id.edtPhone);
        final CountryCodePicker countryCodePicker = dialog.findViewById(R.id.cpp);

        final Button btnSelectGender = dialog.findViewById(R.id.btnSelectGender);
        final Button btnPayMethod = dialog.findViewById(R.id.btnSelectPayMethod);
        final LinearLayout layoutRefer = dialog.findViewById(R.id.layoutRefer);
        final TextView txtReferBy = dialog.findViewById(R.id.txtReferBy);
        Button btnContinue = dialog.findViewById(R.id.btnContinue);

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        edtEmail.setText(user.getEmail());

        edtFullName.setText(user.getDisplayName());



        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)



                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();


                            deepLinkReferCode = deepLink.getQueryParameter("referby");

                            if (deepLinkReferCode!=null){
                                Log.d("Deeplink", deepLinkReferCode);
                                RetrofitClient.getRetrofit()
                                        .create(IRetrofitApiCall.class)
                                        .getReferUser(
                                                Common.getKeyHash(SplashActivity.this),
                                                deepLinkReferCode
                                        )
                                        .enqueue(new Callback<ReferUserResponse>() {
                                            @Override
                                            public void onResponse(Call<ReferUserResponse> call, Response<ReferUserResponse> response) {
                                                if (response.isSuccessful() && response.body()!=null && !response.body().isError()){
                                                    referUser = response.body();
                                                    layoutRefer.setVisibility(View.VISIBLE);
                                                    txtReferBy.setText("Refer By: "+referUser.name);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ReferUserResponse> call, Throwable t) {

                                            }
                                        });

                            }else {
                                Log.d("Deeplink", "Null");

                            }


                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("onFailure", e.getMessage());
                    }
                });

        dialog.show();






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

                if (!error){
                    addUserToDB(user, edtFullName.getText().toString(), edtPhone.getText().toString(), gender, paymentMethod, payid);
                }
            }
        });



    }

    private void addUserToDB(final FirebaseUser user, final String name, final String phone, final int gender, final int paymentMethod, final String payid) {

        dismissWaitingDialog();
        showWaitingDialog();

        final GmailInfo gmailInfo = Paper.book().read(PaperDB.GMAILINFO);
        showWaitingDialog();



        String referLink  ="https://try3x.page.link/?"+
                "link="+"https://play.google.com"+"?referby="+gmailInfo.user_id+
                "&apn="+getPackageName()+
                "&st="+"Install And Earn"+
                "&sd="+"Try3x"+
                "&si="+"https://i.ibb.co/N1RSJQN/splash.jpg";


        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(referLink))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {

                            Log.d("SuccessHolo", task.getResult().getShortLink().toString());
                            // Short link created


                            Uri shortLink  = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            //share(shortLink, shareBtnId);
                            Boolean isReferBy = false;
                            String referUserId = "";
                            String referEmail = "";
                            if (deepLinkReferCode!=null){
                                if (referUser.email!=null){
                                    referUserId = deepLinkReferCode;
                                    isReferBy = true;
                                    referEmail = referUser.email;
                                    Log.d("deeplink", "true");
                                }
                            }

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
                                    String.valueOf(shortLink),
                                    referUserId,
                                    isReferBy,
                                    referEmail
                                    )
                                    .enqueue(new Callback<addUserResponse>() {
                                        @Override
                                        public void onResponse(Call<addUserResponse> call, Response<addUserResponse> response) {
                                            if (response.isSuccessful() && response.body()!=null){
                                                addUserResponse addUserResponse = response.body();
                                                if (!addUserResponse.error){
                                                    dismissWaitingDialog();
                                                    gotoMainActivity();
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



                        } else {
                            Log.e("main", " error "+task.getException() );
                            Toast.makeText(SplashActivity.this, "", Toast.LENGTH_SHORT).show();
                            dismissWaitingDialog();
                        }
                    }
                });





    }

    private void gotoMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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
                            checkLogin(user);



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
}