package com.example.sora.coins;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sora on 2016-04-14.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    MyInfo myinfo;

    TextView ID;
    EditText Name;
    Button logout, change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        myinfo = MyInfo.getInstance();

        ID = (TextView) findViewById(R.id.IDView);
        Name = (EditText) findViewById(R.id.NameEdit);
        logout = (Button) findViewById(R.id.logoutButton);
        change = (Button) findViewById(R.id.changeButton);

        ID.setText(myinfo.getID());
        Name.setText(myinfo.getName());

        change.setOnClickListener(this);
        logout.setOnClickListener(this);

        //ID.setText(myinfo.getID());
        //Name.setText(myinfo.getName());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeButton :
                myinfo.setName(Name.getText().toString());
                Toast.makeText(this, "change success", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logoutButton :
                myinfo.init();
                Settings.Login=false;
                finish();
                break;

        }
    }
}

