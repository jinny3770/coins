package coins.hansung.way.SideMenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import coins.hansung.way.R;

public class ProfileIntroActivity extends AppCompatActivity implements View.OnClickListener{


    Button profileButton, passwordButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_intro);

        profileButton = (Button) findViewById(R.id.profileChange);
        passwordButton= (Button) findViewById(R.id.passwordChange);

        profileButton.setOnClickListener(this);
        passwordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileChange :
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                break;

            case  R.id.passwordChange :
                intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
