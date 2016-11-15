package com.example.test;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Илья on 14.05.2016.
 */
public class MyAdapter extends ArrayAdapter implements SaveCallInterface{
    private Context context;
    private GregorianCalendar start = new GregorianCalendar();
    private GregorianCalendar finish = new GregorianCalendar();
    private GregorianCalendar repeatUntil = new GregorianCalendar();
    String repeatUntilWhat;
    String eventName = "";
    String numberOfRepeats = "";
    boolean isAllDay;


    @Override
    public void setValues() {
        Log.d("SAVE CALL", "INTERFACE");
        ArrayList<Object> t = new ArrayList<Object>();
        t.add(eventName);
        t.add(start);
        t.add(finish);
        t.add(isAllDay);
        t.add(repeatUntilWhat);
        t.add(repeatUntil);
        t.add(numberOfRepeats);
        mInterface.setValues(t);
    }

    public interface DataTransferInterface {
        public boolean setValues(ArrayList<Object> al);
    }
    DataTransferInterface mInterface;

    public MyAdapter(Context context, List objects, Activity a) {
        super(context, 0, objects);
        mInterface = (DataTransferInterface) a;
        start = (GregorianCalendar) GregorianCalendar.getInstance();
        start.set(GregorianCalendar.MINUTE, 0);
        finish = (GregorianCalendar) GregorianCalendar.getInstance();
        finish.set(GregorianCalendar.MINUTE, 0);
        repeatUntil = (GregorianCalendar) GregorianCalendar.getInstance();

        start.add(GregorianCalendar.HOUR, 1);
        finish.add(GregorianCalendar.HOUR, 2);
        repeatUntil.add(GregorianCalendar.MONTH, 2);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int resource;

        if (position  == 0 )
            resource = R.layout.row1;
        else if (position == 1)
            resource = R.layout.row2;
        else if (position == 2)
            resource = R.layout.row5;
        else if (position == 3)
            resource = R.layout.row3;
        else
            resource = R.layout.row4;

        final View rowView = inflater.inflate(resource, parent, false);

        final SharedPreferences settings = getContext().getSharedPreferences("Data", 0);

        final SharedPreferences.Editor editor = settings.edit();

        if (position == 0){
            EditText nameEvent = (EditText) rowView.findViewById(R.id.editText_EventName);
            nameEvent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    eventName = s.toString();
                }
            });
        }
        if (position == 1) {
            final TextView tvsd = (TextView) rowView.findViewById(R.id.startDate);
            final MyDateTime startMyDateTime = new MyDateTime(start.get(GregorianCalendar.YEAR), start.get(GregorianCalendar.MONTH), start.get(GregorianCalendar.DAY_OF_MONTH),
                    start.get(GregorianCalendar.DAY_OF_WEEK), start.get(GregorianCalendar.HOUR_OF_DAY), 0);

            final MyDateTime finishMyDateTime = new MyDateTime(finish.get(GregorianCalendar.YEAR), finish.get(GregorianCalendar.MONTH), finish.get(GregorianCalendar.DAY_OF_MONTH),
                    finish.get(GregorianCalendar.DAY_OF_WEEK), finish.get(GregorianCalendar.HOUR_OF_DAY), 0);

            tvsd.setText(startMyDateTime.getDate());
            tvsd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                        public String date;
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                            int dayOfWeek = date.get(date.DAY_OF_WEEK);
                            MyDateTime myDateTime = new MyDateTime(year, monthOfYear, dayOfMonth, dayOfWeek);
                            tvsd.setText(myDateTime.getDate());
                            start.set(year, monthOfYear, dayOfMonth);
                        }
                    };
                    DatePickerDialog dpd = new DatePickerDialog(getContext(), callback, start.get(GregorianCalendar.YEAR), start.get(GregorianCalendar.MONTH), start.get(GregorianCalendar.DAY_OF_MONTH));
                    dpd.show();
                }
            });

            final TextView tvfd = (TextView) rowView.findViewById(R.id.finishDate);
            tvfd.setText(finishMyDateTime.getDate());
            tvfd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                        public String date;
                        @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                            int dayOfWeek = date.get(date.DAY_OF_WEEK);
                            MyDateTime myDateTime = new MyDateTime(year, monthOfYear, dayOfMonth, dayOfWeek);
                            tvfd.setText(myDateTime.getDate());
                            finish.set(year, monthOfYear, dayOfMonth);
                        }
                    };
                    DatePickerDialog dpd = new DatePickerDialog(getContext(), callback, finish.get(GregorianCalendar.YEAR), finish.get(GregorianCalendar.MONTH), finish.get(GregorianCalendar.DAY_OF_MONTH));
                    dpd.show();
                }
            });

            final TextView tvst = (TextView) rowView.findViewById(R.id.startTime);
            MyDateTime dt = new MyDateTime(getHour(), 0);
            tvst.setText(startMyDateTime.getTime());
            tvst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            MyDateTime myDateTime = new MyDateTime(hourOfDay, minute);
                            tvst.setText(myDateTime.getTime());
                            start.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
                            start.set(GregorianCalendar.MINUTE, minute);
                        }
                    };
                    TimePickerDialog dpd = new TimePickerDialog(getContext(), callback, start.get(GregorianCalendar.HOUR_OF_DAY), start.get(GregorianCalendar.MINUTE), true);
                    dpd.show();
                }
            });

            final TextView tvft = (TextView) rowView.findViewById(R.id.finishTime);
            tvft.setText(finishMyDateTime.getTime());
            tvft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            MyDateTime myDateTime = new MyDateTime(hourOfDay, minute);
                            tvft.setText(myDateTime.getTime());
                            finish.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
                            finish.set(GregorianCalendar.MINUTE, minute);
                        }
                    };
                    TimePickerDialog dpd = new TimePickerDialog(getContext(), callback, finish.get(GregorianCalendar.HOUR_OF_DAY), finish.get(GregorianCalendar.MINUTE), true);
                    dpd.show();
                }
            });

            CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBoxAllDay);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        tvst.setVisibility(View.GONE);
                        tvft.setVisibility(View.GONE);
                        isAllDay = true;
                    } else
                    {
                        tvst.setVisibility(View.VISIBLE);
                        tvft.setVisibility(View.VISIBLE);
                        isAllDay = false;
                    }
                }
            });

        }

        if (position == 3){
            TextView tv =(TextView) rowView.findViewById(R.id.textRepeat);
            tv.setText(settings.getString("repeatState", ""));
        }
        if (position == 4){
            final MyDateTime myDateTime = new MyDateTime(repeatUntil.get(GregorianCalendar.YEAR), repeatUntil.get(GregorianCalendar.MONTH), repeatUntil.get(GregorianCalendar.DAY_OF_MONTH), repeatUntil.get(GregorianCalendar.DAY_OF_WEEK));
            final TextView tvru = (TextView) rowView.findViewById(R.id.textSetRepeatUntil);
            tvru.setText(myDateTime.getDate());
            tvru.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                        public String date;
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                            int dayOfWeek = date.get(date.DAY_OF_WEEK);
                            MyDateTime myDateTime = new MyDateTime(year, monthOfYear, dayOfMonth, dayOfWeek);
                            tvru.setText(myDateTime.getDate());
                            repeatUntil.set(year, monthOfYear, dayOfMonth);
                        }
                    };
                    DatePickerDialog dpd = new DatePickerDialog(getContext(), callback, repeatUntil.get(GregorianCalendar.YEAR), repeatUntil.get(GregorianCalendar.MONTH), repeatUntil.get(GregorianCalendar.DAY_OF_MONTH));
                    dpd.show();
                }
            });

            String[] data = {"date", "number"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) rowView.findViewById(R.id.spinner);

            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 0) {
                        repeatUntilWhat = "date";
                        TextView tv = (TextView) rowView.findViewById(R.id.textSetRepeatUntil);
                        tv.setVisibility(View.VISIBLE);
                        LinearLayout ll = (LinearLayout) rowView.findViewById(R.id.linearLayoutNumberRepeats);
                        ll.setVisibility(View.GONE);
                    }
                    else {
                        repeatUntilWhat = "number";
                        TextView tv = (TextView) rowView.findViewById(R.id.textSetRepeatUntil);
                        tv.setVisibility(View.GONE);
                        LinearLayout ll = (LinearLayout) rowView.findViewById(R.id.linearLayoutNumberRepeats);
                        ll.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        if (position == 2){
            TextView tv =(TextView) rowView.findViewById(R.id.textNotification);
            tv.setText(settings.getString("notificationState", ""));
        }

        if (position == 4){
            EditText editText = (EditText) rowView.findViewById(R.id.editTextNumberRepeats);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    numberOfRepeats = s.toString();
                }

            });
            rowView.setVisibility(View.GONE);
            return rowView;
        }
        return rowView;
    }

    private Calendar getDate(){
       return Calendar.getInstance();
    }

    private int getYear(){
        return getDate().get(Calendar.YEAR);
    }

    private int getMonth(){
        return getDate().get(Calendar.MONTH);
    }

    private int getDayOfMonth(){
        return getDate().get(Calendar.DAY_OF_MONTH);
    }

    private int getMinute(){
        return getDate().get(Calendar.MINUTE);
    }

    private int getHour(){
        return getDate().get(Calendar.HOUR_OF_DAY);
    }
}
