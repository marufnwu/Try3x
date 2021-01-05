package com.try3x.uttam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Models.Apk;
import com.try3x.uttam.Models.Response.AppUpdateResponse;
import com.try3x.uttam.Retrofit.IRetrofitApiCall;
import com.try3x.uttam.Retrofit.RetrofitClient;
import com.try3x.uttam.Services.UpdateServices;

import java.io.File;
import java.util.List;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUpdateActivity extends AppCompatActivity {

    TextView txtUpdate, txtCurrVersion;
    ProgressBar updateProgress;
    Button btnUpdate, btnInstall;
    private ACProgressPie dialog;
    private String version = " ";
    private UpdateServices updateServices;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        localBroadcastManager.registerReceiver(appUpdateReceiver, new IntentFilter(Common.APP_UPDATE_REEIVER));
        localBroadcastManager.registerReceiver(updateProgressReceiver, new IntentFilter(Common.APP_UPDATE_PROGRESS));

        checkUpdate();

    }

    @Override
    protected void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(appUpdateReceiver);
        localBroadcastManager.unregisterReceiver(updateProgressReceiver);
    }

    private void initView() {
        createDialog();
        updateServices = new UpdateServices();

        txtUpdate = findViewById(R.id.txtUpdate);
        txtCurrVersion = findViewById(R.id.txtCurrVersion);
        updateProgress = findViewById(R.id.progress);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnInstall = findViewById(R.id.btnInstall);
    }

    private void createDialog() {
        dialog = new ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build();
    }

    private void checkUpdate() {
        if(isMyServiceRunning(updateServices.getClass())){
            updateProgress.setVisibility(View.VISIBLE);
            Toast.makeText(AppUpdateActivity.this, "App Updating", Toast.LENGTH_SHORT).show();
            return;
        }else{
            updateProgress.setVisibility(View.GONE);
        }
        showWaitingDialog();

        RetrofitClient.getRetrofit().create(IRetrofitApiCall.class)
                .getAppUpdate()
                .enqueue(new Callback<AppUpdateResponse>() {
                    @Override
                    public void onResponse(Call<AppUpdateResponse> call, Response<AppUpdateResponse> response) {
                        dismissWaitingDialog();
                        if (response.isSuccessful() && response.body()!=null){
                            AppUpdateResponse appUpdateResponse = response.body();
                            if (!appUpdateResponse.isError()){
                                final Apk apk = appUpdateResponse.getApk();
                                PackageInfo pInfo = null;
                                try {
                                    pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                     version = pInfo.versionName;
                                    int versionCode = pInfo.versionCode;

                                    Log.d("versionCode", "App "+versionCode+" Server "+apk.vCode);
                                    if (apk.vCode<=versionCode){
                                        //app upto date
                                        updateProgress.setVisibility(View.GONE);
                                        txtCurrVersion.setText("Current Version: "+version);
                                        btnInstall.setVisibility(View.GONE);
                                        btnUpdate.setText("App Up To Date");
                                        btnUpdate.setEnabled(false);
                                        txtUpdate.setText("No update available");

                                    }else {
                                        //update avilable
                                        //updateProgress.setVisibility(View.VISIBLE);

                                        txtCurrVersion.setText("Current Version: "+version);
                                        btnInstall.setVisibility(View.GONE);
                                        btnUpdate.setText("Update");
                                        btnUpdate.setEnabled(true);
                                        txtUpdate.setText("Update available");

                                        final PackageManager pm = getPackageManager();
                                        String apkName = "try3x.apk";
                                        final String fullPath = Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).getAbsolutePath()+ "/" + apkName;
                                        //String fullPath = Environment.getExternalStorageDirectory() + "/" + apkName;
                                        PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
                                        //Toast.makeText(this, "VersionCode : " + info.versionCode + ", VersionName : " + info.versionName , Toast.LENGTH_LONG).show();
                                        if(info !=null && info.versionCode==apk.vCode){
                                            btnInstall.setVisibility(View.VISIBLE);
                                            btnInstall.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                        installApk(fullPath);
                                                }
                                            });
                                        }
                                        btnUpdate.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                updateProgress.setVisibility(View.VISIBLE);

                                                //Toast.makeText(AppUpdateActivity.this, "click", Toast.LENGTH_SHORT).show();
                                                startDownload(apk);
                                            }
                                        });
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                Toast.makeText(AppUpdateActivity.this, appUpdateResponse.error_description, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AppUpdateResponse> call, Throwable t) {

                    }
                });
    }

    private void installApk(String fullPath) {
        if (isMyServiceRunning(updateServices.getClass())) {
            Toast.makeText(this, "App Is Updating", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(fullPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            Uri downloaded_apk = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
            List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                getApplicationContext().grantUriPermission(getApplicationContext().getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
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


    public BroadcastReceiver appUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status", -1);
            if (status==1){
//                if (mPDialog!=null && !mPDialog.isShowing()){
//                    mPDialog.show();
//                }
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
            if (updateProgress != null) {

                updateProgress.setProgress(progress);
                txtUpdate.setText("Updating.. "+progress+" %");
            }else {
                Log.d("UpdateStatus", "dialog null"+progress);

                updateProgress = findViewById(R.id.progress);
            }
        }
    };

    private void startDownload(Apk apk) {
//       downloadApp downloadApp = new downloadApp();
//       downloadApp.setContext(SplashActivity.this);
//       downloadApp.execute(apk);
        Intent intent = new Intent(AppUpdateActivity.this, updateServices.getClass());
        intent.putExtra("apk", apk);
        if (!isMyServiceRunning(updateServices.getClass())) {
            startService(intent);
        }else {
            Toast.makeText(this, "App Is Updating", Toast.LENGTH_SHORT).show();
        }
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
}