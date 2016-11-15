package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by Илья on 02.06.2016.
 */
public class BackgroundManager extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        Context activity = (Context) params[0];
        String query = (String) params[1];
        DBHelper dbHelper = new DBHelper(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DBHelper.KEY_DATE + " = ?";
        String[] selectionArgs = {query};
        Cursor c = db.query(DBHelper.TABLE_DATES, null, selection, selectionArgs, null, null, null);
        int numOfEvents = c.getCount();
        c.close();
        db.close();
        return numOfEvents;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

}
