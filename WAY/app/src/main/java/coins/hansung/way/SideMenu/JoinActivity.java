package coins.hansung.way.SideMenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-05-30.
 */

public class JoinActivity extends AppCompatActivity
{
    EditText joinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        joinCode = (EditText) findViewById(R.id.joinCode);
        Button joinButton = (Button) findViewById(R.id.joinButton);

        joinButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String code = joinCode.getText().toString();

                if (code.equals("")) // 공백 체크
                {
                    Toast.makeText(getApplicationContext(), "그룹코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                else if (code.length() != 6)
                {
                    Toast.makeText(getApplicationContext(), "잘못된 그룹코드를 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                    joinCode.setText(null);
                }

                else
                {
                    joinCode.setText(null);
                }

                /*else if (makePhoneNumber(phone) == null) // 전화번호 정규식 체크
                {
                    inviteNumber.setText(null);
                    Toast.makeText(getApplicationContext(), "전화번호 방식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    inviteNumber.setText(null);
                    phone = makePhoneNumber(phone);
                    inviteSMS(phone);
                }*/
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("그룹가입");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
