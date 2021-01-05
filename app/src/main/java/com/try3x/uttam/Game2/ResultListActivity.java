package com.try3x.uttam.Game2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.try3x.uttam.Adapters.ResultListAdapter;
import com.try3x.uttam.Common.Common;
import com.try3x.uttam.MainActivity;
import com.try3x.uttam.Models.Response.ResultListResponse;
import com.try3x.uttam.Models.Result;
import com.try3x.uttam.Models.ResultVideo;
import com.try3x.uttam.R;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

import java.util.Date;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultListActivity extends AppCompatActivity {
    RecyclerView  recyclerResult;
    private ACProgressPie dialog;
    PlayerView playerView;
    private SimpleExoPlayer simpleExoplayer;
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    private boolean isVideoFound = false;
    private boolean isActivityCreatedByNoti;
    private ProgressBar vdoProgress;
    TextView txtViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        isActivityCreatedByNoti = getIntent().getBooleanExtra(Common.ACTIVITY_CREATED_BY_NOTI, false);
        initView();
        initilizePayer();
        createDialog();
        checkResultVideo();
        getResult();
    }

    private void initilizePayer() {
        mHandler = new Handler();

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
        trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd());

        simpleExoplayer = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector).build();
        playerView.setPlayer(simpleExoplayer);
        simpleExoplayer.setPlayWhenReady(true);
        simpleExoplayer.addListener(eventListener);


    }


    Player.EventListener eventListener = new Player.EventListener() {
        @Override
        public void onIsLoadingChanged(boolean isLoading) {
            if (isLoading){
                vdoProgress.setVisibility(View.VISIBLE);
            }else {
                vdoProgress.setVisibility(View.GONE);
            }
        }



        @Override
        public void onSeekProcessed() {
            simpleExoplayer.getDuration();

        }



        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.d("ExoplayerError", error.getMessage());
        }

        @Override
        public void onPlaybackStateChanged(int state) {

            if (Player.STATE_BUFFERING == state){
                vdoProgress.setVisibility(View.GONE);
            }if (Player.STATE_ENDED == state){
                vdoProgress.setVisibility(View.GONE);
            }else {
                vdoProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.d("ummmm", "onPlayerStateChanged");
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (isPlaying){
                vdoProgress.setVisibility(View.GONE);
            }else {

                vdoProgress.setVisibility(View.VISIBLE);
            }


        }
    };

    private int increaseView(int currView){
        int maxView = 9126383;
        int min = 80;
        int max = 568;
        int random =  (int)(Math.random() * max + min);
        int newView = currView+random;
        return Math.min(newView, maxView);
    }
    private void preparePlayer(String videoUrl, long seek) {
        Uri uri = Uri.parse(videoUrl);
         MediaSource mediaSource = buildMediaSource(uri);
        simpleExoplayer.prepare(mediaSource);
        simpleExoplayer.seekTo(seek);



    }
    private MediaSource buildMediaSource(Uri uri){
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext());
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private void releasePlayer() {
        simpleExoplayer.release();
    }
    private void checkResultVideo() {

        playerView.setVisibility(View.GONE);
        isVideoFound = false;
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getGame2ResultVideo()
                .enqueue(new Callback<ResultVideo>() {
                    @Override
                    public void onResponse(Call<ResultVideo> call, Response<ResultVideo> response) {

                        if (response.isSuccessful() && response.body()!=null){
                            ResultVideo resultVideo = response.body();

                            if (!resultVideo.error){
                                Log.d("Retrofit", resultVideo.video_link);
                                Date startTime = Common.strToTime(resultVideo.start_time);
                                Date endTime = Common.strToTime(resultVideo.end_time);
                                Date videoLength = Common.strToTime(resultVideo.video_length);
                                Date currTime = Common.strToTime(resultVideo.curr_time);

                                if (startTime.before(currTime) && endTime.after(currTime)){
                                    isVideoFound = true;
                                    long diff = currTime.getTime() - startTime.getTime();

                                    long diffSeconds = diff / 1000 % 60;
                                    long diffMinutes = diff / (60 * 1000) % 60;
                                    long diffHours = diff / (60 * 60 * 1000) % 24;
                                    long diffDays = diff / (24 * 60 * 60 * 1000);
                                    txtViews.setText(String.valueOf(Common.getRandomViews()));
                                    playerView.setVisibility(View.VISIBLE);
                                    //simpleExoplayer.seekTo(diff);
                                    preparePlayer(resultVideo.video_link, diff);
                                }else {
                                    playerView.setVisibility(View.GONE);
                                }

                            }else {
                                Toast.makeText(ResultListActivity.this, "error", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultVideo> call, Throwable t) {

                    }
                });

    }

    private void getResult() {
        showWaitingDialog();
        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getGame2ResultList()
                .enqueue(new Callback<ResultListResponse>() {
                    @Override
                    public void onResponse(Call<ResultListResponse> call, Response<ResultListResponse> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body() !=null){
                            ResultListResponse resultListResponse = response.body();
                            if (!resultListResponse.error){

                                setRecycler(resultListResponse.results);
                            }else {
                                Toast.makeText(ResultListActivity.this, resultListResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultListResponse> call, Throwable t) {
                        dismissWaitingDialog();
                    }
                });
    }

    private void setRecycler(List<Result> results) {
        ResultListAdapter adapter = new ResultListAdapter(results, getApplicationContext());
        recyclerResult.setAdapter(adapter);
    }

    private void initView() {
        playerView = findViewById(R.id.videoFullScreenPlayer);
        vdoProgress = findViewById(R.id.vdoProgress);
        txtViews = findViewById(R.id.txtViews);
        recyclerResult = findViewById(R.id.recyclerResult);
        recyclerResult.setHasFixedSize(true);
        recyclerResult.setLayoutManager(new LinearLayoutManager(this));
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
    protected void onResume() {
        super.onResume();
        startRepeatingTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (simpleExoplayer!=null && !simpleExoplayer.isPlaying() && !isVideoFound){
                    Log.d("ResultVideo", "Function call");
                    checkResultVideo();
                }else {
                    Log.d("ResultVideo", "Video Playing");

                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
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