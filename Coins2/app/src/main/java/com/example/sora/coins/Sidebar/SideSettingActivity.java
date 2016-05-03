package com.example.sora.coins.Sidebar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;

import com.example.sora.coins.R;

/**
 * Created by Administrator on 2016-03-21.
 */

public class SideSettingActivity extends AppCompatActivity
{
    private Switch swc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_setting);

        swc = (Switch) findViewById(R.id.lockSwitch);

        SharedPreferences pref = getSharedPreferences("pref", 0);
        Boolean check = pref.getBoolean("swc", true);
        swc.setChecked(check);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        SharedPreferences pref = getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("swc", swc.isChecked());
        editor.commit();
    }
}