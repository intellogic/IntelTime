package com.example.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Илья on 31.05.2016.
 */
public class SimpleEvent extends Event{
    private MyDateTime start;
    private MyDateTime finish;
    public boolean tillTheEnd;
    public boolean fromTheStart;

    SimpleEvent(String n, GregorianCalendar sT, GregorianCalendar fT, boolean allDay, String id, String repID){
        super(n, allDay, repID);
        start = new MyDateTime(sT.get(GregorianCalendar.YEAR), sT.get(GregorianCalendar.MONTH), sT.get(GregorianCalendar.DAY_OF_MONTH),
                sT.get(GregorianCalendar.DAY_OF_WEEK), sT.get(GregorianCalendar.HOUR_OF_DAY), sT.get(GregorianCalendar.MINUTE));

        finish = new MyDateTime(fT.get(GregorianCalendar.YEAR), fT.get(GregorianCalendar.MONTH), fT.get(GregorianCalendar.DAY_OF_MONTH),
                fT.get(GregorianCalendar.DAY_OF_WEEK), fT.get(GregorianCalendar.HOUR_OF_DAY), fT.get(GregorianCalendar.MINUTE));

        if (id.length() == 0)
            ID = UUID.randomUUID().toString();
        else
            ID = id;
    }

    String getFirstLine(){
        return start.getDate();
    }

    String getSecondLine(){
        if (isAllDay)
            return "All day";
        return start.getTime() + " - " + finish.getTime();
    }

    @Override
    public void print(){
        String repeat;
        if (isRepeat)
            repeat = " REPEAT WITH ID : " + repeatID;
        else
            repeat = "NO REPEAT";
        if (!isAllDay){
            if (!tillTheEnd && !fromTheStart)
                Log.d("SIMPLE EVENT", "Name : " + name + " Date: " + start.getDate() + " sTime: " + start.getTime() +
                        " fTime: "+ finish.getTime() + " ID = " + ID + repeat);
            else
                if (tillTheEnd){
                    Log.d("SIMPLE EVENT", "Name : " + name +" Date: " + start.getDate() + " sTime: " + start.getTime() +
                            " fTime: tilltheend"+ " ID = " + ID + repeat);
                }
            else
                if (fromTheStart){
                    Log.d("SIMPLE EVENT", "Name : " + name + " Date: " + start.getDate() + " sTime: fromthestart " +
                            " fTime: "+ finish.getTime() + " ID = " + ID + repeat);
                }

        }
        else{
            Log.d("SIMPLE EVENT", "Name : " + name + " Date: " + start.getDate() + " ALL DAY " + "ID = " + ID + repeat);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void createNotification(Activity activity, GregorianCalendar nt, boolean isToday, boolean isRightNow){
        AlarmManager alarmMgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);


        intent.putExtra("name", name);
        String text;
        DateTime dt = new DateTime(nt.get(GregorianCalendar.YEAR),nt.get(GregorianCalendar.MONTH) + 1, nt.get(GregorianCalendar.DAY_OF_MONTH),
                nt.get(GregorianCalendar.HOUR_OF_DAY), nt.get(GregorianCalendar.MINUTE));
        if (isRightNow)
            text = "Event starts right now. Are you ready?";
        else if (isToday)
            text = "Today from " + start.getTime() + " to "  + finish.getTime() + ". Are you ready?";
        else
           text = start.getDate() + " from " + start.getTime() + " to "  + finish.getTime() + ". Are you ready?";
        Log.d("NOTIFICATION", text);
        DateTime now = DateTime.now();
        int id = (int) ((Math.random()) % 10000);
        long t = Math.abs(now.getMillis() - dt.getMillis());
        intent.putExtra("text", text);
        intent.setAction(UUID.randomUUID().toString());
        Log.d("COMPARING", ((Long)t).toString());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(activity, id, intent, 0);
        // set for 30 seconds later
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + t, alarmIntent);

    }

    @Override
    public void addToDB(Context context, boolean add) {

        Log.d("YES", "NI");

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_DATE, start.getKeyString());
        contentValues.put(DBHelper.KEY_EVENT_ID, ID);
        contentValues.put(DBHelper.KEY_EVENT_REPEAT_ID, repeatID);
        database.insert(DBHelper.TABLE_DATES, null, contentValues);

        Cursor cursor = database.query(DBHelper.TABLE_DATES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
            int eventIDIndex = cursor.getColumnIndex(DBHelper.KEY_EVENT_ID);
            int eventRepeatIDIndex = cursor.getColumnIndex(DBHelper.KEY_EVENT_REPEAT_ID);
            do {
                Log.d("TABLE DATES", "ID = " + cursor.getInt(idIndex) +
                        ", date = " + cursor.getString(dateIndex) +
                        ", id = " + cursor.getString(eventIDIndex) +
                        ", repeatid = " + cursor.getString(eventRepeatIDIndex));
            } while (cursor.moveToNext());

        } else
            Log.d("mLog","0 rows");
        cursor.close();

        if (add) {
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put(DBHelper.KEY_EVENT_ID, ID);
            contentValues2.put(DBHelper.KEY_EVENT_REPEAT_ID, repeatID);
            contentValues2.put(DBHelper.KEY_EVENT_NAME, name);
            contentValues2.put(DBHelper.KEY_EVENT_TIME_BOUNDS_1, getFirstLine());
            contentValues2.put(DBHelper.KEY_EVENT_TIME_BOUNDS_2, getSecondLine());
            database.insert(DBHelper.TABLE_EVENTS, null, contentValues2);

            Cursor cursor2 = database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null);

            if (cursor2.moveToFirst()) {
                int idIndex = cursor2.getColumnIndex(DBHelper.KEY_ID);
                int eventIDIndex = cursor2.getColumnIndex(DBHelper.KEY_EVENT_ID);
                int eventRepeatIDIndex = cursor2.getColumnIndex(DBHelper.KEY_EVENT_REPEAT_ID);
                int eventName = cursor2.getColumnIndex(DBHelper.KEY_EVENT_NAME);
                int eventTB1 = cursor2.getColumnIndex(DBHelper.KEY_EVENT_TIME_BOUNDS_1);
                int eventTB2 = cursor2.getColumnIndex(DBHelper.KEY_EVENT_TIME_BOUNDS_2);

                do {
                    Log.d("mLog", "ID = " + cursor2.getInt(idIndex) +
                            ", id = " + cursor2.getString(eventIDIndex) +
                            ", repeatid = " + cursor2.getString(eventRepeatIDIndex) +
                            " eventName = " + cursor2.getString(eventName) +
                            " eventTB = " + cursor2.getString(eventTB1) + cursor2.getString(eventTB2)
                    );
                } while (cursor2.moveToNext());

            } else
                Log.d("mLog", "0 rows");

            cursor2.close();
        }

        dbHelper.close();

    }

}
