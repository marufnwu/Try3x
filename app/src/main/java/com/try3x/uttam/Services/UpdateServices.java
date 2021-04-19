package com.try3x.uttam.Services;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.os.StatFs;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.try3x.uttam.Common.Common;
import com.try3x.uttam.Models.Apk;
import com.try3x.uttam.R;
import com.try3x.uttam.SplashActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UpdateServices extends Service{


    private NotificationManager manager;
    private Notification notification;
    private NotificationCompat.Builder notificationBuilder;
    private boolean isStarted = false;
    private LocalBroadcastManager localBroadcastManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Apk apk = (Apk)intent.getSerializableExtra("apk");

        if (apk!=null){
            downloadApp downloadApp = new downloadApp();
            downloadApp.execute(apk);
        }else {
            stopSelf(startId);
        }

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
// Get the layouts to use in the custom notification
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.app_update_notification);
        //RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);

        String NOTIFICATION_CHANNEL_ID = "GDRIVE";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

         manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Update")
                .setContentText("Try3x is updating")
                .setProgress(100, 0, false)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    public class downloadApp extends AsyncTask<Apk, Integer, String> {



        @Override
        protected String doInBackground(Apk... asyncParams) {
           /* String PATH = Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).getAbsolutePath();
            File file = new File(PATH);
            File outputFile = new File(file, "try3x.apk");
            Log.d("AppPath", outputFile.getPath());
            installApk("try3x.apk");*/
            try {
                String appName = "try3x.apk";
                URL url = new URL(asyncParams[0].url);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
               // c.setDoOutput(true);
                c.connect();
                int lenghtOfFile = c.getContentLength();
                Log.d("ResCode", c.getResponseCode()+" ");
                String PATH = Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).getAbsolutePath();

                File file = new File(PATH);
               // File file = Environment.getExternalStoragePublicDirectory("try3x");
                //File file = Environment.getDownloadCacheDirectory();
                boolean isCreate = file.mkdirs();
                File outputFile = new File(file, appName);

                outputFile.setReadable(true, true);
                if (outputFile.exists()) {
                    boolean isDelete = outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1;
                long total = 0;
                Log.d("AppPath", outputFile.getPath());
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;
                    fos.write(buffer, 0, len1);
                    if(!isStarted){
                        if(total>10){
                            isStarted = true;
                            startUpdating();

                        }
                    }
                    //publishProgress((int) ((total * 100) / lenghtOfFile));
                    int currProgress = (int) ((total * 100) / lenghtOfFile);
                    updateProgress(currProgress);
                    if (manager!=null && notificationBuilder!=null){
                        notificationBuilder.setProgress(100,(int) ((total * 100) / lenghtOfFile), false);
                        manager.notify(2, notificationBuilder.build());
                    }
                }

                fos.close();
                is.close();
                outputFile.setReadable(true, true);
                installApk(appName);
            } catch (Exception e) {
                failedUpdating();
                e.printStackTrace();
                //Log.e("UpdateAPP", "Update error! " + e.getStackTrace());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        private void installApk(String appName) {
            completedUpdating();
            try {
                String PATH = Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).getAbsolutePath();
                 File file = new File(PATH + "/"+appName);
                //String PATH = Environment.getExternalStoragePublicDirectory("try3x").getAbsolutePath();
                //File file = new File(PATH+"/"+appName);
                Log.d("AppPath", file.getPath());

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
                stopSelf();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("InstallError", e.getMessage());
            }
        }
    }

    private void startUpdating(){
        Intent intent = new Intent(Common.APP_UPDATE_REEIVER);
        intent.putExtra("status", 1);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void completedUpdating(){
        Intent intent = new Intent(Common.APP_UPDATE_REEIVER);
        intent.putExtra("status", 2);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void failedUpdating(){
        stopSelf();
        Intent intent = new Intent(Common.APP_UPDATE_REEIVER);
        intent.putExtra("status", 0);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void updateProgress(int progress){
        Intent intent = new Intent(Common.APP_UPDATE_PROGRESS);
        intent.putExtra("progress", progress);
        localBroadcastManager.sendBroadcast(intent);
    }
}