package coins.hansung.way.SideMenu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import coins.hansung.way.R;
import coins.hansung.way.etc.Links;

public class AppInfoActivity extends AppCompatActivity {

    TextView apache, mit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        mit = (TextView) findViewById(R.id.mit);
        apache = (TextView) findViewById(R.id.apache);


        try {
            mit.setText(readText(R.raw.mit_license));
            apache.setText(readText(R.raw.apache_license));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readText(int textName) throws IOException {
        String data = null;

        InputStream inputStream = getResources().openRawResource(textName);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;

        i = inputStream.read();
        while (i != -1) {
            byteArrayOutputStream.write(i);
            i = inputStream.read();
        }
        data = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
        inputStream.close();

        return data;
    }
}
