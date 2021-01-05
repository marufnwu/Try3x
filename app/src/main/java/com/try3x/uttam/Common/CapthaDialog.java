package com.try3x.uttam.Common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.try3x.uttam.R;

import java.util.Random;

public class CapthaDialog {
    String TAG = "CapthaDialog";
    String operators = "+-";
    int max = 10;
    int min=1;
    private final Random rand;
    Context context;
    private Dialog dialog;
    public OnCaptchaDialogListener onCaptchaDialogListener;
    public interface OnCaptchaDialogListener{
        void onResultOk();
        void onCancel();
        void onToast(String message);
    }

    public CapthaDialog(Context context) {
        Log.d(TAG, "Created");
        this.context = context;
        rand = new Random();
    }

    public OnCaptchaDialogListener getOnCaptchaDialogListener() {
        return onCaptchaDialogListener;
    }

    public void setOnCaptchaDialogListener(OnCaptchaDialogListener onCaptchaDialogListener) {
        this.onCaptchaDialogListener = onCaptchaDialogListener;
    }

    public void init(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_captcha_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        TextView txtQues = dialog.findViewById(R.id.txtQues);
        final TextView edtResult = dialog.findViewById(R.id.edtResult);

        int num1 = rand.nextInt(max - min + 1) + min;
        int num2 = rand.nextInt(max - min + 1) + min;

        String operator = String.valueOf(operators.charAt(rand.nextInt(operators.length())));

        int correctResult = 0;
        if (operator.equals("+")){
            correctResult = num1+num2;
        }else {
            int max = num1;
            if (max<num2){

                num1 = num2;
                num2 = max;

            }
            correctResult = num1-num2;
        }
        String ques = String.valueOf(num1)+operator+String.valueOf(num2);
        txtQues.setText(ques);

        final int finalCorrectResult = correctResult;
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtResult.getText().length()>0){
                    int result = Integer.parseInt(edtResult.getText().toString());

                    if (result== finalCorrectResult){
                        dialog.dismiss();
                        onCaptchaDialogListener.onResultOk();
                    }else {
                        onCaptchaDialogListener.onToast("আপনার উত্তর টি ভুল ছিলো\n" +"অনুগ্রহ করে সঠিক উত্তর দিন");
                    }
                }else {
                    onCaptchaDialogListener.onToast("Please Enter Result");
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




    }

    public void showDialog(){
        if (dialog!=null){
            dialog.show();
        }
    }

    public void hideDialog(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }
}
