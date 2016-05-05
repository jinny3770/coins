package com.example.sora.coins.Sidebar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sora.coins.etc.MyInfo;
import com.example.sora.coins.R;
import com.example.sora.coins.etc.Settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sora on 2016-04-14.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    static String nameUpdateURL = "http://52.79.124.54/nameChange.php";

    MyInfo myinfo;

    TextView ID, groupCode;
    EditText nameEdit;
    Button logout, change;

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myinfo = MyInfo.getInstance();
        settings = Settings.getInstance();

        ID = (TextView) findViewById(R.id.IDView);
        groupCode = (TextView) findViewById(R.id.groupCodeView);
        nameEdit = (EditText) findViewById(R.id.NameEdit);
        logout = (Button) findViewById(R.id.logoutButton);
        change = (Button) findViewById(R.id.changeButton);

        ID.setText(myinfo.getID());
        groupCode.setText(myinfo.getGroupCode());
        nameEdit.setText(myinfo.getName());

        change.setOnClickListener(this);
        logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeButton :

                NameUpdate nameUpdate = new NameUpdate();
                nameUpdate.execute(nameEdit.getText().toString());

                myinfo.setName(nameEdit.getText().toString());
                Toast.makeText(this, "change success", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logoutButton :
                myinfo.init();
                settings.setLogin(false);
                finish();
                break;

        }
    }

    class NameUpdate extends AsyncTask<String, Void, String>{

        String data, name;
        BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {

            name = params[0];

            try {
                URL url = new URL(nameUpdateURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID","UTF-8") + "=" + URLEncoder.encode(myinfo.getID(), "UTF-8")
                        + "&"+ URLEncoder.encode("name", "utf8") + "=" + URLEncoder.encode(name, "utf8");

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

            if(s.equals("success")) {
                Toast.makeText(ProfileActivity.this, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                myinfo.setName(name);
            }else{
                nameEdit.setText(myinfo.getName());
                Toast.makeText(ProfileActivity.this, "이름 변경 실패패.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}

