package coins.hansung.way.SideMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.regex.Pattern;

import coins.hansung.way.Main.MainActivity;
import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-05-25.
 */
public class TermsActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText phoneNumber;
    CheckBox check1, check2;
    Button signup;
    String surName, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        check1 = (CheckBox) findViewById(R.id.check1);
        check2 = (CheckBox) findViewById(R.id.check2);
        signup = (Button) findViewById(R.id.signupButton);
        signup.setOnClickListener(this);

        Intent intent = getIntent();
        surName = intent.getStringExtra("surname");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
    }

    @Override
    public void onClick(View v)
    {
        if (check1.isChecked() && check2.isChecked())
        {
            Intent intent = new Intent(this, MainActivity.class);
        }

        else
        {
            Toast.makeText(getApplicationContext(), "이용약관 및 개인정보 취급방침에 동의해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String makePhoneNumber(String phoneNumber)
    {
        String rule = "(\\d{3})(\\d{3,4})(\\d{4})";

        if (!Pattern.matches(rule, phoneNumber))
        {
            return null;
        }

        return phoneNumber.replaceAll(rule, "$1-$2-$3");
    }
}