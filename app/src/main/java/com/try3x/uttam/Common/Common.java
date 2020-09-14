package com.try3x.uttam.Common;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.try3x.uttam.BuildConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Common {
    public static final String APP_UPDATE_REEIVER = "APP_UPDATE_REEIVER";
    public static final String APP_UPDATE_PROGRESS = "APP_UPDATE_PROGRESS";

    public static String getKeyHash(Context ctx) {
        String key = " ";
        PackageInfo info;
        try {
            info = ctx.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("KeyHash  -->>>>>>>>>>>>" , something);

                // Notification.registerGCM(this);

                key = something;
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found" , e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm" , e.toString());
        } catch (Exception e) {
            Log.e("exception" , e.toString());
        }

        return key;
    }

    public static String getGender(int pos){
        if (pos==0){
            return "Other";
        }else if (pos==1){
            return "Male";
        }else if (pos==2){
            return "Female";
        }else {
            return "Other";
        }
    }

    public static String getPaymentMethod(int pos){
        if (pos==0){
            return "Other";
        }else if (pos==1){
            return "Paytm";
        }else if (pos==2){
            return "Gpay";
        }else if (pos==3){
            return "PhonPay";
        }else {
            return "Other";
        }
    }

    public static String getPayoutStatus(int pos){
        if (pos==0){
            return "Pending";
        }else if(pos==1){
            return "Paid";
        }else if(pos==2){
            return "Rejected";
        }else {
            return "N/A";
        }
    }

    public static boolean isEmailValidPattern(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern)){
            return true;
        }else {
            return false;
        }
    }

    public static String date(String date){
        String[] part = date.split(" ");

        return part[0];
    }

    public static String getDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);

        return  formattedDate;
    }

    public static void subscribeNoti(String s) {
        Log.d("SubscribeNoti", s);
        FirebaseMessaging.getInstance().subscribeToTopic(s);
    }

    public static String getFirstName(String name){
        String[] part = name.split(" ");

        return part[0];
    }
}
