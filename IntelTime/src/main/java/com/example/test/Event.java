package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

public class Event {
    protected String name;
    protected String ID;
    protected boolean isAllDay;
    protected String repeatID;
    protected boolean isRepeat;

    public Event(String n, boolean isAD,  String repID){
        name = n;

        isAllDay = isAD;
        repeatID = repID;
        if (repeatID.equals(""))
            isRepeat = false;
        else
            isRepeat = true;
    }

    public void print(){}

    public void addToDB(Context context, boolean add){
    }

    public void createNotification(Activity activity, GregorianCalendar nt, boolean isToday, boolean ifRightNow){
    }
}
