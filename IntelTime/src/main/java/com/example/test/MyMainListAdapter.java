package com.example.test;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Илья on 02.06.2016.
 */
public class MyMainListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final List<String> names;


    static class ViewHolder {
        public TextView text;
        public LinearLayout linearLayout;
        public TextView newMonth;
        public TextView newYear;

    }

    public MyMainListAdapter(Activity context,List<String> names) {
        super(context, R.layout.resume_list_item, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.resume_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.list_item_forecast_textview);
            viewHolder.linearLayout = (LinearLayout) rowView.findViewById(R.id.dividerLL);
            viewHolder.newMonth = (TextView) rowView.findViewById(R.id.textView_new_month);
            viewHolder.newYear = (TextView) rowView.findViewById(R.id.textView_new_year);


            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        //Log.d("SUBSTR", )
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.linearLayout.getLayoutParams();
        String [] time = names.get(position).split(" ");
        String [] parts = time[0].split("-");
        if (names.get(position).substring(0,2).equals("1-")) {
            String t = String.valueOf(Integer.parseInt(parts[1]));
            holder.newMonth.setText(MainActivity.months[Integer.parseInt(parts[1])]);
            holder.newYear.setText(parts[2]);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        }
        else
            params.height = 0;

        holder.linearLayout.setLayoutParams(params);
        String s = names.get(position);
        String event;
        if (time[2].equals("0"))
            event = " no events";
        else if (time[2].equals("1"))
            event = time[2] + " event";
        else
            event = time[2] + " events";
        holder.text.setText(MainActivity.months[Integer.parseInt(parts[1])] + ", " + parts[0] + " - " + event);



        return rowView;
    }
}

