package com.example.sora.coins.NFC;

import android.nfc.NdefRecord;

import com.google.common.base.Preconditions;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by Administrator on 2016-05-13.
 */

public class TextRecord implements ParsingRecord
{
    private final String languageCode;
    private final String text;

    private TextRecord(String languageCode, String text)
    {
        this.languageCode = Preconditions.checkNotNull(languageCode);
        this.text = Preconditions.checkNotNull(text);
    }

    public int getType()
    {
        return ParsingRecord.TYPE_TEXT;
    }

    public String getText()
    {
        return text;
    }

    public String getLanguageCode()
    {
        return languageCode;
    }

    public static TextRecord parse(NdefRecord record)
    {
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));

        try
        {
            byte payload[] = record.getPayload();
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0077;
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

            return new TextRecord(languageCode, text);
        }

        catch (UnsupportedEncodingException ue)
        {
            throw new IllegalArgumentException(ue);
        }
    }

    public static boolean isText(NdefRecord record)
    {
        try
        {
            parse(record);
            return true;
        }

        catch (IllegalArgumentException ie)
        {
            return false;
        }
    }
}
