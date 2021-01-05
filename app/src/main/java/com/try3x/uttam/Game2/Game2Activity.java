package com.try3x.uttam.Game2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.try3x.uttam.Adapters.BajiViewpagerAdapter;
import com.try3x.uttam.Custom.MyViewpager;
import com.try3x.uttam.Fragments.GameSlotView2;
import com.try3x.uttam.Models.GameSlot;
import com.try3x.uttam.Models.GmailInfo;
import com.try3x.uttam.Models.Slot;
import com.try3x.uttam.R;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Game2Activity extends AppCompatActivity {
    LinearLayout game2layAlert;
    private static final String TOPIC_ALL_USER = "ALL_USERS";
    TabLayout game2tabLayout;
    MyViewpager game2viewpagerBaji;
    private ACProgressPie dialog;
    GmailInfo gmailInfo;
    Button game2btnGame2Bajilist, game2btnResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_game2);
        initView();
        createDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createViepager();
    }

    private void initView(){
        game2tabLayout = findViewById(R.id.game2tabLayout);
        game2viewpagerBaji = findViewById(R.id.game2gameViewPager);
        game2layAlert = findViewById(R.id.game2layAlert);
        game2btnGame2Bajilist = findViewById(R.id.game2btnGame2Bajilist);
        game2btnResult = findViewById(R.id.game2btnResult);

        game2btnGame2Bajilist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Game2Activity.this, MyBajiListActivity.class));
            }
        });

        game2btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Game2Activity.this, ResultListActivity.class));
            }
        });

    }

    private void createViepager() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}