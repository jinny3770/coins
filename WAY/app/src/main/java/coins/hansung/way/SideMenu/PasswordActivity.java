package coins.hansung.way.SideMenu;

import android.app.ActivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.MoreObjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import coins.hansung.way.R;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText currPW, newPw, newPw2;
    Button pwChange;

    String str;

    MyInfo myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        myInfo = MyInfo.getInstance();

        currPW = (EditText) findViewById(R.id.currentPW);
        newPw = (EditText) findViewById(R.id.newPW);
        newPw2 = (EditText) findViewById(R.id.newPW2);

        pwChange = (Button) findViewById(R.id.pwChangeButton);
        pwChange.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (currPW.getText().length() == 0 || newPw.getText().length() == 0 || newPw2.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "빈 칸을 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPw.getText().toString().equals(newPw2.getText().toString())) {
            Toast.makeText(getApplicationContext(), "비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        PasswordTask passwordTask = new PasswordTask();
        try {
            str = passwordTask.execute(myInfo.getID(), currPW.getText().toString(), newPw.getText().toString()).get();

            if(str.equals("success")) {
                Toast.makeText(getApplicationContext(), "비밀번호를 변경했습니다.", Toast.LENGTH_LONG).show();
                finish();
            }

            else {
                Toast.makeText(getApplicationContext(), "현재 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    class PasswordTask extends AsyncTask<String, Void, String> {

        String id, pw, newPw;

        String data;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {

            id = params[0];
            pw = params[1];
            newPw = params[2];


            BufferedReader reader;

            try {
                url = new URL(Links.pwChangeURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                        URLEncoder.encode("PW", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8") + "&" +
                        URLEncoder.encode("newPW", "UTF-8") + "=" + URLEncoder.encode(newPw, "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str = reader.readLine();
                return str;

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

    }

}
