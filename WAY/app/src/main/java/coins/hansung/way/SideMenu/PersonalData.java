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
public class PersonalData extends AppCompatActivity{
    Button persbut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_data);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("개인정보 취급방침");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        persbut = (Button) findViewById(R.id.injungbutton);
        persbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PersonD = new Intent(getApplicationContext(), TermsActivity.class);
                TermsActivity.check2.setChecked(true);
                PersonD.putExtra("persbut", true);
                startActivity(PersonD);
            }
        });
    }
}
