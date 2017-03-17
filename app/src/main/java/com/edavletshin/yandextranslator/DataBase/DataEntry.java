package com.edavletshin.yandextranslator.DataBase;

import android.provider.BaseColumns;

/**
 * Created by edgar on 15.03.2017.
 */

public class DataEntry implements BaseColumns {

    public final static String TABLE_NAME = "translations";

    public final static String _ID = BaseColumns._ID;
    public final static String COLUMN_WORD = "word";
    public final static String COLUMN_TRANSLATE = "translate";
    public final static String COLUMN_IS_ELECTED = "elected";
    public final static String COLUMN_LANG_PAIR = "pair";

    public static final int ELECTED_TRUE = 1;
    public static final int ELECTED_FALSE = 0;

}
