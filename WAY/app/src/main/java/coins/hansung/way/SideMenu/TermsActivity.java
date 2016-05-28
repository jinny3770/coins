package coins.hansung.way.SideMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import coins.hansung.way.Intro.IntroMain;
import coins.hansung.way.Main.MainActivity;
import coins.hansung.way.R;
import coins.hansung.way.etc.Links;

/**
 * Created by Administrator on 2016-05-25.
 */
public class TermsActivity extends AppCompatActivity implements View.OnClickListener {

    final int TERM_REQ = 13;

    public static EditText phoneEditText;
    TextView check1text, check2text;
    public static CheckBox check1, check2;
    Button signup;
    String name, id, password;
    String str;
    boolean termbut=false;
    boolean persbut=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        phoneEditText = (EditText) findViewById(R.id.phoneNumber);

        check1 = (CheckBox) findViewById(R.id.check1);
        check1text = (TextView) findViewById(R.id.check1text);
        check2 = (CheckBox) findViewById(R.id.check2);
        check2text = (TextView) findViewById(R.id.check2text);
        signup = (Button) findViewById(R.id.signupButton);
        signup.setOnClickListener(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        id = intent.getStringExtra("id");
        password = intent.getStringExtra("password");

        Log.d("resultTerm", name + ", " + id + ", " + password);
        check1text.setOnClickListener(this);
        check2text.setOnClickListener(this);

        Intent termOU = getIntent();
        termbut = termOU.getBooleanExtra("termbut", false);
        if (termbut == true) {
            check1.setChecked(true);
        }
        Intent PersonD = getIntent();
        persbut = PersonD.getBooleanExtra("persbut", false);
        if (persbut == true) {
            check2.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.signupButton:
                Intent intent = new Intent(this, IntroMain.class);
                String phone = makePhoneNumber(phoneEditText.getText().toString());

                if (phone == null) {
                    Toast.makeText(getApplicationContext(), "연락처 방식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (!check1.isChecked() || !check2.isChecked()) {
                    Toast.makeText(getApplicationContext(), "이용약관 및 개인정보 취급방침에 동의해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    SendInfoTask task = new SendInfoTask(name, id, password, phone);
                    try {
                        str = task.execute().get().toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (str.equals("exist")) {
                        Toast.makeText(getApplicationContext(), "중복된 아이디입니다.", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent outIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                        Toast.makeText(TermsActivity.this, "가입이 완료되었습니다.", Toast.LENGTH_SHORT);
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                    }
                }
                break;
            case R.id.check1text:
                Intent terms = new Intent(TermsActivity.this, TermsOfUse.class);
                startActivity(terms);
                break;
            case R.id.check2text:
                Intent Personal = new Intent(TermsActivity.this, PersonalData.class);
                startActivity(Personal);
                break;
        }
    }

    public static String makePhoneNumber(String phoneNumber) {
        String rule = "(\\d{3})(\\d{3,4})(\\d{4})";
        String rule2 = "(\\d{3})-(\\d{3,4})-(\\d{4})";

        if (!Pattern.matches(rule, phoneNumber) && !Pattern.matches(rule2, phoneNumber)) {
            return null;
        }

        return phoneNumber.replaceAll(rule, "$1-$2-$3");
    }

    public class SendInfoTask extends AsyncTask<Void, Void, String> {
        String name, id, password, phoneNumber;
        BufferedReader reader = null;

        SendInfoTask(String name, String id, String password, String phoneNumber) {
            this.name = name;
            this.id = id;
            this.password = password;
            this.phoneNumber = phoneNumber;
            Log.d("resultTask", name + ", " + id + ", " + password + ", " + phoneNumber);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(Links.signupURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                String sendData = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                        URLEncoder.encode("PW", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(phoneNumber, "UTF-8");

                Log.e("result", sendData);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(sendData);
                writer.flush();
                Log.e("result", "S");

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("result", "e : " + e.getMessage());
                return e.toString();

            }
        }
    }
}