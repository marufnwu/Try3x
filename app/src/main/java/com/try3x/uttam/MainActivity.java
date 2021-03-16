package com.try3x.uttam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smarteist.autoimageslider.SliderView;
import com.try3x.uttam.Adapters.BajiViewpagerAdapter;
import com.try3x.uttam.Adapters.BannerSliderAdapter;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Custom.MyViewpager;
import com.try3x.uttam.Fragments.GameSlotView;
import com.try3x.uttam.Fragments.GameSlotView2;
import com.try3x.uttam.Models.ActivityBanner;
import com.try3x.uttam.Models.GameSlot;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.Response.ResultStatusResponse;
import com.try3x.uttam.Models.Response.SlideResponse;
import com.try3x.uttam.Models.ResultStatus;
import com.try3x.uttam.Models.Slot;
import com.try3x.uttam.Models.User;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ScrollView mScrollView;
    LinearLayout laySecondGame;
    LinearLayout layMyBaji,layAddCoin,layMycoin,layWithdraw,layCommission, layTransaction, layAlert;
    private static final String TOPIC_ALL_USER = "ALL_USERS";
    TabLayout tabLayout;
    MyViewpager viewpagerBaji;
    Button btnMyCoin, btnCommission ,btnWithdrawable, btnResult, btnReloadBaji, btnYtVideo, btnHowToPly;
    User user;
    private NavigationView navigationView;
    ImageView imgDrawer,imgChat, imgUpdate;
    private DrawerLayout drawer;
    private ACProgressPie dialog;
    GmailInfo gmailInfo;

    private SliderView imageSlider;
    private TextView txtUName;
    private PackageInfo pInfo;

    //Game 2
    LinearLayout game2layAlert;
    TabLayout game2tabLayout;
    MyViewpager game2viewpagerBaji;
    Button game2btnGame2Bajilist, game2btnResult,  btnMyBajiList;
    ImageView imgBanner, imgBanner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor(getResources().getColor(R.color.OrangeRed));
        setContentView(R.layout.activity_main);
        Paper.init(this);
        user = Paper.book().read(PaperDB.USER_PROFILE);
        gmailInfo = Paper.book().read(PaperDB.GMAILINFO);

        drawer = findViewById(R.id.drawer_layout);
        //navigationView = findViewById(R.id.nav_view);

        initview();

        addInfoToNav();

        getBannerSlider();
        getBanner();
        getBanner1();

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

      scrollAnimation();



//        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mScrollView.post(new Runnable() {
//                    public void run() {
//                        mScrollView.fullScroll(View.FOCUS_DOWN);
//
//                    }
//                });
//            }
//        });


        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL_USER);
        setAppUpdateHistory();
    }

    private void getBanner1() {
        imgBanner1.setVisibility(View.GONE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getActivityBanner("MainActivity1")
                .enqueue(new Callback<ActivityBanner>() {
                    @Override
                    public void onResponse(Call<ActivityBanner> call, Response<ActivityBanner> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            final ActivityBanner activityBanner = response.body();
                            if (!activityBanner.error){
                                if (activityBanner.imageUrl!=null){
                                    int val = new Random().nextInt(6-1)+1;
                                    int drawable = R.drawable.place1;
                                    switch (val){
                                        case 2:
                                            drawable = R.drawable.place2;
                                            break;
                                        case 3:
                                            drawable = R.drawable.place3;
                                            break;
                                        case 4:
                                            drawable = R.drawable.place4;
                                            break;
                                        case 5:
                                            drawable = R.drawable.place5;
                                            break;
                                        case 6:
                                            drawable = R.drawable.place6;
                                            break;
                                        default:
                                            drawable = R.drawable.place1;

                                    }
                                    imgBanner1.setVisibility(View.VISIBLE);
                                    if(!MainActivity.this.isFinishing()){
                                        Glide.with(MainActivity.this)
                                                .load(activityBanner.imageUrl)
                                                .thumbnail(Glide.with(getApplicationContext()).load(drawable))
                                                .into(imgBanner1);

                                        imgBanner1.setOnClickListener(new View.OnClickListener() {
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
                    }

                    @Override
                    public void onFailure(Call<ActivityBanner> call, Throwable t) {

                    }
                });
    }

    private void scrollAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.post(new Runnable() {
                    public void run() {
                        //mScrollView.smoothScrollTo(0, laySecondGame.getBottom());
                        ObjectAnimator.ofInt(mScrollView, "scrollY",  laySecondGame.getBottom()).setDuration(1000).start();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               // mScrollView.smoothScrollTo(laySecondGame.getBottom(), View.FOCUS_UP);
                                ObjectAnimator.ofInt(mScrollView, "scrollY",  View.FOCUS_UP).setDuration(1000).start();
                            }
                        }, 1000);
                    }
                });
            }
        }, 2000);
    }

    private void getBanner() {
        imgBanner.setVisibility(View.GONE);
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getActivityBanner("MainActivity")
                .enqueue(new Callback<ActivityBanner>() {
                    @Override
                    public void onResponse(Call<ActivityBanner> call, Response<ActivityBanner> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            final ActivityBanner activityBanner = response.body();
                            if (!activityBanner.error){
                                if (activityBanner.imageUrl!=null){
                                    imgBanner.setVisibility(View.VISIBLE);
                                    if(!MainActivity.this.isFinishing()){
                                        int val = new Random().nextInt(6-1)+1;
                                        int drawable = R.drawable.place1;
                                        switch (val){
                                            case 2:
                                                drawable = R.drawable.place2;
                                                break;
                                            case 3:
                                                drawable = R.drawable.place3;
                                                break;
                                            case 4:
                                                drawable = R.drawable.place4;
                                                break;
                                            case 5:
                                                drawable = R.drawable.place5;
                                                break;
                                            case 6:
                                                drawable = R.drawable.place6;
                                                break;
                                            default:
                                                drawable = R.drawable.place1;

                                        }
                                        Glide.with(MainActivity.this)
                                                .load(activityBanner.imageUrl)
                                                .thumbnail(Glide.with(getApplicationContext()).load(drawable))
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
                    }

                    @Override
                    public void onFailure(Call<ActivityBanner> call, Throwable t) {

                    }
                });
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
        LinearLayout layTransaction = findViewById(R.id.layTransaction);
        LinearLayout layProfile = findViewById(R.id.layProfile);

        TextView txtPrivacyPolicyNav = findViewById(R.id.txtPrivacyPolicyNav);
        TextView txtTermsAndConditionNav = findViewById(R.id.txtTermsAndConditionNav);

        Glide.with(this)
                .load(user.getPhoto_url())
                .placeholder(R.drawable.person)
                .into(imgPro);

        TextView name = findViewById(R.id.txtNavUserName);
        TextView mail = findViewById(R.id.txtNavUserMail);

        name.setText(user.name);
        mail.setText(user.email);



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
        layTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TransactionActivity.class));
            }
        });

        layProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });

        txtPrivacyPolicyNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
                    }
         });

        txtTermsAndConditionNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, TermActivity.class));
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

                                //createViepager(resultStatus);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultStatusResponse> call, Throwable t) {

                    }
                });




    }

    private void createViepager(/*ResultStatus resultStatus*/) {
        layAlert.setVisibility(View.GONE);
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getGameSlot()
                .enqueue(new Callback<GameSlot>() {
                    @Override
                    public void onResponse(Call<GameSlot> call, Response<GameSlot> response) {
                        dismissWaitingDialog();
                        List<Fragment> fragments = new ArrayList<>();
                        List<String> titles = new ArrayList<>();
                        if (response.isSuccessful() && response.body()!=null){
                            GameSlot gameSlot = response.body();
                            if (!gameSlot.error){
                                List<Slot> slots = gameSlot.slots;
                                if (slots.size()>0){
                                    layAlert.setVisibility(View.GONE);
                                    for (Slot slot : slots){
                                        GameSlotView gameSlotView = GameSlotView.getInstance(slot);
                                        fragments.add(gameSlotView);
                                        titles.add(slot.name);
                                    }


                                    BajiViewpagerAdapter bajiViewpagerAdapter = new BajiViewpagerAdapter(getSupportFragmentManager(), fragments, titles);
                                    viewpagerBaji.setAdapter(bajiViewpagerAdapter);
                                    tabLayout.setupWithViewPager(viewpagerBaji);

                                    viewpagerBaji.setMyScroller();

                                    return;

                                }
                            }
                        }

                        layAlert.setVisibility(View.VISIBLE);


                    }

                    @Override
                    public void onFailure(Call<GameSlot> call, Throwable t) {
                        dismissWaitingDialog();
                    }
                });

//        List<Boolean> resultPublish = new ArrayList<>();
//        if (resultStatus.getBaji1()==0){
//            resultPublish.add(false);
//        }else {
//            resultPublish.add(true);
//
//        }
//
//        if (resultStatus.getBaji2()==0){
//            resultPublish.add(false);
//        }else {
//            resultPublish.add(true);
//
//        }
//
//        if (resultStatus.getBaji3()==0){
//            resultPublish.add(false);
//        }else {
//            resultPublish.add(true);
//
//        }
//
//        if (resultStatus.getBaji4()==0){
//            resultPublish.add(false);
//        }else {
//            resultPublish.add(true);
//
//        }
//
//        if (resultStatus.getBaji5()==0){
//            resultPublish.add(false);
//        }else {
//            resultPublish.add(true);
//
//        }

        //List<Fragment> fragments = new ArrayList<>();
        //List<String> titles = new ArrayList<>();
       /* fragments.add(new Baji1(resultPublish.get(0)));
        fragments.add(new Baji2(resultPublish.get(1)));
        fragments.add(new Baji3(resultPublish.get(2)));
        fragments.add(new Baji4(resultPublish.get(3)));
        fragments.add(new Baji5(resultPublish.get(4)));*/




       /* titles.add("Baji 1");
        titles.add("Baji 2");
        titles.add("Baji 3");
        titles.add("Baji 4");
        titles.add("Baji 5");*/




    }
    private void createGame2Viepager() {
        game2layAlert.setVisibility(View.GONE);
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getGameSlot2()
                .enqueue(new Callback<GameSlot>() {
                    @Override
                    public void onResponse(Call<GameSlot> call, Response<GameSlot> response) {
                        dismissWaitingDialog();
                        List<Fragment> fragments = new ArrayList<>();
                        List<String> titles = new ArrayList<>();
                        if (response.isSuccessful() && response.body()!=null){
                            GameSlot gameSlot = response.body();
                            if (!gameSlot.error){
                                List<Slot> slots = gameSlot.slots;
                                if (slots.size()>0){
                                    game2layAlert.setVisibility(View.GONE);
                                    for (Slot slot : slots){
                                        GameSlotView2 gameSlotView = GameSlotView2.getInstance(slot);
                                        fragments.add(gameSlotView);
                                        titles.add(slot.name);
                                    }


                                    BajiViewpagerAdapter bajiViewpagerAdapter = new BajiViewpagerAdapter(getSupportFragmentManager(), fragments, titles);
                                    game2viewpagerBaji.setAdapter(bajiViewpagerAdapter);
                                    game2tabLayout.setupWithViewPager(game2viewpagerBaji);

                                    game2viewpagerBaji.setMyScroller();

                                    return;

                                }
                            }
                        }

                        game2layAlert.setVisibility(View.VISIBLE);


                    }

                    @Override
                    public void onFailure(Call<GameSlot> call, Throwable t) {
                        dismissWaitingDialog();
                    }
                });



    }

    private void initview() {
        tabLayout = findViewById(R.id.tabLayout);
        viewpagerBaji = findViewById(R.id.gameViewPager);
        btnCommission = findViewById(R.id.btnCommission);
        btnWithdrawable = findViewById(R.id.btnwithdrawable);
        btnResult = findViewById(R.id.btnResult);
        btnMyBajiList = findViewById(R.id.btnMyBajiList);
        btnHowToPly = findViewById(R.id.btnHowToPly);

        btnMyCoin = findViewById(R.id.btnMyCoin);
        btnReloadBaji = findViewById(R.id.btnReloadBaji);
        imgDrawer = findViewById(R.id.imgDrawer);
        imageSlider = findViewById(R.id.imageSlider);
        imgChat = findViewById(R.id.imgChat);
        imgUpdate = findViewById(R.id.imgChckUpdate);
        layAlert = findViewById(R.id.layAlert);

        mScrollView = findViewById(R.id.scrollview);
        laySecondGame = findViewById(R.id.laySecondGame);
        imgBanner = findViewById(R.id.imgBanner);
        imgBanner1 = findViewById(R.id.imgBanner1);
        btnYtVideo = findViewById(R.id.btnYtVideo);

        initGame2View();

        btnReloadBaji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createViepager();

            }
        });

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

        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLiveChat();
            }
        });

        imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AppUpdateActivity.class));

            }
        });

        btnYtVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), YtVideoActivity.class));

                }
            });
        btnMyBajiList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), MyBajiListActivity.class));

                }
            });

         btnHowToPly.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(), HowToPlyActivity.class));

                        }
         });


    }

    private void initGame2View() {
        game2tabLayout = findViewById(R.id.game2tabLayout);
        game2viewpagerBaji = findViewById(R.id.game2gameViewPager);
        game2layAlert = findViewById(R.id.game2layAlert);
        game2btnGame2Bajilist = findViewById(R.id.game2btnGame2Bajilist);
        game2btnResult = findViewById(R.id.game2btnResult);

        game2btnGame2Bajilist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, com.try3x.uttam.Game2.MyBajiListActivity.class));
            }
        });

        game2btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, com.try3x.uttam.Game2.ResultListActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        createViepager();
        createGame2Viepager();

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

    public void  openLiveChat(){
        String contact = "+918585807175"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(MainActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setAppUpdateHistory(){
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);

            String version = pInfo.versionName;
            int versionCode = pInfo.versionCode;

            RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                    .appUpdateHistory(
                          gmailInfo.gmail,
                            gmailInfo.user_id,
                            version,
                            String.valueOf(versionCode)
                    )
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}