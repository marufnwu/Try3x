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

import androidx.annotation.Nullable;

import com.try3x.uttam.R;

import java.util.Random;

public class GenericDialog {
    String TAG = "GenericDialog";
    String negativeButtonText, positiveButtonText, bodyText;
    Context context;
    private Dialog dialog;


    public OnGenericDialogListener onGenericDialogListener;

    public abstract interface OnGenericDialogListener {
        void onPositiveButtonClick();
        void onNegativeButtonClick();
        void onToast(String message);
    }

    public GenericDialog(@Nullable String bodyText, @Nullable String negativeButtonText, @Nullable String positiveButtonText, Context context) {
        this.negativeButtonText = negativeButtonText;
        this.positiveButtonText = positiveButtonText;
        this.bodyText = bodyText;
        this.context = context;
    }

    public OnGenericDialogListener getOnGenericDialogListener() {
        return onGenericDialogListener;
    }

    public void setOnGenericDialogListener(OnGenericDialogListener onGenericDialogListener) {
        this.onGenericDialogListener = onGenericDialogListener;
    }

    public void init(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_coin_to_mycoin);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);

        TextView txtBody = dialog.findViewById(R.id.txtBody);

        if (bodyText!=null){
            txtBody.setText(bodyText);
        }

        if (negativeButtonText!=null){
            btnNegative.setText(negativeButtonText);
        }

        if (positiveButtonText!=null){
            btnPositive.setText(positiveButtonText);
        }

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenericDialogListener.onPositiveButtonClick();
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenericDialogListener.onNegativeButtonClick();
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
