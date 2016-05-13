package com.example.sora.coins.NFC;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-05-13.
 */
public class NdefMessageParsing
{
    public static List <ParsingRecord> parse(NdefMessage message)
    {
        return getRecords(message.getRecords());
    }

    public static List <ParsingRecord> getRecords(NdefRecord records[])
    {
        List <ParsingRecord> elements = new ArrayList <ParsingRecord>();

        for (NdefRecord record : records)
        {
            if (TextRecord.isText(record))
            {
                elements.add(TextRecord.parse(record));
            }
        }

        return elements;
    }
}
