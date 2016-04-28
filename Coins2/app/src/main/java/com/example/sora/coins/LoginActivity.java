package com.example.sora.coins;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    EditText id, pw;
    Button signup, login;
    MyInfo myInfo;

    LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myInfo = MyInfo.getInstance();

        id = (EditText) findViewById(R.id.IDEdit);
        pw = (EditText) findViewById(R.id.PWEdit);

        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);

        signup.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup:
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                break;

            case R.id.login:

                if(LoginCheck()) {
                    loginTask = new LoginTask();
                    loginTask.execute(id.getText().toString(), pw.getText().toString());
                }
                break;
        }
    }

    boolean LoginCheck() {
        if(id.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(pw.getText().length() == 0) {
            Toast.makeText(LoginActivity.this, "PW를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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


        @Override
        protected void onPostExecute(String s) {

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

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObj = jsonArray.getJSONObject(i);
                        myInfo.setName(jsonObj.getString("name"));
                        myInfo.setGroupCode(jsonObj.getString("group_code"));
                    }
                    //myInfo.setName(jsonObj.getString("name"));
                    //

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Settings.Login = true;
                finish();
            }
        }

    }
}
