package com.example.sora.coins.Sidebar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.sora.coins.Main.MainActivity;
import com.example.sora.coins.R;
import com.example.sora.coins.etc.RegID;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * Created by Administrator on 2016-03-21.
 */

public class SideSettingActivity extends AppCompatActivity
{
    private Switch swc;
    private Result result;
    private Message message;
    RegID regID = new RegID();
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

        Button asdf = (Button) findViewById(R.id.asdf);

        message = new Message.Builder().addData("message", "pupupupu").build();

        asdf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    result = sender.send(message, regID.getRegID(), 5);
                }

                catch (Exception e)
                {
                }

                if (result.getMessageId() != null)
                {
                    try
                    {
                        System.out.println("푸쉬 테스트");
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                else
                {
                    String error = result.getErrorCodeName();
                    System.out.println(error);

                    if (Constants.ERROR_INTERNAL_SERVER_ERROR.equals(error))
                    {
                        System.out.println("구글 서버 에러");
                    }
                }
            }
        });
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