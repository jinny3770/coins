package com.example.sora.coins;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2016-04-25.
 */
public class SplashActivity extends AppCompatActivity {
    final int splashTime = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        try
        {
            Thread.sleep(2500);
        }

        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
