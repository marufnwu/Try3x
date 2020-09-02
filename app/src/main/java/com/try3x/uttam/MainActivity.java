package com.try3x.uttam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.try3x.uttam.Adapters.BajiViewpagerAdapter;
import com.try3x.uttam.Common.PaperDB;
import com.try3x.uttam.Custom.MyViewpager;
import com.try3x.uttam.Fragments.Baji1;
import com.try3x.uttam.Fragments.Baji2;
import com.try3x.uttam.Fragments.Baji3;
import com.try3x.uttam.Fragments.Baji4;
import com.try3x.uttam.Fragments.Baji5;
import com.try3x.uttam.Models.User;
import com.try3x.uttam.Models.UserLogin;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private static final String TOPIC_ALL_USER = "ALL_USERS";
    TabLayout tabLayout;
    MyViewpager viewpagerBaji;
    Button btnMyCoin, btnCommission ,btnWithdrawable;
    User user;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor(R.color.red);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        user = Paper.book().read(PaperDB.USER_PROFILE);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        initview();
        addInfoToNav();
        initViewpager();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menuMybaji){
                    startActivity(new Intent(MainActivity.this, MyBajiListActivity.class));
                }else if (id == R.id.menuMyCoin){
                    startActivity(new Intent(MainActivity.this, MyCoinActivity.class));
                }else if (id == R.id.menuProfile){
                    startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                }
                return true;
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL_USER);
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
        View view = navigationView.getHeaderView(0);
        ImageView imgPro = view.findViewById(R.id.navImgProfile);
        Glide.with(this)
                .load(user.getPhoto_url())
                .placeholder(R.drawable.person)
                .into(imgPro);

        TextView name = view.findViewById(R.id.txtNavUserName);
        TextView mail = view.findViewById(R.id.txtNavUserMail);

        name.setText(user.name);
        mail.setText(user.email);

        imgPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });

    }

    private void initViewpager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Baji1());
        fragments.add(new Baji2());
        fragments.add(new Baji3());
        fragments.add(new Baji4());
        fragments.add(new Baji5());

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
        btnMyCoin = findViewById(R.id.btnMyCoin);

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
    }



}