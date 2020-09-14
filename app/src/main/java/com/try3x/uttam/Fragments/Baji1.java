package com.try3x.uttam.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.try3x.uttam.Adapters.BajiPlaceListAdapter;
import com.try3x.uttam.Adapters.PackagesListAdapter;
import com.try3x.uttam.AddCoinActivity;
import com.try3x.uttam.Common.AddCoinDialog;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Listener.OnPackageItemClickListener;
import com.try3x.uttam.Models.Response.BajiServerBody;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.PackageList;
import com.try3x.uttam.Models.Packages;
import com.try3x.uttam.Models.Response.MyCoinResponse;
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

public class Baji1 extends Fragment implements OnPackageItemClickListener {

    Button btn1, btn2, btn3, btn4, btn5, btnPlaceBaji;
    private Dialog dialog;
    Packages pBtn1, pBtn2, pBtn3, pBtn4, pBtn5;
    BajiServerBody bajiServerBody;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    BajiPlaceListAdapter bajiPlaceListAdapter;
    private ACProgressPie waitingdialog;
    boolean isResultPublish;
    TextView bajiStatus;
    private GmailInfo gmailInfo;

    public Baji1(boolean isResultPublish) {
        this.isResultPublish = isResultPublish;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Paper.init(getContext());
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_baji, null, false);

        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btnPlaceBaji = view.findViewById(R.id.btnPlace);

        bajiStatus = view.findViewById(R.id.bajiStatus);

        if (isResultPublish){
            bajiStatus.setText("Result Published");
            bajiStatus.setBackground(getResources().getDrawable(R.drawable.bg_gra_gray));
            bajiStatus.setTextColor(getResources().getColor(R.color.black));
        }else{
            bajiStatus.setText("Live Now | Result 11:00am");
        }

        bajiServerBody = new BajiServerBody();
        createDialog();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPackages(btn1);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPackages(btn2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPackages(btn3);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPackages(btn4);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPackages(btn5);
            }
        });

        btnPlaceBaji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Packages packages : bajiServerBody.getPackageList()){
                    Log.d("Packages"+packages.btn, ""+packages.price);

                }

                if (mUser!=null){
                    placeBaji();
                }else {
                    Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    private void createDialog() {
        waitingdialog = new ACProgressPie.Builder(getContext())
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build();
    }


    private void showWaitingDialog() {
        if (waitingdialog!=null && !waitingdialog.isShowing()){
            waitingdialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (waitingdialog!=null && waitingdialog.isShowing()){
            waitingdialog.dismiss();
        }
    }
    private void placeBaji() {

        if (bajiServerBody.getPackageList().size()>0){

            final float bajiCoin = bajiServerBody.getPackageList().get(0).price;


        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getCoin(Common.getKeyHash(getContext()),
                        gmailInfo.getGmail(),
                        gmailInfo.user_id,
                        gmailInfo.access_token)
                .enqueue(new Callback<MyCoinResponse>() {
                    @Override
                    public void onResponse(Call<MyCoinResponse> call, Response<MyCoinResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            if (response.body().coin<bajiCoin){
                                //show dialog
                                new AddCoinDialog(getContext());

                            }else {
                                final Dialog dialog = new Dialog(getContext());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(true);
                                dialog.setContentView(R.layout.layout_fianl_baji_list_dialog);

                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                Window window = dialog.getWindow();
                                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                                final RecyclerView recyclerPck = dialog.findViewById(R.id.recyclerPck);
                                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                                Button btnConfirm = dialog.findViewById(R.id.btnConfirm);



                                recyclerPck.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerPck.setHasFixedSize(true);


                                bajiPlaceListAdapter = new BajiPlaceListAdapter(getContext(), bajiServerBody.getPackageList(), Baji1.this);
                                recyclerPck.setAdapter(bajiPlaceListAdapter);
                                Button btnAddCoin = dialog.findViewById(R.id.btnAddCoin);
                                btnAddCoin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getContext(), AddCoinActivity.class));
                                    }
                                });
                                dialog.show();

                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (dialog.isShowing()){
                                            dialog.dismiss();
                                        }
                                    }
                                });

                                btnConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (bajiServerBody.getPackageList().size()>0){
                                            confirmBaji();
                                        }else {
                                            Toast.makeText(getContext(), "Baji List Is Empty", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyCoinResponse> call, Throwable t) {

                    }
                });



        }else {
            Toast.makeText(getContext(), "Please add baji", Toast.LENGTH_SHORT).show();
        }



    }

    private void confirmBaji() {

        if (gmailInfo==null){
            Toast.makeText(getContext(), "Login Again", Toast.LENGTH_SHORT).show();
        }
        showWaitingDialog();
        mAuth.getAccessToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            bajiServerBody.email = mUser.getEmail();
                            bajiServerBody.u_id = gmailInfo.user_id;
                            bajiServerBody.token = gmailInfo.access_token;
                            bajiServerBody.noOfBaji = 1;
                            bajiServerBody.sha1 = Common.getKeyHash(getContext());
                            RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                                    .placeBaji(bajiServerBody)
                                    .enqueue(new Callback<ServerResponse>() {
                                        @Override
                                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                            if (response.isSuccessful() && response.body()!=null){
                                                ServerResponse serverResponse = response.body();
                                                if (!serverResponse.error){
                                                    dismissWaitingDialog();
                                                    bajiServerBody.getPackageList().clear();
                                                    bajiPlaceListAdapter.notifyDataSetChanged();
                                                    Toast.makeText(getContext(), ""+serverResponse.getError_description(), Toast.LENGTH_SHORT).show();

                                                    Common.subscribeNoti(Common.getDate()+"_"+bajiServerBody.noOfBaji);
                                                }else {
                                                    dismissWaitingDialog();
                                                    Toast.makeText(getContext(),  serverResponse.getError_description(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                                            dismissWaitingDialog();
                                            Log.d("retrofit", t.getMessage());
                                            Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });



    }

    private void selectPackages(final Button btn) {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.layout_pck_list_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        final RecyclerView recyclerPck = dialog.findViewById(R.id.recyclerPck);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        recyclerPck.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPck.setHasFixedSize(true);

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getPackages()
                .enqueue(new Callback<PackageList>() {
                    @Override
                    public void onResponse(Call<PackageList> call, Response<PackageList> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            PackageList pack = response.body();

                            if (!pack.isError()){
                                PackagesListAdapter adapter = new PackagesListAdapter(getContext(), pack.getPackages(), btn.getId(), Baji1.this);
                                recyclerPck.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PackageList> call, Throwable t) {

                    }
                });


        dialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(int pos, int id, float price, String name, int btnId) {

       if (btnId==0){
           bajiServerBody.getPackageList().remove(pos);
           if (bajiPlaceListAdapter!=null){
               bajiPlaceListAdapter.notifyItemRemoved(pos);
           }
       }else {
           Packages packages = new Packages();
           packages.price = price;
           packages.id = id;
           packages.baji = 1;
           packages.name = name;
           if (btnId==R.id.btn1){
               packages.btn = "btn1";
           }else if (btnId==R.id.btn2){
               packages.btn = "btn2";
           }else if (btnId==R.id.btn2){
               packages.btn = "btn2";
           }else if (btnId==R.id.btn3){
               packages.btn = "btn3";
           }else if (btnId==R.id.btn4){
               packages.btn = "btn4";
           }else if (btnId==R.id.btn5){
               packages.btn = "btn5";
           }

           bajiServerBody.packageList.add(packages);

           Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
           if (dialog!=null && dialog.isShowing()){
               dialog.dismiss();
           }
       }
    }
}
