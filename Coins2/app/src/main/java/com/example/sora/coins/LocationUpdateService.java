package com.example.sora.coins;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by sora on 2016-04-01.
 */

public class LocationUpdateService extends Service {

    Runnable locationUpdate = new Runnable() {
        @Override
        public void run() {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_REDELIVER_INTENT;
    }
}
