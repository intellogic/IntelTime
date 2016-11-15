package com.example.test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Илья on 02.06.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    static int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String name = intent.getExtras().getString("name");
        String text = intent.getExtras().getString("text");
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(name)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notif_white)
                .build();
        Log.d("NOTIFY!", "4353345353");
        notificationManager.notify(NOTIFICATION_ID++, notification);
    }
}