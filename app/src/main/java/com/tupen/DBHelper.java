package com.tupen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "tupen.db";
    static final int DB_VERSION = 1;

    static final String DB_TABLE = "notes";

    static final String NOTE_ID = "id";
    static final String NOTE_TITLE = "title";
    static final String NOTE_BODY = "body";
    static final String NOTE_IMAGE1 = "image1";
    static final String NOTE_IMAGE2 = "image2";
    static final String NOTE_AUDIO = "audio";
    static final String NOTE_DATE = "_date";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + DB_TABLE + " (" +
                NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTE_TITLE + " TEXT, " +
                NOTE_BODY + " TEXT, " +
                NOTE_IMAGE1 + " TEXT, " +
                NOTE_IMAGE2 + " TEXT, " +
                NOTE_AUDIO + " TEXT, " +
                NOTE_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }
}
