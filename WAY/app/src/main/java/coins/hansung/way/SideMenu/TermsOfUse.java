package coins.hansung.way.SideMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-05-27.
 */
public class TermsOfUse extends AppCompatActivity{
    Button termbut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_use);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("이용약관");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        termbut = (Button) findViewById(R.id.injungbutton);
        termbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termOU = new Intent(getApplicationContext(), TermsActivity.class);
                termOU.putExtra("termbut", true);
                TermsActivity.check1.setChecked(true);
                startActivity(termOU);
            }
        });
    }
}
