package coins.hansung.way.SideMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import coins.hansung.way.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener
{
    final int REQ = 100;
    ImageView profileView, cancelView;
    EditText surname, name, email;
    Button createProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 초기화 및 리스너 지정
        profileView = (ImageView) findViewById(R.id.createProfileImage);
        cancelView = (ImageView) findViewById(R.id.createCancelImage);
        surname = (EditText) findViewById(R.id.createSurname);
        name = (EditText) findViewById(R.id.createName);
        email = (EditText) findViewById(R.id.createEmail);
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
                break;

            case R.id.createProfileButton: // 프로필 생성
                intent = new Intent(this, TermsActivity.class);

                String strSurname = surname.getText().toString();
                String strName = name.getText().toString();
                String strEmail = email.getText().toString();

                /*Drawable drawable = profileView.getBackground();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();*/

                if (strSurname.equals("") || strName.equals("") || strEmail.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "프로필을 채워주세요.", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    intent.putExtra("surname", strSurname);
                    intent.putExtra("name", strName);
                    intent.putExtra("email", strEmail);
                    /*intent.putExtra("profile", (Bitmap) bitmap);

                    Log.e("result", bitmap.toString());*/
                    startActivity(intent);
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