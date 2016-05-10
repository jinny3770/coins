package com.example.sora.coins.Sidebar;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sora on 2016-04-07.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    final static String loginURL = "http://52.79.124.54/login.php";

    MyInfo myInfo;

    EditText id, pw;
    Button login;
    TextView textView;

    SharedPreferences loginPref;
    SharedPreferences.Editor loginPrefEditor;


    LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myInfo = MyInfo.getInstance();

        id = (EditText) findViewById(R.id.IDEdit);
        pw = (EditText) findViewById(R.id.PWEdit);

        login = (Button) findViewById(R.id.login);
        textView = (TextView) findViewById(R.id.textView);

        loginPref = getSharedPreferences("Login", 0);
        loginPrefEditor = loginPref.edit();

        login.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView:
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                break;

            case R.id.login:

                if (LoginCheck()) {
                    loginTask = new LoginTask();
                    try {
                        String s = loginTask.execute(id.getText().toString(), pw.getText().toString()).get();
                        myInfoParsing(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    boolean LoginCheck() {
        if (id.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (pw.getText().length() == 0) {
            Toast.makeText(LoginActivity.this, "PW를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    void myInfoParsing(String s) {

        JSONObject jsonObj;
        JSONArray jsonArray;


        if (s.equals("fail")) {
            Toast.makeText(LoginActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
        } else if (s.equals("not exist")) {
            Toast.makeText(LoginActivity.this, "존재하지 않습니다.", Toast.LENGTH_LONG).show();
        } else {

            myInfo.setID(id.getText().toString());

            try {
                jsonArray = new JSONArray(s);
                jsonObj = jsonArray.getJSONObject(0);

                String name = jsonObj.getString("name");
                String groupCode = jsonObj.getString("group_code");
                myInfo.setName(name);
                myInfo.setGroupCode(groupCode);

                //id groupcode name point
                loginPrefEditor.putString("ID", id.getText().toString());
                loginPrefEditor.putString("PW", pw.getText().toString());
                loginPrefEditor.putString("Name", name);
                loginPrefEditor.putString("Code", groupCode);
                loginPrefEditor.putBoolean("AutoLogin", true);
                loginPrefEditor.commit();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            finish();
        }
    }

    class LoginTask extends AsyncTask<String, Void, String> {

        String data;
        BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {

            String PW = params[1];
            String ID = params[0];

            try {
                URL url = new URL(loginURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8")
                        + "&" + URLEncoder.encode("PW", "UTF-8") + "=" + URLEncoder.encode(PW, "UTF-8");

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

    }
}
