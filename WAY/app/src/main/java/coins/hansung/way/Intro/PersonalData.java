package coins.hansung.way.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import coins.hansung.way.Intro.TermsActivity;
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
        TextView tv = (TextView) findViewById(R.id.textView);
        try {
            tv.setText(readTxt());
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        persbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    private String readTxt() {
        String data = null;
        InputStream inputstream = getResources().openRawResource(R.raw.person);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputstream.read();
            while(i!=-1) {
                byteArrayOutputStream.write(i);
                i=inputstream.read();
            }
            data = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            inputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}