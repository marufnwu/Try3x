package com.try3x.uttam.Common;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.try3x.uttam.AddCoinActivity;
import com.try3x.uttam.R;

public class AddCoinDialog {
    private Context context;

    public AddCoinDialog(final Context context) {
        this.context = context;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_coin);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView txtBuyCoin = dialog.findViewById(R.id.txtBuyCoin);
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);

        if (!dialog.isShowing()){
            dialog.show();
        }

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        txtBuyCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                context.startActivity(new Intent(context, AddCoinActivity.class));
            }
        });
    }


}
