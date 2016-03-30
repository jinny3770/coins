package com.example.sora.coins;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.view.KeyEvent;

/**
 * Created by Administrator on 2016-03-23.
 */
public class LockScreenService extends Service
{
    private LockScreenReceiver myReceiver = null;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        myReceiver = new LockScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(myReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        if (intent != null)
        {
            if (intent.getAction() == null)
            {
                if (myReceiver == null)
                {
                    myReceiver = new LockScreenReceiver();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(myReceiver, filter);
                }
            }
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (myReceiver != null)
        {
            unregisterReceiver(myReceiver);
        }
    }
}