package com.example.sora.coins;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-03-21.
 */
public class SideSetting extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar_setting);
        Toast.makeText(getApplicationContext(), "환경 설정", Toast.LENGTH_LONG).show();
    }
}