package com.example.test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class EventSetterActivity extends AppCompatActivity implements RepeatDialog.NoticeDialogListener, MyAdapter.DataTransferInterface {
    DBHelper dbHelper;
    static ArrayAdapter<String> t;
    private boolean showRepeatView = false;
    public static int repeatChoose;
    public static int notificationChoose;

    static SaveCallInterface mInterface;

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CREATE", "EventSetterActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_setter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(this);

        repeatChoose = notificationChoose = 0;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_setter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {

            mInterface.setValues();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRepeatDialogStateChanged(Context context, DialogFragment dialog, int whatDialog, int position, String choose) {
        if (whatDialog == 1) {
            repeatChoose = position;
            int index = 3;
            ListView lv = (ListView) findViewById(R.id.listview_forecast);
            View v = lv.getChildAt(index - lv.getFirstVisiblePosition());
            TextView tv = (TextView) v.findViewById(R.id.textRepeat);
            tv.setText(choose);
            index++;
            v = lv.getChildAt(index - lv.getFirstVisiblePosition());
            if (position > 0 && !showRepeatView) {
                v.setVisibility(View.VISIBLE);
                showRepeatView = true;
            } else if (position == 0 && showRepeatView) {
                v.setVisibility(View.GONE);
                showRepeatView = false;
            }
        } else {
            notificationChoose = position;
            int index = 2;
            ListView lv = (ListView) findViewById(R.id.listview_forecast);
            View v = lv.getChildAt(index - lv.getFirstVisiblePosition());
            TextView tv = (TextView) v.findViewById(R.id.textNotification);
            tv.setText(choose);
        }
        Log.d("POSITION", ((Integer)repeatChoose).toString() + " " + ((Integer)notificationChoose).toString());

    }

    @Override
    public boolean setValues(ArrayList<Object> al) {


        String eventName = (String) al.get(0);
        GregorianCalendar start = (GregorianCalendar)((GregorianCalendar) al.get(1)).clone();
        GregorianCalendar finish = (GregorianCalendar)((GregorianCalendar) al.get(2)).clone();
        GregorianCalendar startClone = (GregorianCalendar) start.clone();
        GregorianCalendar notificationTime = (GregorianCalendar) GregorianCalendar.getInstance();
        notificationTime.set(GregorianCalendar.YEAR, start.get(GregorianCalendar.YEAR));
        notificationTime.set(GregorianCalendar.DAY_OF_YEAR, start.get(GregorianCalendar.DAY_OF_YEAR));
        notificationTime.set(GregorianCalendar.HOUR_OF_DAY, start.get(GregorianCalendar.HOUR_OF_DAY));
        notificationTime.set(GregorianCalendar.MINUTE, start.get(GregorianCalendar.MINUTE));
        Calendar currentCalendarTime = Calendar.getInstance();
        Boolean isAllDay = (boolean) al.get(3);
        String repeatUntilWhat = (String) al.get(4);
        GregorianCalendar repeatUntil = (GregorianCalendar)((GregorianCalendar) al.get(5)).clone();
        repeatUntil.set(GregorianCalendar.HOUR, 23);
        repeatUntil.set(GregorianCalendar.MINUTE, 59);
        repeatUntil.set(GregorianCalendar.SECOND, 59);
        String numOfRepeats = (String) al.get(6);
        GregorianCalendar currenttime = (GregorianCalendar) Calendar.getInstance();
        Log.d("SET VALUES", "OK");
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        Log.d("START", start.toString());
        Log.d("FINISH", finish.toString());
        String uniqueID = UUID.randomUUID().toString();
        Log.d("ID", uniqueID);
        int numberOfRepeats = 0;

        switch (notificationChoose){
            case 1:
                notificationTime.add(GregorianCalendar.MINUTE, -10);
                break;
            case 2:
                notificationTime.add(GregorianCalendar.MINUTE, -30);
                break;
            case 3:
                notificationTime.add(GregorianCalendar.HOUR, -1);
                break;
            case 4:
                notificationTime.add(GregorianCalendar.DAY_OF_YEAR, -1);
        }

        if (notificationChoose > 0 && notificationTime.before(currentCalendarTime))
        {
            v.vibrate(100);
            Toast.makeText(this, "You notification time was set before current time. Fix it!", Toast.LENGTH_LONG).show();
            return false;
        }

        //Check filling
        if (eventName.length() == 0){
            v.vibrate(100);
            Toast.makeText(this, "You event doesn't have name. Fix it!", Toast.LENGTH_LONG).show();
            return false;
        }
        Log.d("Event name", eventName);

        if (finish.before(start)){
            v.vibrate(100);
            Toast.makeText(this, "You event will finish before start. Fix it!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (finish.before(currenttime)){
            v.vibrate(100);
            Toast.makeText(this, "You event will finish in past. Fix it!", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean isOneDay = false;
        if (start.get(GregorianCalendar.DAY_OF_YEAR) == finish.get(GregorianCalendar.DAY_OF_YEAR)
                && start.get(GregorianCalendar.YEAR) == finish.get(GregorianCalendar.YEAR))
            isOneDay = true;

        Event newEvent;


        if (repeatChoose == 0) {
            if (isOneDay)
                newEvent = new SimpleEvent(eventName, start, finish, isAllDay, "", "");
                else
                newEvent = new ComplexEvent(eventName, start, finish, isAllDay, "");
            if (notificationChoose > 0) {
                boolean isToday = true;
                notificationTime.set(GregorianCalendar.SECOND, 0);
                notificationTime.setTimeZone(TimeZone.getTimeZone("UTC+0"));
                Log.d("NOTIFICATION TIME", notificationTime.toString());
                if (notificationTime.get(GregorianCalendar.DAY_OF_YEAR) !=
                        currentCalendarTime.get(Calendar.DAY_OF_YEAR))
                    isToday = false;
                newEvent.createNotification(this, notificationTime, isToday, false);
            }
            newEvent.createNotification(this, start, true, true);
            newEvent.print();
            newEvent.addToDB(this, true);
        }
        if (repeatChoose > 0) {
            int field = GregorianCalendar.HOUR_OF_DAY, add = 1;
            switch (repeatChoose) {
                case 1:
                    field = GregorianCalendar.DAY_OF_YEAR;
                    break;
                case 2:
                    field = GregorianCalendar.WEEK_OF_YEAR;
                    break;
                case 3:
                    field = GregorianCalendar.WEEK_OF_YEAR;
                    add++;
                    break;
                case 4:
                    field = GregorianCalendar.MONTH;
                    break;
            }
            if (repeatChoose > 0 && repeatUntilWhat.equals("number")) {
                try {
                    numberOfRepeats = Integer.parseInt(numOfRepeats);
                } catch (NumberFormatException e) {
                    v.vibrate(100);
                    Toast.makeText(this, "Number of repeats is false. Fix it!", Toast.LENGTH_LONG).show();
                    return false;
                }
                //create event
            } else if (repeatChoose > 0 && repeatUntilWhat.equals("date")) {
                if (finish.after(repeatUntil)) {
                    v.vibrate(100);
                    Toast.makeText(this, "You event will finish after repeat-until time. Fix it!", Toast.LENGTH_LONG).show();
                    return false;
                }
                //create event
                while (startClone.before(repeatUntil)) {
                    startClone.add(field, add);
                    numberOfRepeats++;
                }
                /*if (startClone.get(GregorianCalendar.DAY_OF_YEAR) == repeatUntil.get(GregorianCalendar.DAY_OF_YEAR) &&
                        startClone.get(GregorianCalendar.YEAR) == repeatUntil.get(GregorianCalendar.YEAR) )
                    numberOfRepeats++;*/

            }
            String repeatID = UUID.randomUUID().toString();
            for (int i = 0; i < numberOfRepeats; i++){
                if (isOneDay)
                    newEvent = new SimpleEvent(eventName, start, finish, isAllDay, "", repeatID);
                else
                    newEvent = new ComplexEvent(eventName, start, finish, isAllDay, repeatID);
                newEvent.print();
                if (notificationChoose > 0) {
                    boolean isToday = true;
                    notificationTime.set(GregorianCalendar.SECOND, 0);
                    notificationTime.setTimeZone(TimeZone.getTimeZone("UTC+0"));
                    Log.d("NOTIFICATION TIME", notificationTime.toString());
                    if (notificationTime.get(GregorianCalendar.DAY_OF_YEAR) !=
                            currentCalendarTime.get(Calendar.DAY_OF_YEAR))
                        isToday = false;
                    newEvent.createNotification(this, notificationTime, isToday, false);
                }
                newEvent.createNotification(this, start, true, true);
                newEvent.print();
                newEvent.addToDB(this, true);
                start.add(field, add);
                finish.add(field, add);
                notificationTime.add(field, add);
            }
        }
        this.finish();
        return true;
    }


    public static class EventSetterFragment extends Fragment {
        public EventSetterFragment() {
        }
        public List<String> words;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_setter, container, false);
            String[] strings = {"Name", "MyDateTime", "Repeat?", "SetRepeat", "Notification"};
            words = new ArrayList<String>(Arrays.asList(strings));
            t = new MyAdapter(getActivity(), words, getActivity());
            mInterface = (SaveCallInterface) t;
            ListView lv = (ListView)rootView.findViewById(R.id.listview_forecast);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (id == 2){
                        RepeatDialog rd = new RepeatDialog(2);
                        rd.show(getFragmentManager(), "");
                    }

                    if (id == 3){
                        RepeatDialog rd = new RepeatDialog(1);
                        rd.show(getFragmentManager(), "");
                    }

                }

            });
            final SharedPreferences settings = getActivity().getSharedPreferences("Data", 0);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putString("repeatState", "Don't repeat");
            editor.putString("notificationState", "No notification");
            editor.commit();
            lv.setAdapter(t);

            return rootView;
        }
    }
}
