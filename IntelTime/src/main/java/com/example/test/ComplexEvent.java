package com.example.test;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by Илья on 01.06.2016.
 */
public class ComplexEvent extends Event{
    private GregorianCalendar start;
    private GregorianCalendar finish;


    ComplexEvent(String n, GregorianCalendar sT, GregorianCalendar fT, boolean allDay, String repID){
        super(n, allDay, repID);
        start = (GregorianCalendar) sT.clone();
        finish = (GregorianCalendar) fT.clone();
        Log.d("START TIME", ((Integer)start.get(GregorianCalendar.HOUR)).toString());
        ID = UUID.randomUUID().toString();

    }
    String getFirstLine(){
        MyDateTime dt = new MyDateTime(start.get(GregorianCalendar.YEAR), start.get(GregorianCalendar.MONTH), start.get(GregorianCalendar.DAY_OF_MONTH),
                start.get(GregorianCalendar.DAY_OF_WEEK),  start.get(GregorianCalendar.HOUR_OF_DAY),  start.get(GregorianCalendar.MINUTE));
        if (isAllDay)
            return dt.getDate();
        return dt.getDate() + ", " + dt.getTime();
    }

    String getSecondLine(){
        MyDateTime dt = new MyDateTime(finish.get(GregorianCalendar.YEAR), finish.get(GregorianCalendar.MONTH), finish.get(GregorianCalendar.DAY_OF_MONTH),
                finish.get(GregorianCalendar.DAY_OF_WEEK),  finish.get(GregorianCalendar.HOUR_OF_DAY),  finish.get(GregorianCalendar.MINUTE));
        if (isAllDay)
            return dt.getDate();
        return dt.getDate() + ", " + dt.getTime();
    }

    @Override
    public void createNotification(Activity activity, GregorianCalendar nt, boolean isToday, boolean isRightNow){
        Log.d("WE CREATE", "NOTIFICATION");
        DateTime dt = new DateTime(nt.get(GregorianCalendar.YEAR),nt.get(GregorianCalendar.MONTH) + 1,nt.get(GregorianCalendar.DAY_OF_MONTH),
                nt.get(GregorianCalendar.HOUR_OF_DAY), nt.get(GregorianCalendar.MINUTE));
        AlarmManager alarmMgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra("name", name);
        String text;
        if (isRightNow)
            text = "Event starts right now. Are you ready?";
        else if (isToday)
           text = "Today from " + start.getTime().toString() + " to "  + getSecondLine() + ". Are you ready?";
        else
            text = getFirstLine() + " - " + getSecondLine() + ". Are you ready?";
        Log.d("NOTIFICATION", text);
        DateTime now = DateTime.now();
        int id = (int) ((Math.random()) % 10000);
        long t = Math.abs(now.getMillis() - dt.getMillis());
        intent.putExtra("text", text);
        intent.setAction(UUID.randomUUID().toString());
        Log.d("COMPARING", ((Long)t).toString());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(activity, id, intent, 0);
        // set for 30 seconds later
        alarmMgr.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + t, alarmIntent);

    }


    @Override
    public void addToDB(Context context, boolean add){
        List<SimpleEvent> res = split();
        for (int i = 0; i  < res.size(); i++)
            res.get(i).addToDB(context, false);

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_EVENT_ID, ID);
        contentValues.put(DBHelper.KEY_EVENT_REPEAT_ID, repeatID);
        contentValues.put(DBHelper.KEY_EVENT_NAME, name);
        contentValues.put(DBHelper.KEY_EVENT_TIME_BOUNDS_1, getFirstLine());
        contentValues.put(DBHelper.KEY_EVENT_TIME_BOUNDS_2, getSecondLine());


        database.insert(DBHelper.TABLE_EVENTS, null, contentValues);

        Cursor cursor = database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int eventIDIndex = cursor.getColumnIndex(DBHelper.KEY_EVENT_ID);
            int eventRepeatIDIndex = cursor.getColumnIndex(DBHelper.KEY_EVENT_REPEAT_ID);
            int eventName = cursor.getColumnIndex(DBHelper.KEY_EVENT_NAME);
            int eventTB1 = cursor.getColumnIndex(DBHelper.KEY_EVENT_TIME_BOUNDS_1);
            int eventTB2 = cursor.getColumnIndex(DBHelper.KEY_EVENT_TIME_BOUNDS_2);

            do {
                Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                        ", id = " + cursor.getString(eventIDIndex) +
                        ", repeatid = " + cursor.getString(eventRepeatIDIndex)+
                        " eventName = " + cursor.getString(eventName) +
                        " eventTB = " + cursor.getString(eventTB1) + " - " + cursor.getString(eventTB2)
                );
            } while (cursor.moveToNext());

        } else
            Log.d("mLog","0 rows");

        cursor.close();


        dbHelper.close();
    }

    @Override
    public void print(){
        List<SimpleEvent> res = split();
        for (int i = 0; i  < res.size(); i++)
            res.get(i).print();
    }
    List <SimpleEvent> split(){
        List<SimpleEvent> res  = new ArrayList<SimpleEvent>();
        GregorianCalendar current = (GregorianCalendar) start.clone();
        res.add(new SimpleEvent(name, start, start, isAllDay, ID,repeatID));
        res.get(0).tillTheEnd = true;
        current.add(GregorianCalendar.DATE, 1);
        while (current.get(GregorianCalendar.DAY_OF_YEAR) != finish.get(GregorianCalendar.DAY_OF_YEAR) || current.get(GregorianCalendar.YEAR) != finish.get(GregorianCalendar.YEAR)){
            res.add(new SimpleEvent(name, current, current, true, ID, repeatID));
            current.add(GregorianCalendar.DAY_OF_YEAR, 1);
            Log.d("SPLIT", "Complex event");
        }
        res.add(new SimpleEvent(name, finish, finish, isAllDay, ID, repeatID));
        res.get(res.size() - 1).fromTheStart = true;

        return res;
    }
}
