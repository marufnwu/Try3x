package com.try3x.uttam.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BajiClaimService extends Service {

    // Binder given to clients
    private final IBinder binder = new BajiClaimServiceBinder();

    public class BajiClaimServiceBinder extends Binder {
        public BajiClaimService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BajiClaimService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
