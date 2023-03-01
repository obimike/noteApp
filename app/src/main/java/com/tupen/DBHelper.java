package com.tupen;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addNote(String title, String body, String imagePath, String imagePath2, String audioPath, String date){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_TITLE, title);
        contentValues.put(NOTE_BODY, body);
        contentValues.put(NOTE_IMAGE1, imagePath);
        contentValues.put(NOTE_IMAGE2, imagePath2);
        contentValues.put(NOTE_AUDIO, audioPath);
        contentValues.put(NOTE_DATE, date);

        long res = db.insert(DBHelper.DB_TABLE, null, contentValues);
        db.close();

        return res;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                DBHelper.DB_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            @SuppressLint("Range")int id = cursor.getInt(cursor.getColumnIndex(DBHelper.NOTE_ID));
            @SuppressLint("Range")String title = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_TITLE));
            @SuppressLint("Range")String body = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_BODY));
            @SuppressLint("Range")String imagePath = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_IMAGE1));
            @SuppressLint("Range")String imagePath2 = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_IMAGE2));
            @SuppressLint("Range")String audioPath = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_AUDIO));
            @SuppressLint("Range")String date = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_DATE));

            notes.add(new Note(id, title, body, imagePath, imagePath2, audioPath, date));

        }
        cursor.close();
        db.close();
        return notes;
    }

}
