package com.example.sora.coins;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-03-23.
 */
public class LockScreenActivity extends Activity {
    private static MediaPlayer alertPlayer;
    private static int x, y; // btnSelect의 좌표값
    Button btnAlert, btnSelect, btnUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.lockscreen); // 잠금화면 레이아웃

        btnAlert = (Button) findViewById(R.id.alert);
        btnSelect = (Button) findViewById(R.id.select);
        btnUnlock = (Button) findViewById(R.id.unlock);

        alertPlayer = MediaPlayer.create(this, R.raw.alert);

        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertPlayer.start();
                sendSMS("010-7347-5027", "문자보내기 테스트");
            }
        });

        btnSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float offsetX = 0;
                boolean isMoving = false;
                boolean chk = true;

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    chk = false;
                    isMoving = true;
                }

                else if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    offsetX = v.getWidth() - event.getX();
                    v.setX((int) event.getRawX() - offsetX);
                    check(v, btnAlert, btnUnlock);
                }

                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    isMoving = false;
                }

                return false;
            }
        });

        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: // 취소 버튼
                return true;

            case KeyEvent.KEYCODE_HOME:
                break;

            case KeyEvent.KEYCODE_MENU:
                break;
        }

        return true;
    }

    public void sendSMS(String number, String text) {
        PendingIntent sendIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SEND_ACTION"), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS 전송완료", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "SMS 전송실패", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getApplicationContext(), "서비스 지역이 아닙니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getApplicationContext(), "무선이 꺼져있습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getApplicationContext(), "PDU Null", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SNS_SEND_ACTION"));

        SmsManager mySmsManager = SmsManager.getDefault();
        mySmsManager.sendTextMessage(number, null, text, sendIntent, null);
    }

    public void check(View select, View alert, View unlock)
    {
        if (select.getX() < alert.getX() + 50)
        {
            Toast.makeText(getApplicationContext(), "왼쪽", Toast.LENGTH_SHORT).show();
        }

        else if (select.getX() > unlock.getX() - 50)
        {
            finish();
        }
    }
}