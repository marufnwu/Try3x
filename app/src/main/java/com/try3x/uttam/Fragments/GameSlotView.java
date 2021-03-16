package com.try3x.uttam.Fragments;

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
import android.widget.ProgressBar;
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
import com.try3x.uttam.Adapters.BajiBtnAdapter;
import com.try3x.uttam.Adapters.BajiPlaceListAdapter;
import com.try3x.uttam.Adapters.PackagesListAdapter;
import com.try3x.uttam.AddCoinActivity;
import com.try3x.uttam.Common.AddCoinDialog;
import com.try3x.uttam.Common.CapthaDialog;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Listener.OnPackageItemClickListener;
import com.try3x.uttam.Models.Btn;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.PackageList;
import com.try3x.uttam.Models.Packages;
import com.try3x.uttam.Models.Response.BajiBtnResponse;
import com.try3x.uttam.Models.Response.BajiServerBody;
import com.try3x.uttam.Models.Response.MyCoinResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.Models.Slot;
import com.try3x.uttam.PayoutActivity;
import com.try3x.uttam.R;
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

public class GameSlotView extends Fragment implements OnPackageItemClickListener {
    Slot slot;
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
    RecyclerView recyBajiBtn;
    ProgressBar btnProgress;
    BajiBtnAdapter bajiBtnAdapter;
    List<Btn> btnList = new ArrayList<>();
    private TextView bajiId,txtResultTime;
    private static final String SLOT_KEY = "slot_key";

    public GameSlotView() {

    }

    public static GameSlotView getInstance(Slot slot){
        GameSlotView gameSlotView = new GameSlotView();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SLOT_KEY, slot);
        gameSlotView.setArguments(bundle);

        return  gameSlotView;
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
        slot = (Slot) getArguments().getSerializable(
                SLOT_KEY);
        btnProgress = view.findViewById(R.id.btnProgress);
        recyBajiBtn = view.findViewById(R.id.recyBajiBtn);
        bajiId = view.findViewById(R.id.bajiId);
        txtResultTime = view.findViewById(R.id.txtResultTime);

        recyBajiBtn.setHasFixedSize(true);
        recyBajiBtn.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) );

        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btnPlaceBaji = view.findViewById(R.id.btnPlace);

        bajiServerBody = new BajiServerBody();
        createDialog();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectPackages(btn1);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectPackages(btn2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectPackages(btn3);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // selectPackages(btn4);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectPackages(btn5);
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

        bajiId.setText("ID #"+slot.id);
        txtResultTime.setText("রেজাল্ট পাবলিশ এর টাইম: "+slot.end_time);

        getBajiBtns();

        return view;
    }

    private void getBajiBtns() {
        btnProgress.setVisibility(View.VISIBLE);
        bajiBtnAdapter = new BajiBtnAdapter(getContext(), btnList);
        recyBajiBtn.setAdapter(bajiBtnAdapter);


        bajiBtnAdapter.setOnBtnClickListener(new BajiBtnAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(int pos, Btn btn) {
                selectPackages(btn.id);
            }
        });


        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getBajiBtn()
                .enqueue(new Callback<BajiBtnResponse>() {
                    @Override
                    public void onResponse(Call<BajiBtnResponse> call, Response<BajiBtnResponse> response) {
                        btnProgress.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body()!=null){
                            BajiBtnResponse bajiBtnResponse = response.body();

                            if (!bajiBtnResponse.error){
                                List<Btn> btns = bajiBtnResponse.btns;
                                btnList.clear();
                                btnList.addAll(btns);
                                bajiBtnAdapter.notifyDataSetChanged();

                            }
                        }else {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BajiBtnResponse> call, Throwable t) {

                    }
                });
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

    private void selectPackages(final int btn) {
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
                .getPackages(
                        slot.id,
                        gmailInfo.user_id
                )
                .enqueue(new Callback<PackageList>() {
                    @Override
                    public void onResponse(Call<PackageList> call, Response<PackageList> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            PackageList pack = response.body();

                            if (!pack.isError()){
                                PackagesListAdapter adapter = new PackagesListAdapter(getContext(), pack.getPackages(), btn, GameSlotView.this);
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
        if (btnId==0 && bajiServerBody.getPackageList().size()>0){
            bajiServerBody.getPackageList().remove(pos);
            if (bajiPlaceListAdapter!=null){
                bajiPlaceListAdapter.notifyItemRemoved(pos);
                bajiPlaceListAdapter.notifyItemRangeRemoved(pos, bajiServerBody.getPackageList().size());
            }
        }else {
            bajiServerBody.packageList.clear(); //we clear all baji cause we required
            Packages packages = new Packages();
            packages.price = price;
            packages.id = id;
            packages.baji = Integer.parseInt(slot.id);
            packages.name = name;
            packages.btn = String.valueOf(btnId);


            bajiServerBody.packageList.add(packages);

            //Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
            if (dialog!=null && dialog.isShowing()){
                dialog.dismiss();
            }
            waitingdialog.show();
            placeBaji();
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
                                    dismissWaitingDialog();

                                    new AddCoinDialog(getContext());

                                }else {
                                    Toast.makeText(getContext(), "Participate with the correct answer and take the chance to win prizes", Toast.LENGTH_SHORT).show();
                                    showCaptcha();

//                                    final Dialog dialog = new Dialog(getContext());
//                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                                    dialog.setCancelable(true);
//                                    dialog.setContentView(R.layout.layout_fianl_baji_list_dialog);
//
//                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                                    Window window = dialog.getWindow();
//                                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//
//                                    final RecyclerView recyclerPck = dialog.findViewById(R.id.recyclerPck);
//                                    Button btnCancel = dialog.findViewById(R.id.btnCancel);
//                                    Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
//
//
//
//                                    recyclerPck.setLayoutManager(new LinearLayoutManager(getContext()));
//                                    recyclerPck.setHasFixedSize(true);
//
//
//                                    bajiPlaceListAdapter = new BajiPlaceListAdapter(getContext(), bajiServerBody.getPackageList(), GameSlotView.this);
//                                    recyclerPck.setAdapter(bajiPlaceListAdapter);
//                                    Button btnAddCoin = dialog.findViewById(R.id.btnAddCoin);
//                                    btnAddCoin.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            startActivity(new Intent(getContext(), AddCoinActivity.class));
//                                        }
//                                    });
//                                    dialog.show();
//
//                                    btnCancel.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            if (dialog.isShowing()){
//                                                dialog.dismiss();
//                                            }
//                                        }
//                                    });
//
//                                    btnConfirm.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            if (bajiServerBody.getPackageList().size()>0){
////                                                confirmBaji();
//                                                dialog.dismiss();
//                                                showCaptcha();
//                                            }else {
//                                                Toast.makeText(getContext(), "Baji List Is Empty", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyCoinResponse> call, Throwable t) {
                            dismissWaitingDialog();
                        }
                    });



        }else {
            dismissWaitingDialog();
            Toast.makeText(getContext(), "Please add baji", Toast.LENGTH_SHORT).show();
        }



    }
    /*private void showCaptcha() {

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getMathQuestion(
                        Common.getKeyHash(getContext()),
                        gmailInfo.getGmail(),
                        gmailInfo.user_id,
                        gmailInfo.access_token
                )
                .enqueue(new Callback<MathQuestion>() {
                    @Override
                    public void onResponse(Call<MathQuestion> call, Response<MathQuestion> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body()!=null){
                            final MathQuestion mathQuestion = response.body();
                            if (!mathQuestion.isError()){
                                final Dialog dialog = new Dialog(getContext());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(true);
                                dialog.setContentView(R.layout.dialog_captcha_layout);

                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                Window window = dialog.getWindow();
                                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
                                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                                TextView txtQues = dialog.findViewById(R.id.txtQues);
                                final TextView edtResult = dialog.findViewById(R.id.edtResult);


                                String ques = mathQuestion.getNum1()+mathQuestion.getOperator()+mathQuestion.getNum2();
                                txtQues.setText(ques);


                                btnSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (edtResult.getText().length()>0){
                                            int result = Integer.parseInt(edtResult.getText().toString());
                                            dialog.dismiss();
                                            confirmBaji(mathQuestion.getId(), result);
                                        }else {
                                            Toast.makeText(getContext(), "Please Enter Result", Toast.LENGTH_SHORT).show();;
                                        }

                                    }
                                });

                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();
                            }else {
                                Toast.makeText(getContext(), mathQuestion.getErrorDescription(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MathQuestion> call, Throwable t) {
                        dismissWaitingDialog();
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

    private void showCaptcha(){
        dismissWaitingDialog();
        final CapthaDialog capthaDialog = new CapthaDialog(getContext());
        capthaDialog.init();
        capthaDialog.showDialog();
        capthaDialog.setOnCaptchaDialogListener(new CapthaDialog.OnCaptchaDialogListener() {
            @Override
            public void onResultOk() {

               confirmBaji();
            }

            @Override
            public void onCancel() {
                capthaDialog.hideDialog();
            }

            @Override
            public void onToast(String message) {

                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                capthaDialog.hideDialog();
            }
        });
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
                            bajiServerBody.noOfBaji = Integer.parseInt(slot.id);
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
                                                    //bajiPlaceListAdapter.notifyDataSetChanged();
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

    /*private void confirmBaji(final String quesId, final int result) {

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
                            bajiServerBody.noOfBaji = Integer.parseInt(slot.id);
                            bajiServerBody.sha1 = Common.getKeyHash(getContext());
                            bajiServerBody.quesId = quesId;
                            bajiServerBody.quesResult = result;
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



    }*/
}
