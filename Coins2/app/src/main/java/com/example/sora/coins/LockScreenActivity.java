package com.example.sora.coins;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-03-23.
 */
public class LockScreenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lockscreen); // 잠금화면 레이아웃

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // FLAG_SHOW_WHEN_LOCKED => 잠금화면보다 위에 Activity 출력 & FLAG_DISMISS_KEYGUARD => 기본 잠금화면 제거
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }

        return true;
    }
}