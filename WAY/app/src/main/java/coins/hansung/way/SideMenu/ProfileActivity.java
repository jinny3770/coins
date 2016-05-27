package coins.hansung.way.SideMenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import coins.hansung.way.Main.MainActivity;
import coins.hansung.way.R;
import coins.hansung.way.etc.MyInfo;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    final int REQ = 100;
    ImageView profileView, cancelView;
    EditText name, password, passwordRepeat;
    TextView id;
    Button fixProfile;
    MyInfo myinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 초기화 및 리스너 지정
        profileView = (ImageView) findViewById(R.id.fixProfileImage);
        cancelView = (ImageView) findViewById(R.id.fixCancelImage);

        name = (EditText) findViewById(R.id.fixName);
        id = (TextView) findViewById(R.id.fixID);
        password = (EditText) findViewById(R.id.fixPassword);
        passwordRepeat = (EditText) findViewById(R.id.fixPasswordRepeat);

        fixProfile = (Button) findViewById(R.id.fixProfileButton);

        profileView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        fixProfile.setOnClickListener(this);

        myinfo = MyInfo.getInstance();
        id.setText(myinfo.getID());
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;

        switch (v.getId())
        {
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
                String strPassword = password.getText().toString();
                String strPasswordRepeat = passwordRepeat.getText().toString();
                /*Drawable drawable = profileView.getBackground();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();*/

                if (strName.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "프로필을 채워주세요.", Toast.LENGTH_SHORT).show();
                }

                else if (!strPassword.equals(strPasswordRepeat))
                {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    SharedPreferences pref = getSharedPreferences("pref", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("fixName", strName);
                    editor.putString("fixPassword", strPassword);
                    editor.commit();
                    finish();
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
                    Bitmap profileBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    profileView.setImageBitmap(profileBitmap);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}