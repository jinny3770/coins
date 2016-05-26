package coins.hansung.way.NFC;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import java.util.List;

import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-05-08.
 */

public class ReadNFC extends AppCompatActivity
{
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter intentFilters[];
    String techLists[][];

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;
    public static final String CHARS = "0123456789ABCDEF";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = NfcAdapter.getDefaultAdapter(this);
        Intent targetIntent = new Intent(this, ReadNFC.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        pendingIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try
        {
            ndefFilter.addDataType("*/*");
        }

        catch (IntentFilter.MalformedMimeTypeException e)
        {
            throw new RuntimeException("fail", e);
        }

        intentFilters = new IntentFilter[] {ndefFilter, };
        techLists = new String[][] {new String[] {NfcF.class.getName()}};
        Intent passedIntent = getIntent();

        if (passedIntent != null)
        {
            String actionString = passedIntent.getAction();

            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(actionString))
            {
                processTag(passedIntent);
            }
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        if (adapter != null)
        {
            adapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);
        }
    }


    @Override
    protected void onPause()
    {
        super.onPause();

        if (adapter != null)
        {
            adapter.disableForegroundDispatch(this);
        }
    }


    @Override
    protected void onNewIntent(Intent passedIntent)
    {
        super.onNewIntent(passedIntent);

        Tag tag = passedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null)
        {
            byte tagID[] = tag.getId();
            Log.e("태그 ID : ", toHexString(tagID) + "\n");
        }

        if (passedIntent != null)
        {
            processTag(passedIntent);
        }
    }


    private void processTag(Intent passedIntent)
    {
        Parcelable rawMessage[] = passedIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMessage == null)
        {
            return;
        }

        Toast.makeText(getApplicationContext(), "스캔 성공", Toast.LENGTH_SHORT).show();
        Log.i("info", "rawMessage.length : " + rawMessage.length);

        NdefMessage message[];

        if (rawMessage != null)
        {
            message = new NdefMessage[rawMessage.length];

            for (int i = 0; i < rawMessage.length; i++)
            {
                message[i] = (NdefMessage) rawMessage[i];
                showTag(message[i]);
            }
        }
    }


    public static String toHexString(byte data[])
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < data.length; ++i)
        {
            stringBuilder.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(CHARS.charAt(data[i] & 0x0F));
        }

        return stringBuilder.toString();
    }


    private int showTag(NdefMessage message)
    {
        List<ParsingRecord> records = NdefMessageParsing.parse(message);
        final int size = records.size();

        for (int i = 0; i < size; i++)
        {
            ParsingRecord record = records.get(i);

            int recordType = record.getType();
            String recordString = "";

            if (recordType == ParsingRecord.TYPE_TEXT)
            {
                recordString = "TEXT : " + ((TextRecord) record).getText();
            }

            Log.e(recordString, "\n");
        }

        return size;
    }
}