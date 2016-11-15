package com.example.test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Илья on 14.05.2016.
 */ class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION  = 1;
    public static final String DATABASE_NAME = "MainDB";
    public static final String TABLE_DATES = "Dates";
    public static final String TABLE_EVENTS = "Events";

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date_id";
    public static final String KEY_EVENT_ID = "event_id";
    public static final String KEY_EVENT_REPEAT_ID = "event_repeat_id";

    public static final String KEY_EVENT_NAME = "evet_name";
    public static final String KEY_EVENT_TIME_BOUNDS_1 = "event_time_bounds_1";
    public static final String KEY_EVENT_TIME_BOUNDS_2 = "event_time_bounds_2";


    List<List<String>> getDailyEvents(String date){
        SQLiteDatabase db = getWritableDatabase();
        String selection = DBHelper.KEY_DATE +" = ?";
        String[] selectionArgs = {date};
        List<List<String>> res = new ArrayList<List<String>>();
        Cursor cursor = db.query(DBHelper.TABLE_DATES, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int eventIDIndex = cursor.getColumnIndex(DBHelper.KEY_EVENT_ID);
            do {
                String eventID = cursor.getString(eventIDIndex);
                String selection2 = DBHelper.KEY_EVENT_ID +" = ?";
                String[] selectionArgs2 = {eventID};
                Cursor cursor2 = db.query(DBHelper.TABLE_EVENTS, null, selection2, selectionArgs2, null, null, null);
                if (cursor2.moveToFirst()) {
                    int eventName = cursor2.getColumnIndex(DBHelper.KEY_EVENT_NAME);
                    int eventTBFirstLine = cursor2.getColumnIndex(DBHelper.KEY_EVENT_TIME_BOUNDS_1);
                    int eventTBSecondLine = cursor2.getColumnIndex(DBHelper.KEY_EVENT_TIME_BOUNDS_2);
                    do {
                        String name = cursor2.getString(eventName);
                        String firstLine = cursor2.getString(eventTBFirstLine);
                        String secondLine = cursor2.getString(eventTBSecondLine);
                        List<String> additional = new ArrayList<String>();
                        additional.add(name);
                        additional.add(firstLine);
                        additional.add(secondLine);
                        res.add(additional);
                    } while (cursor2.moveToNext());

                    cursor2.close();
                }
            } while (cursor.moveToNext());

            cursor.close();
        }
        return res;
    }

    public DBHelper(Context context) {
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_DATES + "(" + KEY_ID
                + " integer primary key," + KEY_DATE + " text," + KEY_EVENT_ID + " text," + KEY_EVENT_REPEAT_ID + " text" + ")");

        db.execSQL("create table " + TABLE_EVENTS + "(" + KEY_ID
                + " integer primary key," + KEY_EVENT_ID + " text," + KEY_EVENT_REPEAT_ID + " text,"
                + KEY_EVENT_NAME + " text," + KEY_EVENT_TIME_BOUNDS_1 + " text,"+  KEY_EVENT_TIME_BOUNDS_2 + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}


