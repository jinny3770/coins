package com.example.sora.coins;

import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;


public class ChatActivity extends AppCompatActivity
{
    private final String TAG = this.getClass().getSimpleName();
    private final static String TAB_TAG_CHAT = "tChat";
    private final static String TAB_TAG_USERS = "tUsers";

    private final static boolean VERBOSE_MODE = true;

    private final static int COLOR_GREEN = Color.parseColor("#99FF99");
    private final static int COLOR_BLUE = Color.parseColor("#99CCFF");
    private final static int COLOR_GRAY = Color.parseColor("#cccccc");
    private final static int COLOR_RED = Color.parseColor("#FF0000");
    private final static int COLOR_ORANGE = Color.parseColor("#f4aa0b");


    ActionBar actionBar;


    EditText inputChatMessage;
    View  buttonChatSend, layoutConnector, layoutLogin, layoutChat;
    TextView labelStatus, labelTagUsers;
    ListView listUsers, listMessages;
    ArrayAdapter<String> adapterUsers;
    MessagesAdapter adapterMessages;
    TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFF8080));
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);

        initUI();
    }


    private void initUI() {

        buttonChatSend = (Button) findViewById(R.id.button_chat_send);
        listMessages = (ListView) findViewById(R.id.list_chat);
        inputChatMessage = (EditText) findViewById(R.id.input_chat_message);
        final ArrayList<String> message2 = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, message2);
        listMessages.setAdapter(adapter);
        buttonChatSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String message = inputChatMessage.getText().toString();
                if (message.length() > 0) {
                    message2.add("나 : "+message);
                    adapter.notifyDataSetChanged();
                    inputChatMessage.setText("");
                }
            }
        });

        // The list of users
       /* adapterUsers = new ArrayAdapter<String>(this, R.layout.row_user);
        listUsers.setAdapter(adapterUsers);
        adapterMessages = new MessagesAdapter(this);
        listMessages.setAdapter(adapterMessages);
        // Enable auto scroll
        listMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listMessages.setStackFromBottom(true);*/

        //showLayout(layoutConnector);
    }



    /**
     * Frees the resources.
     */
    private void showLayout(final View layoutToShow) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // Show the layout selected and hide the others
                for (View layout : new View[]{layoutChat, layoutConnector, layoutLogin}) {
                    if (layoutToShow == layout) {
                        layout.setVisibility(View.VISIBLE);
                    } else {
                        layout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /*
    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.fade, R.anim.hold);
        super.onBackPressed();
    }
    */



}
