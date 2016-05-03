package com.example.sora.coins.Sidebar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sora.coins.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sora on 2016-04-07.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    final static String signUpURL = "http://52.79.124.54/signup.php";

    EditText id, pw, pw2, name;
    Button signupButton;

    SignUpTask signUpTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        id = (EditText) findViewById(R.id.signUpID);
        pw = (EditText) findViewById(R.id.signUpPW);
        pw2 = (EditText) findViewById(R.id.signUpPWRepeat);
        name = (EditText) findViewById(R.id.signUpName);


        signupButton = (Button) findViewById(R.id.signupButton);
        signupButton.setOnClickListener(this);

    }

    // 다시 볼 것.!
    // email 형식 check
    private boolean checkEmail(String email) {

        //String mail = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        //Pattern p = Pattern.compile(mail);

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    // pw 형식 check
    private boolean checkPW (String pw) {
        String password = "^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{4,16}$";
        Pattern p = Pattern.compile(password);
        Matcher m = p.matcher(pw);
        return m.matches();
    }


    @Override
    public void onClick(View v) {

        if (id.getText().length() < 1 || pw.getText().length() < 1
                || pw2.getText().length() < 1 || name.getText().length() < 1) {
            Toast.makeText(SignUpActivity.this, "모두 입력해주세요.", Toast.LENGTH_LONG).show();
        } else if(checkEmail(id.getText().toString())) {
            Toast.makeText(SignUpActivity.this, "이메일 형식이 아닙니다.", Toast.LENGTH_LONG).show();
        } else if (checkPW(pw.getText().toString())){
            Toast.makeText(SignUpActivity.this, "비밀번호가 같지 않습니다.", Toast.LENGTH_LONG).show();
        } else if (!pw.getText().toString().equals(pw2.getText().toString())) {
            Toast.makeText(SignUpActivity.this, "형식에 맞지 않습니다.", Toast.LENGTH_LONG).show();
        } else if (name.getText().length() > 32) {
            Toast.makeText(SignUpActivity.this, "이름이 너무 깁니다.", Toast.LENGTH_LONG).show();
        } else {
            signUpTask = new SignUpTask();
            signUpTask.execute(id.getText().toString(), pw.getText().toString(), name.getText().toString());
        }
    }


    class SignUpTask extends AsyncTask<String, Void, String> {

        String data;
        BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {


            try {
                String ID = (String) params[0];
                String PW = (String) params[1];
                String name = (String) params[2];

                //String urlString = Integer.toString(R.string.signUpURL);

                URL url = new URL(signUpURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);


                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8")
                        + "&" + URLEncoder.encode("PW", "UTF-8") + "=" + URLEncoder.encode(PW, "UTF-8")
                        + "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");



                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("success")) {
                Toast.makeText(SignUpActivity.this, "가입 성공", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "존재하는 ID입니다.", Toast.LENGTH_LONG).show();
            }

        }
    }
}



