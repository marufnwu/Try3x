package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smarteist.autoimageslider.SliderView;
import com.try3x.uttam.Adapters.BajiViewpagerAdapter;
import com.try3x.uttam.Adapters.BannerSliderAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Custom.MyViewpager;
import com.try3x.uttam.Fragments.Baji1;
import com.try3x.uttam.Fragments.Baji2;
import com.try3x.uttam.Fragments.Baji3;
import com.try3x.uttam.Fragments.Baji4;
import com.try3x.uttam.Fragments.Baji5;
import com.try3x.uttam.Models.Response.ResultStatusResponse;
import com.try3x.uttam.Models.Response.SlideResponse;
import com.try3x.uttam.Models.ResultStatus;
import com.try3x.uttam.Models.User;
import com.try3x.uttam.Models.UserLogin;
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

public class MainActivity extends AppCompatActivity {

    LinearLayout layMyBaji,layAddCoin,layMycoin,layWithdraw,layCommission;
    private static final String TOPIC_ALL_USER = "ALL_USERS";
    TabLayout tabLayout;
    MyViewpager viewpagerBaji;
    Button btnMyCoin, btnCommission ,btnWithdrawable, btnResult;
    User user;
    private NavigationView navigationView;
    ImageView imgDrawer;
    private DrawerLayout drawer;
    private ACProgressPie dialog;
    private SliderView imageSlider;
    private TextView txtUName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor(R.color.red);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        user = Paper.book().read(PaperDB.USER_PROFILE);

        drawer = findViewById(R.id.drawer_layout);
        //navigationView = findViewById(R.id.nav_view);

        initview();
        addInfoToNav();
        initViewpager();
        //getBannerSlider();

      /*  navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                drawer.closeDrawers();
                if (id == R.id.menuMybaji){

                    startActivity(new Intent(MainActivity.this, MyBajiListActivity.class));
                    return true;
                }else if (id == R.id.menuMyCoin){
                    startActivity(new Intent(MainActivity.this, MyCoinActivity.class));
                    return true;
                }else if (id == R.id.menuProfile){
                    startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                    return true;
                }
                return true;
            }
        });*/

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL_USER);
    }

    private void getBannerSlider() {
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getBannerSlide()
                .enqueue(new Callback<SlideResponse>() {
                    @Override
                    public void onResponse(Call<SlideResponse> call, Response<SlideResponse> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            SlideResponse slideResponse = response.body();
                            if (!slideResponse.error){
                                BannerSliderAdapter adapter = new BannerSliderAdapter(getApplicationContext(), slideResponse.slides);
                                imageSlider.setSliderAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SlideResponse> call, Throwable t) {

                    }
                });
    }

    private void changeStatusBarColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT<23){
                Window window = MainActivity.this.getWindow();

                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.WhiteBlue));
            }else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            }

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.WhiteBlue)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.WhiteBlue)); //status bar or the time bar at the top
        }

    }

    private void addInfoToNav() {
        ImageView imgPro = findViewById(R.id.navImgProfile);

        LinearLayout layMyBaji = findViewById(R.id.layMyBaji);
        LinearLayout layAddCoin = findViewById(R.id.layAddCoin);
        LinearLayout layMycoin = findViewById(R.id.layMycoin);
        LinearLayout layWithdraw = findViewById(R.id.layWithdraw);
        LinearLayout layCommission = findViewById(R.id.layCommission);

        Glide.with(this)
                .load(user.getPhoto_url())
                .placeholder(R.drawable.person)
                .into(imgPro);

        TextView name = findViewById(R.id.txtNavUserName);
        TextView mail = findViewById(R.id.txtNavUserMail);

        name.setText(user.name);
        mail.setText(user.email);

        txtUName.setText("Hi! "+Common.getFirstName(user.name));

        imgPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });

        layAddCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddCoinActivity.class));
            }
        });

        layMycoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyCoinActivity.class));
            }
        });

        layWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyWithrawbleActivity.class));
            }
        });

        layCommission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyCommisionActivity.class));
            }
        });

        layMyBaji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyBajiListActivity.class));
            }
        });




    }

    private void initViewpager() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getBajiResultStatus()
                .enqueue(new Callback<ResultStatusResponse>() {
                    @Override
                    public void onResponse(Call<ResultStatusResponse> call, Response<ResultStatusResponse> response) {
                        dismissWaitingDialog();
                        if(response.isSuccessful() && response.body()!=null){
                            ResultStatusResponse ResultStatusResponse = response.body();
                            if(!ResultStatusResponse.error){
                                ResultStatus resultStatus = ResultStatusResponse.resultStatus;

                                createViepager(resultStatus);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultStatusResponse> call, Throwable t) {

                    }
                });




    }

    private void createViepager(ResultStatus resultStatus) {

        List<Boolean> resultPublish = new ArrayList<>();
        if (resultStatus.getBaji1()==0){
            resultPublish.add(false);
        }else {
            resultPublish.add(true);

        }

        if (resultStatus.getBaji2()==0){
            resultPublish.add(false);
        }else {
            resultPublish.add(true);

        }

        if (resultStatus.getBaji3()==0){
            resultPublish.add(false);
        }else {
            resultPublish.add(true);

        }

        if (resultStatus.getBaji4()==0){
            resultPublish.add(false);
        }else {
            resultPublish.add(true);

        }

        if (resultStatus.getBaji5()==0){
            resultPublish.add(false);
        }else {
            resultPublish.add(true);

        }

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Baji1(resultPublish.get(0)));
        fragments.add(new Baji2(resultPublish.get(1)));
        fragments.add(new Baji3(resultPublish.get(2)));
        fragments.add(new Baji4(resultPublish.get(3)));
        fragments.add(new Baji5(resultPublish.get(4)));

        List<String> titles = new ArrayList<>();
        titles.add("Baji 1");
        titles.add("Baji 2");
        titles.add("Baji 3");
        titles.add("Baji 4");
        titles.add("Baji 5");



        BajiViewpagerAdapter bajiViewpagerAdapter = new BajiViewpagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewpagerBaji.setAdapter(bajiViewpagerAdapter);
        tabLayout.setupWithViewPager(viewpagerBaji);

        viewpagerBaji.setMyScroller();
    }

    private void initview() {
        tabLayout = findViewById(R.id.tabLayout);
        viewpagerBaji = findViewById(R.id.gameViewPager);
        btnCommission = findViewById(R.id.btnCommission);
        btnWithdrawable = findViewById(R.id.btnwithdrawable);
        btnResult = findViewById(R.id.btnResult);
        btnMyCoin = findViewById(R.id.btnMyCoin);
        imgDrawer = findViewById(R.id.imgDrawer);
        imageSlider = findViewById(R.id.imageSlider);
        txtUName = findViewById(R.id.txtUName);

        imgDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        btnMyCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyCoinActivity.class));
            }
        });

        btnCommission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyCommisionActivity.class));
            }
        });

        btnWithdrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyWithrawbleActivity.class));
            }
        });

        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResultListActivity.class));
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
        if (dialog!=null && !dialog.isShowing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }


}