package coins.hansung.way.SideMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import coins.hansung.way.R;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;

/**
 * Created by Administrator on 2016-05-30.
 */

public class JoinActivity extends AppCompatActivity {

    EditText joinCode;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        joinCode = (EditText) findViewById(R.id.joinCode);
        Button joinButton = (Button) findViewById(R.id.joinButton);
        intent = getIntent();

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = joinCode.getText().toString();

                if (code.equals("")) // 공백 체크
                {
                    Toast.makeText(getApplicationContext(), "그룹코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (code.length() != 6) {
                    Toast.makeText(getApplicationContext(), "잘못된 그룹코드를 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                    joinCode.setText(null);
                } else {
                    JoinTask joinTask = new JoinTask();
                    joinTask.execute(MyInfo.getInstance().getID(), joinCode.getText().toString());
                }

            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("그룹가입");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    class JoinTask extends AsyncTask<String, Void, String> {

        String id, code;

        @Override
        protected String doInBackground(String... params) {

            id = params[0];
            code = params[1];

            try {
                URL url = new URL(Links.groupJoinURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                String data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") +
                        "&" + URLEncoder.encode("CODE", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8");

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

            if (s.equals(code)) {
                Toast.makeText(getApplicationContext(), "그룹에 가입되었습니다.", Toast.LENGTH_SHORT).show();
                MyInfo myInfo = MyInfo.getInstance();
                myInfo.setGroupCode(code);

                SharedPreferences pref = getSharedPreferences("Login", 0);
                SharedPreferences.Editor prefEdit = pref.edit();

                prefEdit.putString("Code", code);
                prefEdit.commit();

                setResult(RESULT_OK);

                finish();

            }
            else Toast.makeText(getApplicationContext(), "그룹에 가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
