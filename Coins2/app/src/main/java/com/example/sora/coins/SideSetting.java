package com.example.sora.coins;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-03-21.
 */
public class SideSetting extends AppCompatActivity
{
    Intent intent;
    private Switch swc;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar_setting);

        intent = new Intent(getApplicationContext(), LockScreenService.class);

        swc = (Switch) findViewById(R.id.lockSwitch);
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        Boolean check = pref.getBoolean("swc", true);
        swc.setChecked(check);

        swc.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton cb, boolean isChecking)
            {
                if (isChecking)
                {
                    startService(intent);
                }

                else
                {
                    stopService(intent);
                }
            }
        });
    }

    @Override
    public void onStop()
    {
        super.onStop();
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("swc", swc.isChecked());
        editor.commit();
    }
}