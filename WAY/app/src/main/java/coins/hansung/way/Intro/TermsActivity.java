package coins.hansung.way.Intro;

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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import coins.hansung.way.R;
import coins.hansung.way.etc.Links;
/**
 * Created by Administrator on 2016-05-25.
 */
public class TermsActivity extends AppCompatActivity implements View.OnClickListener {

    final int TERM_REQ = 1;
    final int PERSON_REQ = 2;

    public EditText phoneEditText;
    TextView check1text, check2text;
    public CheckBox check1, check2;
    Button signup;
    String name, id, password;
    String str;

    String imagePath;

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

        imagePath = intent.getStringExtra("imagePath");

        Log.d("resultTerm", "imagePath : " + imagePath);
        Log.d("resultTerm", name + ", " + id + ", " + password);


        check1text.setOnClickListener(this);
        check2text.setOnClickListener(this);

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

                        UploadImage uploadImage = new UploadImage();
                        uploadImage.execute(id, imagePath);

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
                startActivityForResult(terms, TERM_REQ);
                break;
            case R.id.check2text:
                Intent Personal = new Intent(TermsActivity.this, PersonalData.class);
                startActivityForResult(Personal, PERSON_REQ);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == TERM_REQ) {
            if(resultCode == RESULT_OK)
                check1.setChecked(true);
        }
        else if(requestCode == PERSON_REQ){
            if(resultCode == RESULT_OK) {
                check2.setChecked(true);
            }
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

    public class UploadImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            try {

                String id = params[0];
                String path = params[1];

                File imageFile = new File(path);

                FileInputStream fileInputStream = new FileInputStream(imageFile);
                URL url = new URL(Links.uploadImageURL);


                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", path);
                Log.d("UploadImage", "fileName : " + path);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"imageName\"" + lineEnd);

                dos.writeBytes(lineEnd);
                dos.writeBytes(id);
                dos.writeBytes(lineEnd);


                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + path + "\"" + lineEnd);
                dos.writeBytes(lineEnd);


                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    Log.d("uploadImage", "size > 0");
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                //String serverResponseMessage = conn.getResponseMessage();

                int serverResponseCode = conn.getResponseCode();
                Log.d("uploadImage", "responseCode + " + Integer.toString(serverResponseCode));

                fileInputStream.close();
                dos.flush();
                dos.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("UploadImage", e.getMessage());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("uploadImage", "message : " + s);
        }
    }
}