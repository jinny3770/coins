package com.example.sora.coins.LockScreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-04-27.
 */
public class DeliverReceiver extends BroadcastReceiver // 문자 받는 상태 감지하는 Broadcast
{
    @Override
    public void onReceive(Context context, Intent intent) // 문자 받으면 메소드 실행
    {
        switch (getResultCode())
        {
            case Activity.RESULT_OK:
                Toast.makeText(null, "SMS 수신 완료", Toast.LENGTH_SHORT).show();
                break;

            case Activity.RESULT_CANCELED:
                Toast.makeText(null, "SMS 수신 실패", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
