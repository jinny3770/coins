package coins.hansung.way.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-05-23.
 */
public class IntroMain extends AppCompatActivity implements View.OnClickListener
{
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Button account = (Button) findViewById(R.id.btn_account);
        Button login = (Button) findViewById(R.id.btn_login);

        account.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;

        switch (v.getId())
        {
            case R.id.btn_account :
                intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                break;

            case R.id.btn_login :
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                break;
        }

        finish();
    }

    @Override
    public void onBackPressed()
    {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (intervalTime >= 0 && FINSH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }

        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(),"'뒤로'버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}