package com.example.sora.coins;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by sora on 2016-04-14.
 */
public class ListData
{
    Drawable icon;
    String name;
    String loca;

    public static final Comparator<ListData> comparator = new Comparator<ListData>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(ListData listData1, ListData listData2) {
            return collator.compare(listData1.name, listData2.name);
        }
    };
}