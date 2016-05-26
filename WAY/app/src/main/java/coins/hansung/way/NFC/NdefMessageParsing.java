package coins.hansung.way.NFC;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sora on 2016-05-21.
 */
public class NdefMessageParsing {
    public static List<ParsingRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static List<ParsingRecord> getRecords(NdefRecord records[]) {
        List<ParsingRecord> elements = new ArrayList<ParsingRecord>();

        for (NdefRecord record : records) {
            if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            }
        }

        return elements;
    }
}
