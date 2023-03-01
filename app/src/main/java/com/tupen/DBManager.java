package com.tupen;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        this.context = context;
    }

    public DBManager open() throws SQLDataException{
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        return  this;
    }

    public void close(){
        dbHelper.close();
    }

    public void addNote(String title, String body, String imagePath, String imagePath2, String audioPath, String date){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NOTE_TITLE, title);
        contentValues.put(DBHelper.NOTE_BODY, body);
        contentValues.put(DBHelper.NOTE_IMAGE1, imagePath);
        contentValues.put(DBHelper.NOTE_IMAGE2, imagePath2);
        contentValues.put(DBHelper.NOTE_AUDIO, audioPath);
        contentValues.put(DBHelper.NOTE_DATE, date);

        db.insert(DBHelper.DB_TABLE, null, contentValues);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        dbHelper.getReadableDatabase();
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
        close();
        return notes;
    }
}
