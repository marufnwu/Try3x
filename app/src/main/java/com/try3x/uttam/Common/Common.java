package com.try3x.uttam.Common;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.try3x.uttam.BuildConfig;
import com.try3x.uttam.MainActivity;
import com.try3x.uttam.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Common {
    public static final String APP_UPDATE_REEIVER = "APP_UPDATE_REEIVER";
    public static final String APP_UPDATE_PROGRESS = "APP_UPDATE_PROGRESS";
    public static final String ACTIVITY =  "ACTIVITY";
    public static final String ACTIVITY_CREATED_BY_NOTI = "ACTIVITY_CREATED_BY_NOTI";

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

    public static void invite(Context context, String referCode){
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        String shareBody = "Use My Code To Refer Box -" +referCode+"\n"+
                "রেফার কোডে আমার কোডটি ব্যবহার করুন " +referCode+"\n"+
                "\n" +
                "Download the app now=> https://www.try3x.com";
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /*Fire!*/
        context.startActivity(Intent.createChooser(intent, "Share Using"));
    }

    public static Date strToTime(String time){
        Date date = null;
        DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        try {
             date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  date;
    }

    public static void  openLiveChat(Context context){
        String contact = "+918585807175"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void setShakeAnimation(ImageView img, Context context){
        img.startAnimation(AnimationUtils.loadAnimation(context,R.anim.shake));
    }
}
