package com.example.sora.coins.NFC;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Administrator on 2016-05-08.
 */

public class ReadNFC extends AppCompatActivity
{
    TextView textView;
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter intentFilter[];
    String techLists[][];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }
}