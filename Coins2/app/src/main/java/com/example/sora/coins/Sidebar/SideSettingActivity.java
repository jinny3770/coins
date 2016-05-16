package com.example.sora.coins.Sidebar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.sora.coins.Chat.SendBirdMessagingActivity;
import com.example.sora.coins.Main.LoadFamilyList;
import com.example.sora.coins.Main.MainActivity;
import com.example.sora.coins.R;
import com.example.sora.coins.etc.MyInfo;
import com.example.sora.coins.etc.RegID;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016-03-21.
 */

public class SideSettingActivity extends AppCompatActivity
{
    RegID regID;
    MyInfo myInfo;
    private Switch swc;
    private Result result;
    private Message message;
    private final Sender sender = new Sender("AIzaSyAfClZJckVp_ZFNWWdl_kVOJFX2qzoP18s");


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_setting);

        swc = (Switch) findViewById(R.id.lockSwitch);
        SharedPreferences pref = getSharedPreferences("pref", 0);
        Boolean check = pref.getBoolean("swc", true);
        swc.setChecked(check);
        message = new Message.Builder().addData("message", "pupupupu").build();


        // GCM 테스트 & 그룹코드 가져오기
        Button asdf = (Button) findViewById(R.id.asdf);
        regID = RegID.getInstance();
        myInfo = MyInfo.getInstance();


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