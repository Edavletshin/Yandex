package com.edavletshin.yandextranslator.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by edgar on 15.03.2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DataEntry.TABLE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + DataEntry.TABLE_NAME + " ("
                + DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataEntry.COLUMN_WORD + " TEXT NOT NULL, "
                + DataEntry.COLUMN_TRANSLATE + " TEXT NOT NULL, "
                + DataEntry.COLUMN_IS_ELECTED + " INTEGER NOT NULL DEFAULT 0, "
                + DataEntry.COLUMN_LANG_PAIR + " TEXT NOT NULL);";

        // Запускаем создание таблицы
        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void insertTranslate(String word, String translate, String pair) {
        ContentValues map = new ContentValues();
        map.put(DataEntry.COLUMN_WORD, word);
        map.put(DataEntry.COLUMN_TRANSLATE, translate);
        map.put(DataEntry.COLUMN_IS_ELECTED, DataEntry.ELECTED_FALSE);
        map.put(DataEntry.COLUMN_LANG_PAIR, pair);

        try {
            getWritableDatabase().insert(DataEntry.TABLE_NAME, null, map);
        }
        catch (SQLiteException e) {
            Log.e("db", e.toString());
        }
    }

    public void update(String id,String isElected){
        ContentValues values = new ContentValues();
        if (isElected.equals("0")) {
            values.put(DataEntry.COLUMN_IS_ELECTED, DataEntry.ELECTED_TRUE);
        } else {
            values.put(DataEntry.COLUMN_IS_ELECTED, DataEntry.ELECTED_FALSE);
        }
        try {
            getWritableDatabase().update(DataEntry.TABLE_NAME,
                    values,
                    DataEntry._ID + "= ?", new String[]{id});
        } catch (SQLiteException e) {
            Log.e("db", e.toString());
        }
    }

    public Cursor fetchHistory(boolean onlyElected) {

        Cursor cursor;

        if (onlyElected) {
             cursor = getReadableDatabase().query(DataEntry.TABLE_NAME,
                    new String[]{DataEntry._ID, DataEntry.COLUMN_WORD, DataEntry.COLUMN_TRANSLATE, DataEntry.COLUMN_IS_ELECTED, DataEntry.COLUMN_LANG_PAIR},
                    DataEntry.COLUMN_IS_ELECTED + "= ?",
                    new String[]{"1"},
                    null, null, null);
        } else {
            cursor = getReadableDatabase().query(DataEntry.TABLE_NAME,
                    new String[]{DataEntry._ID, DataEntry.COLUMN_WORD, DataEntry.COLUMN_TRANSLATE, DataEntry.COLUMN_IS_ELECTED, DataEntry.COLUMN_LANG_PAIR},
                    null, null, null, null, null);
        }

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public void clearDB(){
        getWritableDatabase().delete(DataEntry.TABLE_NAME, null,null);
    }

}
