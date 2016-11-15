package com.example.test;

import android.util.Log;

import java.security.PrivateKey;

/**
 * Created by Илья on 15.05.2016.
 */
public class MyDateTime {
    private int year;
    private int month;
    private int day;
    private int dayOfWeek;
    private int hour;
    private int minute;
    private String [] months = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};
    private String [] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    MyDateTime(int y, int m, int d1, int d2) {
        year = y;
        month = m;
        day = d1;
        dayOfWeek = d2;
    }

    MyDateTime(int h, int m){
        hour = h;
        minute = m;
    }

    MyDateTime(int y, int m, int d1, int d2, int h, int min){
        year = y;
        month = m;
        day = d1;
        dayOfWeek = d2;
        hour = h;
        minute = min;

    }

    String getDate(){
        return days[dayOfWeek - 1] + ", " + day + " " + months[month] + " " + year;
    }

    public String getKeyString(){
        return  ((Integer)day).toString() + "-" + ((Integer)month).toString() + "-" + ((Integer)year).toString();
    }

    String getTime(){
        String h, m;
        if (hour <= 9)
            h = "0" + hour;
        else
            h = ((Integer)hour).toString();

        if (minute <= 9)
            m = "0" + minute;
        else
            m = ((Integer)minute).toString();
        return h + ":" + m;
    }
}
