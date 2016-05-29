package coins.hansung.way.Intro;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


import coins.hansung.way.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener
{
    final int REQ = 100;
    final int TERM_REQ = 13;
    ImageView profileView, cancelView;
    EditText name, id, password, passwordRepeat;
    Button createProfile;

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 초기화 및 리스너 지정
        profileView = (ImageView) findViewById(R.id.createProfileImage);
        cancelView = (ImageView) findViewById(R.id.createCancelImage);

        name = (EditText) findViewById(R.id.createName);
        id = (EditText) findViewById(R.id.createID);
        password = (EditText) findViewById(R.id.createPassword);
        passwordRepeat = (EditText) findViewById(R.id.createPasswordRepeat);

        createProfile = (Button) findViewById(R.id.createProfileButton);

        profileView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        createProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;

        switch (v.getId())
        {
            case R.id.createProfileImage: // 프로필 사진 선택
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQ);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                break;

            case R.id.createCancelImage: // 프로필 사진 취소
                profileView.setImageResource(R.drawable.profile);
                imagePath = null;
                break;

            case R.id.createProfileButton: // 프로필 생성
                intent = new Intent(this, TermsActivity.class);

                String strName = name.getText().toString();
                String strID = id.getText().toString();
                String strPassword = password.getText().toString();
                String strPasswordRepeat = passwordRepeat.getText().toString();

                if (strName.equals("") || strID.equals("") || strPassword.equals("") || strPasswordRepeat.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "프로필을 채워주세요.", Toast.LENGTH_SHORT).show();
                }

                else if (!strPassword.equals(strPasswordRepeat))
                {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    intent.putExtra("name", strName);
                    intent.putExtra("id", strID);
                    intent.putExtra("password", strPassword);

                    if(imagePath != null) {
                        Log.d("imagePath", imagePath.toString());
                        intent.putExtra("imagePath", imagePath);
                    }

                    Log.d("result", strName + ", " + strID + ", " + strPassword + ", " + strPasswordRepeat);
                    startActivityForResult(intent, TERM_REQ);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                }

                break;
        }
    }


    // 프로필 사진 선택
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQ)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                try
                {
                    Uri uri = data.getData();
                    imagePath = imageUriToString(uri);

                    Bitmap profileBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    profileView.setImageBitmap(profileBitmap);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        else if (requestCode == TERM_REQ) {
            if(resultCode == RESULT_OK) {
                Log.d("SignUpActivity", "OK");
                finish();
            }
        }
    }

    String imageUriToString(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

}