package coins.hansung.way.SideMenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

    String imagePath;

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
        if (myinfo.getProfileImage() != null) {
            profileView.setImageBitmap(myinfo.getProfileImage());
        }
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
                imagePath = null;
                break;

            case R.id.fixProfileButton: // 프로필 수정
                String strName = name.getText().toString();


                if (strName.equals("")) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (strName.equals(myinfo.getName())) {
                    if (imagePath != null) {

                        UploadImage uploadImage = new UploadImage();
                        uploadImage.execute(myinfo.getID(), imagePath);

                    }
                    finish();
                } else {

                    NameTask nameTask = new NameTask();
                    nameTask.execute(myinfo.getID(), strName);

                    if (imagePath != null) {

                        UploadImage uploadImage = new UploadImage();
                        uploadImage.execute(myinfo.getID(), imagePath);

                    }

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
                    imagePath = imageUriToString(data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String imageUriToString(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
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

            if (s.equals("success")) {
                Toast.makeText(getApplicationContext(), "이름을 변경했습니다.", Toast.LENGTH_SHORT).show();
                SharedPreferences pref = getSharedPreferences("Login", 0);
                SharedPreferences.Editor prefEdit = pref.edit();
                prefEdit.putString("Name", name);
                prefEdit.commit();
                finish();
            } else
                Toast.makeText(getApplicationContext(), "이름 변경에 실패했습니다.", Toast.LENGTH_SHORT).show();
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

            if (s.equals("upload success")) {
                Toast.makeText(getApplicationContext(), "프로필 사진을 변경하였습니다", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "프로필 사진을 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}