package coins.hansung.way.SideMenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import coins.hansung.way.Main.MainActivity;
import coins.hansung.way.R;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    final int REQ = 100;
    ImageView profileView, cancelView;
    EditText name;
    TextView id;
    Button fixProfile;
    MyInfo myinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 초기화 및 리스너 지정
        profileView = (ImageView) findViewById(R.id.fixProfileImage);
        cancelView = (ImageView) findViewById(R.id.fixCancelImage);

        name = (EditText) findViewById(R.id.fixName);
        id = (TextView) findViewById(R.id.fixID);

        fixProfile = (Button) findViewById(R.id.fixProfileButton);

        profileView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        fixProfile.setOnClickListener(this);

        myinfo = MyInfo.getInstance();
        name.setText(myinfo.getName());
        id.setText(myinfo.getID());
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.fixProfileImage: // 프로필 사진 선택
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                break;

            case R.id.fixCancelImage: // 프로필 사진 취소
                profileView.setImageResource(R.drawable.profile);
                break;

            case R.id.fixProfileButton: // 프로필 수정
                String strName = name.getText().toString();
                String str;

                if (strName.equals("")) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (strName.equals(myinfo.getName())) {
                    finish();
                } else {

                    NameTask nameTask = new NameTask();
                    nameTask.execute(myinfo.getID(), strName);

                }

                break;
        }
    }

    // 프로필 사진 선택
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Bitmap profileBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    profileView.setImageBitmap(profileBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class NameTask extends AsyncTask<String, Void, String> {

        String id, name;
        String data;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            id = params[0];
            name = params[1];

            BufferedReader reader;


            try {
                url = new URL(Links.nameChangeURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");

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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("success")) {
                Toast.makeText(getApplicationContext(), "이름을 변경했습니다.", Toast.LENGTH_SHORT).show();
                myinfo.setName(name);
                finish();
            }
            else Toast.makeText(getApplicationContext(), "이름 변경에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}