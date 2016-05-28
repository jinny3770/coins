package coins.hansung.way.LockScreen;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import coins.hansung.way.R;

/**
 * Created by sora on 2016-05-21.
 */
public class LockScreenActivity extends AppCompatActivity implements View.OnTouchListener{
    private Thread thread;
    private String numbers[];
    private int initX, offsetX;
    private TextView timeView;
    private static MediaPlayer alertPlayer;
    float select_x;
    int x_bang, x2_bang;
    ImageView select;
    Drawable alert, unlock;
    final private int screens[] = new int[] {R.drawable.screen1, R.drawable.screen2, R.drawable.screen3, R.drawable.screen4, R.drawable.screen5, R.drawable.screen6, R.drawable.screen7};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen); // 잠금화면 레이아웃
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // 시계 출력
        timeView = (TextView) findViewById(R.id.time);

        thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while (!isInterrupted())
                    {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                long time = System.currentTimeMillis();
                                Date date = new Date(time);
                                SimpleDateFormat data = new SimpleDateFormat("\nyyyy.MM.dd\nHH : mm : ss");
                                String strTime = data.format(date);
                                timeView.setText(strTime);
                            }
                        });
                    }
                }

                catch (InterruptedException ie)
                {
                }
            }
        };

        thread.start();

        // 배경화면 랜덤 설정
        final RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.myLayout);
        int random = (int) (Math.random() * screens.length);
        myLayout.setBackgroundResource(screens[random]);

        alertPlayer = MediaPlayer.create(this, R.raw.alert);
        alert = ((ImageView) findViewById(R.id.alert)).getBackground();
        unlock = ((ImageView) findViewById(R.id.unlock)).getBackground();
        select = (ImageView) findViewById(R.id.select);
        initX = select.getWidth();
        select_x = select.getX();
        alert.setAlpha(0);
        unlock.setAlpha(0);
        select.setOnTouchListener(this);

        SharedPreferences pref = getSharedPreferences("pref", 0);
        String phone = pref.getString("phone", null);

        if (phone != null) {
            phone = phone.replace("[", "");
            phone = phone.replace("]", "");
            phone = phone.replace("\"", "");
            numbers = phone.split(",");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getRawX();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                offsetX = (int) (x - select.getTranslationX());
                break;

            case MotionEvent.ACTION_MOVE:
                select.setTranslationX(x - offsetX);
                check(select);
                x_bang = (Math.abs((int)(select_x - select.getX())))/2;
                x2_bang = (Math.abs((int)(select_x - select.getX())))/2;
                Log.d("Asdfsadfdsf", "x_bang : " + x_bang);
                if (x_bang>255) x_bang=255;
                alert.setAlpha(255-x_bang);
                if (x2_bang<255) x2_bang=255;
                if (x2_bang>500) x2_bang=510;
                if (300-x2_bang<100 && 300-x2_bang>0) x2_bang=255;
                unlock.setAlpha(x2_bang-255);
                break;

            case MotionEvent.ACTION_UP:
                alert.setAlpha(0);
                unlock.setAlpha(0);
                Log.d("adsdlfsflje", "ddem");
                select.setTranslationX(initX);
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) // 잠금화면에서 취소 버튼 누를 시 적용되는 메소드
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: // 취소 버튼
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void check(View select)
    {
        if (select.getX() < 55.0)
        {
            alertPlayer.start();
            sendSMS(numbers, "<WAY> 위급상황입니다.");
            select.setTranslationX(initX);
            finish();
        } else if (select.getX() > 1135.0) {
            select.setTranslationX(initX);
            finish();
        }
    }

    private void sendSMS(String numbers[], String message) {
        if (numbers == null) {
            Toast.makeText(getApplicationContext(), "비상연락망 리스트가 없습니다.", Toast.LENGTH_SHORT).show();
            return;

        } else {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            BroadcastReceiver send = new SendReceiver();
            BroadcastReceiver deliver = new DeliverReceiver();

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0); // 문자 보내는 상태 감지하는 Intent
            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0); // 문자 받는 상태 감지하는 Intent

            registerReceiver(send, new IntentFilter(SENT));
            registerReceiver(deliver, new IntentFilter(DELIVERED));

            SmsManager sms = SmsManager.getDefault();

            for (int i = 0; i < numbers.length; i++) {
                sms.sendTextMessage(numbers[i], null, message, sentPI, deliveredPI);
            }

            unregisterReceiver(send);
            unregisterReceiver(deliver);
        }
    }
}
