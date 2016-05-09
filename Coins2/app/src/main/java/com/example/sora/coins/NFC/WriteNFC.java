package com.example.sora.coins.NFC;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class WriteNFC extends ActionBarActivity
{
//    private NfcAdapter adapter;
//    private PendingIntent pendingIntent;
//
//    public static final int TYPE_TEXT = 1;
//    public static final int TYPE_URI = 2;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//
//        /*if (savedInstanceState == null)
//        {
//            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
//        }*/
//
//        adapter = NfcAdapter.getDefaultAdapter(this);
//        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//    }
//@Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        if (intent != null) {
//            processTag(intent); // processTag �޼ҵ� ȣ��
//        }
//    }
//
//    // onNewIntent �޼ҵ� ���� �� ȣ��Ǵ� �޼ҵ�
//    private void processTag(Intent intent) {
//        // EditText�� �Էµ� ���� ������
//        String s = writeText.getText().toString();
//
//        // ������ �±׸� ����Ű�� ��ü
//        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//        // �ƹ��͵� �Է¹��� ������ �±׿� ���� ����
//        if (s.equals("")) {
//            Toast.makeText(getApplicationContext(), "������ �Է����ּ���.", 1000).show();
//        }
//
//        // �Է¹��� ���� ������ �±׿� ��
//        else {
//            NdefMessage message = createTagMessage(s, TYPE_TEXT);
//            writeTag(message, detectedTag);
//        }
//    }
//
//    // ������ �±׿� NdefMessage�� ���� �޼ҵ�
//    public boolean writeTag(NdefMessage message, Tag tag) {
//        int size = message.toByteArray().length;
//        try {
//            Ndef ndef = Ndef.get(tag);
//            if (ndef != null) {
//                ndef.connect();
//                if (!ndef.isWritable()) {
//                    return false;
//                }
//
//                if (ndef.getMaxSize() < size) {
//                    return false;
//                }
//
//                ndef.writeNdefMessage(message);
//                Toast.makeText(getApplicationContext(), "���� ����!", 1000).show();
//            } else {
//                Toast.makeText(this, "���˵��� ���� �±��̹Ƿ� ���� �����ϰ� �����͸� ���ϴ�.",
//                        Toast.LENGTH_SHORT).show();
//
//                NdefFormatable formatable = NdefFormatable.get(tag);
//                if (formatable != null) {
//                    try {
//                        formatable.connect();
//                        formatable.format(message);
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//
//                return false;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//
//            return false;
//        }
//
//        return true;
//    }
//
//    /**
//     * Create a new tag message
//     *
//     * @param msg
//     * @param type
//     * @return
//     */
//    private NdefMessage createTagMessage(String msg, int type) {
//        NdefRecord[] records = new NdefRecord[1];
//
//        if (type == TYPE_TEXT) {
//            records[0] = createTextRecord(msg, Locale.KOREAN, true);
//        } else if (type == TYPE_URI) {
//            records[0] = createUriRecord(msg.getBytes());
//        }
//
//        NdefMessage mMessage = new NdefMessage(records);
//
//        return mMessage;
//    }
//
//    private NdefRecord createTextRecord(String text, Locale locale,
//                                        boolean encodeInUtf8) {
//        final byte[] langBytes = locale.getLanguage().getBytes(
//                Charsets.US_ASCII);
//        final Charset utfEncoding = encodeInUtf8 ? Charsets.UTF_8 : Charset
//                .forName("UTF-16");
//        final byte[] textBytes = text.getBytes(utfEncoding);
//        final int utfBit = encodeInUtf8 ? 0 : (1 << 7);
//        final char status = (char) (utfBit + langBytes.length);
//        final byte[] data = Bytes.concat(new byte[] { (byte) status },
//                langBytes, textBytes);
//        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
//                new byte[0], data);
//    }
//
//    private NdefRecord createUriRecord(byte[] data) {
//        return new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, NdefRecord.RTD_URI,
//                new byte[0], data);
//    }

}
