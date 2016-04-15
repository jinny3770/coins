package com.example.sora.coins;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.sora.coins.gcm.RegistrationIntentService;

/**
 * SendBird Prebuilt UI
 */
public class ChatActivity extends FragmentActivity {
    private static final int REQUEST_SENDBIRD_CHAT_ACTIVITY = 100;
    private static final int REQUEST_SENDBIRD_CHANNEL_LIST_ACTIVITY = 101;
    private static final int REQUEST_SENDBIRD_MESSAGING_ACTIVITY = 200;
    private static final int REQUEST_SENDBIRD_MESSAGING_CHANNEL_LIST_ACTIVITY = 201;
    private static final int REQUEST_SENDBIRD_USER_LIST_ACTIVITY = 300;

    public static String VERSION = "2.0.9.1";

    /**
     To test push notifications with your own appId, you should replace google-services.json with yours.
     Also you need to set Server API Token and Sender ID in SendBird dashboard.
     Please carefully read "Push notifications" section in SendBird Android documentation
     */
    final String appId = "65502478-E77E-41D0-A636-4D5DEE969EEA"; /* Sample SendBird Application */
    String userId = SendBirdChatActivity.Helper.generateDeviceUUID(ChatActivity.this); /* Generate Device UUID */
    String userName = "최귷"; /* Generate User Nickname  + userId.substring(0, 5)*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting);

        /**
         * Start GCM Service.
         */
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);


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
        });*/
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

        startMessagingChannelList();
        findViewById(R.id.btn_select_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserList();
            }
        });

        findViewById(R.id.btn_start_messaging_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMessagingChannelList();
            }
        });

    }

    private void startChat(String channelUrl) {
        Intent intent = new Intent(ChatActivity.this, SendBirdChatActivity.class);
        Bundle args = SendBirdChatActivity.makeSendBirdArgs(appId, userId, userName, channelUrl);

        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_CHAT_ACTIVITY);
    }

    private void startUserList() {
        Intent intent = new Intent(ChatActivity.this, SendBirdUserListActivity.class);
        Bundle args = SendBirdUserListActivity.makeSendBirdArgs(appId, userId, userName);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_USER_LIST_ACTIVITY);
    }

    private void startMessaging(String [] targetUserIds) {
        Intent intent = new Intent(ChatActivity.this, SendBirdMessagingActivity.class);
        Bundle args = SendBirdMessagingActivity.makeMessagingStartArgs(appId, userId, userName, targetUserIds);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_ACTIVITY);
    }

    private void joinMessaging(String channelUrl) {
        Intent intent = new Intent(ChatActivity.this, SendBirdMessagingActivity.class);
        Bundle args = SendBirdMessagingActivity.makeMessagingJoinArgs(appId, userId, userName, channelUrl);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_ACTIVITY);
    }

    private void startMessagingChannelList() {
        Intent intent = new Intent(ChatActivity.this, SendBirdMessagingChannelListActivity.class);
        Bundle args = SendBirdMessagingChannelListActivity.makeSendBirdArgs(appId, userId, userName);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_CHANNEL_LIST_ACTIVITY);
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