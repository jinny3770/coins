package coins.hansung.way.Main;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by sora on 2016-04-14.
 */
public class FamilyListData {
    Drawable icon;
    String name;
    String loca;
    String gps;
    int battery;
    Bitmap profileImage;

    public static final Comparator<FamilyListData> comparator = new Comparator<FamilyListData>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(FamilyListData listData1, FamilyListData listData2) {
            return collator.compare(listData1.name, listData2.name);
        }
    };
}