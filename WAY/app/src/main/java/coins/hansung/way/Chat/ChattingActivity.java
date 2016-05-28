package coins.hansung.way.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import coins.hansung.way.R;
import coins.hansung.way.etc.MyInfo;

public class ChattingActivity extends FragmentActivity{

    private static final int REQUEST_SENDBIRD_CHAT_ACTIVITY = 100;
    private static final int REQUEST_SENDBIRD_CHANNEL_LIST_ACTIVITY = 101;
    private static final int REQUEST_SENDBIRD_MESSAGING_ACTIVITY = 200;
    private static final int REQUEST_SENDBIRD_MESSAGING_CHANNEL_LIST_ACTIVITY = 201;
    private static final int REQUEST_SENDBIRD_USER_LIST_ACTIVITY = 300;
    public static String VERSION = "2.0.9.1";
    MyInfo myInfo = MyInfo.getInstance();
    String message;
    /**
     To test push notifications with your own appId, you should replace google-services.json with yours.
     Also you need to set Server API Token and Sender ID in SendBird dashboard.
     Please carefully read "Push notifications" section in SendBird Android documentation
     */
    final String appId = "65502478-E77E-41D0-A636-4D5DEE969EEA"; /* Sample SendBird Application */
    String userId = ChatActivity.Helper.generateDeviceUUID(ChattingActivity.this); /* Generate Device UUID */
    String userName = myInfo.getName(); /* Generate User Nickname  + userId.substring(0, 5)*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        /**
         * Start GCM Service.
         */
        Intent intent = getIntent();
        startService(intent);



        Double Long, Lati;
        Integer Check = 0;
        String address;

        Long = intent.getDoubleExtra("Longitude", 0);
        Lati = intent.getDoubleExtra("Latitude", 0);
        Check = intent.getIntExtra("Check", 0);
        address = intent.getStringExtra("address");
        message = intent.getStringExtra("message");

        //Toast.makeText(ChatActivity.this, "ChatActivity : "+ Long + ", " + Lati+", "+Check, Toast.LENGTH_SHORT).show();

        ((EditText)findViewById(R.id.etxt_nickname)).setText(userName);

        ((EditText)findViewById(R.id.etxt_nickname)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                userName = s.toString();
            }
        });
/*
오픈챗 버튼 액티비티
        findViewById(R.id.btn_start_open_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChannelList();
            }
        });
/*
        findViewById(R.id.main_container).setVisibility(View.VISIBLE);
        findViewById(R.id.messaging_container).setVisibility(View.GONE);
        findViewById(R.id.btn_start_messaging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.main_container).setVisibility(View.GONE);
                findViewById(R.id.messaging_container).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_messaging_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.main_container).setVisibility(View.VISIBLE);
                findViewById(R.id.messaging_container).setVisibility(View.GONE);
            }
        });*/
        startMessagingChannelList(Long, Lati, Check, address);
        finish();
    }

    private void startChat(String channelUrl) {
        Intent intent = new Intent(ChattingActivity.this, ChatActivity.class);
        Bundle args = ChatActivity.makeSendBirdArgs(appId, userId, userName, channelUrl);

        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_CHAT_ACTIVITY);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    private void startUserList() {
        Intent intent = new Intent(ChattingActivity.this, UserListActivity.class);
        Bundle args = UserListActivity.makeSendBirdArgs(appId, userId, userName);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_USER_LIST_ACTIVITY);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    private void startMessaging(String [] targetUserIds) {
        Intent intent = new Intent(ChattingActivity.this, MessagingActivity.class);
        Bundle args = MessagingActivity.makeMessagingStartArgs(appId, userId, userName, targetUserIds);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_ACTIVITY);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    private void joinMessaging(String channelUrl) {
        Intent intent = new Intent(ChattingActivity.this, MessagingActivity.class);
        Bundle args = MessagingActivity.makeMessagingJoinArgs(appId, userId, userName, channelUrl);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_ACTIVITY);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    private void startMessagingChannelList(Double Long, Double Lati, Integer Check, String address) {
        Intent intent = new Intent(ChattingActivity.this, MessagingChannelListActivity.class);
        Bundle args = MessagingChannelListActivity.makeSendBirdArgs(appId, userId, userName);
        intent.putExtras(args);
        intent.putExtra("Longitude", Long);
        intent.putExtra("Latitude", Lati);
        intent.putExtra("Check", Check);
        intent.putExtra("address", address);
        intent.putExtra("message", message);

        startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_CHANNEL_LIST_ACTIVITY);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_SENDBIRD_MESSAGING_CHANNEL_LIST_ACTIVITY && data != null) {
            joinMessaging(data.getStringExtra("channelUrl"));
        }

        if(resultCode == RESULT_OK && requestCode == REQUEST_SENDBIRD_USER_LIST_ACTIVITY && data != null) {
            startMessaging(data.getStringArrayExtra("userIds"));
        }

        if(resultCode == RESULT_OK && requestCode == REQUEST_SENDBIRD_CHAT_ACTIVITY && data != null) {
            startMessaging(data.getStringArrayExtra("userIds"));
        }

        if(resultCode == RESULT_OK && requestCode == REQUEST_SENDBIRD_CHANNEL_LIST_ACTIVITY && data != null) {
            startChat(data.getStringExtra("channelUrl"));
        }
    }
}
