package com.example.sora.coins.Sidebar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sora.coins.MyInfo;
import com.example.sora.coins.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016-03-21.
 */

public class SideGroupActivity extends AppCompatActivity implements View.OnClickListener
{
    final String makeURL = "http://52.79.124.54/groupMaker.php";
    final String joinURL = "http://52.79.124.54/groupJoin.php";

    MyInfo myInfo;

    Button maker, checker, invite, join;
    EditText codeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_group);

        myInfo = MyInfo.getInstance();

        maker = (Button)findViewById(R.id.groupMaker);
        checker = (Button)findViewById(R.id.groupChecker);
        invite = (Button) findViewById(R.id.groupInvite);
        join = (Button) findViewById(R.id.groupJoin);

        codeEdit = (EditText) findViewById(R.id.groupCodeEdit);

        maker.setOnClickListener(this);
        checker.setOnClickListener(this);
        invite.setOnClickListener(this);
        join.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(myInfo.getGroupCode().equals("000000")) {
            maker.setEnabled(true);
            invite.setEnabled(false);
            join.setEnabled(true);
        }
        else{
            maker.setEnabled(false);
            invite.setEnabled(true);
            join.setEnabled(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.groupMaker :
                GroupMake groupMake = new GroupMake();
                groupMake.execute(myInfo.getID());
                break;

            case R.id.groupChecker:
                Toast.makeText(getApplicationContext(), myInfo.getGroupCode(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.groupInvite:
                // 문자로 code 보내줌
                break;

            case R.id.groupJoin :
                String str = codeEdit.getText().toString();

                if(str.length()!=6) {
                    Toast.makeText(SideGroupActivity.this, "코드 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    GroupJoin groupJoin = new GroupJoin();
                    groupJoin.execute(myInfo.getID(), str);
                }
                break;

        }
    }


    class GroupMake extends AsyncTask <String, Void, String>{

        String data;
        BufferedReader reader;

        @Override
        protected String doInBackground(String... params) {
            String ID = params[0];

            try {
                URL url = new URL(makeURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {

            if(!s.equals("fail")) {
                myInfo.setGroupCode(s);
                Toast.makeText(getApplicationContext(), "makeSuccess", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class GroupJoin extends AsyncTask<String, Void, String> {

        String data;
        BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {

            String ID = params[0];
            String Code = params[1];

            try {
                URL url = new URL(joinURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8")
                        + "&" + URLEncoder.encode("CODE", "UTF-8") + "=" + URLEncoder.encode(Code, "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(!s.equals("fail")) {
                myInfo.setGroupCode(s);
                Toast.makeText(getApplicationContext(), "JoinSuccess", Toast.LENGTH_SHORT).show();
            }
        }

    }
}