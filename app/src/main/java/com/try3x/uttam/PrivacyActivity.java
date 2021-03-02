package com.try3x.uttam;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;

public class PrivacyActivity extends AppCompatActivity {

    private ACProgressPie dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getSupportActionBar ();
        actionbar.setDisplayHomeAsUpEnabled ( true );
        setContentView(R.layout.activity_privacy);
        createDialog();
        WebView view = (WebView) findViewById(R.id.webView);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
               showWaitingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissWaitingDialog();

            }
        });
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl("https://try3x.com/privacy-policy-2/");
    }

    private void createDialog() {
        dialog = new ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build();
        dialog.setCancelable(false);
    }

    private void showWaitingDialog() {

        if (dialog!=null && !dialog.isShowing() && !isFinishing()){
            dialog.show();
        }
    }

    private void dismissWaitingDialog() {
        if (!isFinishing() && dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}