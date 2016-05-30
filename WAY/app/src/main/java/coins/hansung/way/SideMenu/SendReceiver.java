package coins.hansung.way.SideMenu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by sora on 2016-05-21.
 */
public class SendReceiver extends BroadcastReceiver // 문자 보내는 상태 감지하는 Broadcast
{
    @Override
    public void onReceive(Context context, Intent intent) // 문자 보내면 메소드 실행
    {
        switch (getResultCode())
        {
            case Activity.RESULT_OK:
                Toast.makeText(null, "SMS 송신 완료", Toast.LENGTH_SHORT).show();
                break;

            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(null, "SMS 송신 실패", Toast.LENGTH_SHORT).show();
                break;

            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(null, "서비스 지역이 아닙니다.", Toast.LENGTH_SHORT).show();
                break;

            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(null, "무선이 꺼져있습니다.", Toast.LENGTH_SHORT).show();
                break;

            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(null, "PDU Null", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
