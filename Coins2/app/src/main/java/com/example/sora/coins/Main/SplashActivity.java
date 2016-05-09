package com.example.sora.coins.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sora.coins.R;

/**
 * Created by Administrator on 2016-04-25.
 */

public class SplashActivity extends AppCompatActivity
{
    final int splashTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try
        {
            Thread.sleep(splashTime);
        }

        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
