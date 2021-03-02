package com.try3x.uttam.Services;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.try3x.uttam.MyBajiListActivity;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;

public class BajiClaimSchedule extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("JobScheduler", "Started");
        int bajiId = params.getExtras().getInt(MyBajiListActivity.CLAIM_BAJI_ID);
        int gameNo = params.getExtras().getInt(MyBajiListActivity.GAME_NO);

        if (bajiId>0 && gameNo>0){

            return true;
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("JobScheduler", "Stopped");

        return true;
    }
}