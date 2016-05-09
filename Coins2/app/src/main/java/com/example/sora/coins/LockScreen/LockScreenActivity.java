package com.example.sora.coins.LockScreen;

import android.app.*;
import android.content.*;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.example.sora.coins.R;
import com.example.sora.coins.etc.Settings;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-03-23.
 */

public class LockScreenActivity extends Activity implements View.OnTouchListener
{
    private String numbers[];
    private int initX, offsetX;
    private static MediaPlayer alertPlayer;
    private ImageView alert, select, unlock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen); // 잠금화면 레이아웃
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        alertPlayer = MediaPlayer.create(this, R.raw.alert);
        alert = (ImageView) findViewById(R.id.alert);
        select = (ImageView) findViewById(R.id.select);
        unlock = (ImageView) findViewById(R.id.unlock);
        initX = select.getWidth();
        select.setOnTouchListener(this);

        SharedPreferences pref = getSharedPreferences("pref", 0);
        String phone = pref.getString("phone", null);

        if (phone != null)
        {
            phone = phone.replace("[", "");
            phone = phone.replace("]", "");
            phone = phone.replace("\"", "");
            numbers = phone.split(",");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        final int x = (int) event.getRawX();

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                offsetX = (int) (x - select.getTranslationX());
                break;

            case MotionEvent.ACTION_MOVE:
                select.setTranslationX(x - offsetX);
                check(select, alert, unlock);
                break;

            case MotionEvent.ACTION_UP:
                select.setTranslationX(initX);
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) // 잠금화면에서 취소 버튼 누를 시 적용되는 메소드
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK : // 취소 버튼
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void check(View select, View alert, View unlock)
    {
        if (select.getX() < alert.getX() + 25)
        {
            alertPlayer.start();
            sendSMS(numbers, "문자보내기 테스트");
            select.setTranslationX(initX);
            finish();
        }

        else if (select.getX() > unlock.getX() - 25)
        {
            select.setTranslationX(initX);
            finish();
        }
    }

    private void sendSMS(String numbers[], String message)
    {
        if (numbers == null)
        {
            Toast.makeText(getApplicationContext(), "비상연락망 리스트가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        else
        {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            BroadcastReceiver send = new SendReceiver();
            BroadcastReceiver deliver = new DeliverReceiver();

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0); // 문자 보내는 상태 감지하는 Intent
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0); // 문자 받는 상태 감지하는 Intent

        registerReceiver(send, new IntentFilter(SENT));
        registerReceiver(deliver, new IntentFilter(DELIVERED));

            SmsManager sms = SmsManager.getDefault();

            for (int i = 0; i < numbers.length; i++)
            {
                sms.sendTextMessage(numbers[i], null, message, sentPI, deliveredPI);
            }

            unregisterReceiver(send);
            unregisterReceiver(deliver);
        }
    }
}