package coins.hansung.way.SideMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import coins.hansung.way.Main.LoadFamilyList;
import coins.hansung.way.R;
import coins.hansung.way.etc.Family;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;
import coins.hansung.way.etc.PersonInfo;

public class GroupManageActivity extends AppCompatActivity implements View.OnClickListener {
    TextView groupCode;
    Button btnInvite, btncheckInvite, btnCreateGroup;
    MyInfo myinfo = MyInfo.getInstance();

    ListView alertView;
    GroupListAdapter adapter;

    Family family = Family.getInstance();

    final int REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupCode = (TextView) findViewById(R.id.groupcode);
        btnInvite = (Button) findViewById(R.id.inviteUser); // 있
        btncheckInvite = (Button) findViewById(R.id.checkInvite); // 없
        btnCreateGroup = (Button) findViewById(R.id.createGroup); // 없

        if (!myinfo.getGroupCode().equals("000000")) // 그룹코드가 있을 때
        {
            groupCode.setText(myinfo.getGroupCode());
            btncheckInvite.setVisibility(View.INVISIBLE);
            btnCreateGroup.setVisibility(View.INVISIBLE);
            btnInvite.setVisibility(View.VISIBLE);

        } else // 그룹코드가 없을 때
        {
            groupCode.setText("000000");
            btnInvite.setVisibility(View.INVISIBLE);
            btncheckInvite.setVisibility(View.VISIBLE);
            btnCreateGroup.setVisibility(View.VISIBLE);
        }

        alertView = (ListView) findViewById(R.id.alertList);
        adapter = new GroupListAdapter();
        alertView.setAdapter(adapter);


        try {
            Log.e("size", String.valueOf(family.getFamilyArray().size()));

            for (int i = 0; i < family.getFamilyArray().size(); i++) {
                PersonInfo p = (PersonInfo) family.getFamilyArray().get(i);
                adapter.addItem(p.getName(), p.getPhoneNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("그룹관리");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btnInvite.setOnClickListener(this);
        btncheckInvite.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.inviteUser:
                intent = new Intent(getApplicationContext(), InviteActivity.class);
                intent.putExtra("code", myinfo.getGroupCode());
                startActivity(intent);
                break;

            case R.id.checkInvite:
                Log.d("GroupManageActivity", "joinClick");
                intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivityForResult(intent, REQ_CODE);
                break;

            case R.id.createGroup:
                MakeGroupTask makeGroupTask = new MakeGroupTask();
                makeGroupTask.execute(myinfo.getID());

                btnInvite.setVisibility(View.VISIBLE);
                btncheckInvite.setVisibility(View.GONE);
                btnCreateGroup.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("group", groupCode.getText().toString());
        editor.commit();
    }


    class MakeGroupTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String id = params[0];

            try {
                URL url = new URL(Links.groupMakingURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                String data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(myinfo.getID(), "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str = reader.readLine();

                return str;

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("fail")) {
                Toast.makeText(getApplication(), "그룹 생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                myinfo.setGroupCode(s);
                groupCode.setText(s);
                Toast.makeText(getApplication(), "그룹을 생성하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}