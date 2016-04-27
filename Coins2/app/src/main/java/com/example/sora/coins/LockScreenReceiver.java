package com.example.sora.coins;

import android.app.KeyguardManager;
import android.content.*;

/**
 * Created by Administrator on 2016-03-23.
 */

public class LockScreenReceiver extends BroadcastReceiver
{
    private KeyguardManager km = null;
    private KeyguardManager.KeyguardLock keyLock = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
            if (km == null)
            {
                km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            }

            if (keyLock == null)
            {
                keyLock = km.newKeyguardLock(Context.KEYGUARD_SERVICE);
            }

            disableKeyguard();

            Intent i = new Intent(context, LockScreenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public void reenableKeyguard()
    {
        keyLock.reenableKeyguard();
    }

    public void disableKeyguard()
    {
        keyLock.disableKeyguard();
    }
}