package coins.hansung.way.SideMenu;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import coins.hansung.way.LockScreen.DeliverReceiver;
import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-05-30.
 */
public class InviteActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        final EditText inviteNumber = (EditText) findViewById(R.id.inviteNumber);
        Button inviteButton = (Button) findViewById(R.id.inviteButton);

        inviteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String phone = inviteNumber.getText().toString();

                if (phone.equals("")) // 공백 체크
                {
                    Toast.makeText(getApplicationContext(), "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    inviteNumber.setText(null);
                }

                else if (makePhoneNumber(phone) == null) // 전화번호 정규식 체크
                {
                    Toast.makeText(getApplicationContext(), "전화번호 방식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    inviteNumber.setText(null);
                }

                else
                {
                    phone = makePhoneNumber(phone);
                    inviteSMS(phone);
                    inviteNumber.setText(null);
                }
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("그룹초대");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public static String makePhoneNumber(String phone) // 전화번호 정규식
    {
        String regExample = "(\\d{3})(\\d{3,4})(\\d{4})";
        String regExample2 = "(\\d{3})-(\\d{3,4})-(\\d{4})";

        if (!Pattern.matches(regExample, phone) && !Pattern.matches(regExample2, phone))
        {
            return null;
        }

        return phone.replaceAll(regExample, "$1-$2-$3");
    }

    private void inviteSMS(String number)
    {
        Intent intent = getIntent();
        String message = "<WAY> 초대문자가 왔습니다. \n가입하시려면 " + intent.getStringExtra("code") + "로 가입해주세요.";
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        BroadcastReceiver send = new SendReceiver();
        BroadcastReceiver deliver = new DeliverReceiver();

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0); // 문자 보내는 상태 감지하는 Intent
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0); // 문자 받는 상태 감지하는 Intent

        registerReceiver(send, new IntentFilter(SENT));
        registerReceiver(deliver, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, sentPI, deliveredPI);
        Toast.makeText(getApplicationContext(), "초대문자 전송완료", Toast.LENGTH_SHORT).show();

        unregisterReceiver(send);
        unregisterReceiver(deliver);
    }
}
