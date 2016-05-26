package coins.hansung.way.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import coins.hansung.way.Intro.IntroMain;
import coins.hansung.way.R;

public class SplashActivity extends AppCompatActivity {

    final int splashTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try
        {
            Thread.sleep(splashTime);
        }

        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }

        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.fade, R.anim.hold);
        finish();
    }
}