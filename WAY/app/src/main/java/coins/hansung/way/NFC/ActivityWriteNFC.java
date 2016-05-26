package coins.hansung.way.NFC;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-05-08.
 */

public class ActivityWriteNFC extends AppCompatActivity
{
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;

    EditText writeText;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_nfc);

        writeText = (EditText) findViewById(R.id.nfcEditText);
        adapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        if (adapter != null)
        {
            adapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }


    @Override
    protected void onPause()
    {
        if (adapter != null)
        {
            adapter.disableForegroundDispatch(this);
        }

        super.onPause();
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        if (intent != null)
        {
            processTag(intent);
        }
    }


    private void processTag(Intent intent)
    {
        String s = writeText.getText().toString();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (s.equals(""))
        {
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }

        else
        {
            NdefMessage message = createTagMessage(s, TYPE_TEXT);
            writeTag(message, tag);
        }
    }


    private NdefMessage createTagMessage(String message, int type)
    {
        NdefRecord records[] = new NdefRecord[1];

        if (type == TYPE_TEXT)
        {
            records[0] = createTextRecord(message, Locale.KOREAN, true);
        }

        else if (type == TYPE_URI)
        {
            records[0] = createUriRecord(message.getBytes());
        }

        NdefMessage ndefMessage = new NdefMessage(records);
        return ndefMessage;
    }


    private NdefRecord createTextRecord(String text, Locale locale, boolean encodeInUtf8)
    {
        final byte languageBytes[] = locale.getLanguage().getBytes(Charsets.US_ASCII);
        final Charset utfEncoding = encodeInUtf8 ? Charsets.UTF_8 : Charset.forName("UTF-16");
        final byte textBytes[] = text.getBytes(utfEncoding);
        final int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        final char status = (char) (utfBit + languageBytes.length);
        final byte data[] = Bytes.concat(new byte[]{(byte) status}, languageBytes, textBytes);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }


    private NdefRecord createUriRecord(byte data[])
    {
        return new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, NdefRecord.RTD_URI, new byte[0], data);
    }


    public boolean writeTag(NdefMessage message, Tag tag)
    {
        int size = message.toByteArray().length;

        try
        {
            Ndef ndef = Ndef.get(tag);

            if (ndef != null)
            {
                ndef.connect();

                if (!ndef.isWritable())
                {
                    return false;
                }

                if (ndef.getMaxSize() < size)
                {
                    return false;
                }

                ndef.writeNdefMessage(message);
                Toast.makeText(getApplicationContext(), "쓰기 성공", Toast.LENGTH_SHORT).show();
            }

            else
            {
                Toast.makeText(getApplicationContext(), "포맷되지 않은 태그", Toast.LENGTH_SHORT).show();

                NdefFormatable formatable = NdefFormatable.get(tag);

                if (formatable != null)
                {
                    try
                    {
                        formatable.connect();
                        formatable.format(message);
                    }

                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                }

                return false;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}