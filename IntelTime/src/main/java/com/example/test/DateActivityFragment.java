package com.example.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Илья on 02.06.2016.
 */
public class DateActivityFragment extends Fragment {

    public DateActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_date, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Date", 0);
        String currentDate = sharedPreferences.getString("currentDate","");
        DBHelper dbHelper = new DBHelper(getContext());
        List<List<String>> res = dbHelper.getDailyEvents(currentDate);

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                res.size());
        Map<String, Object> m;
        for (int i = 0; i < res.size(); i++) {
            m = new HashMap<String, Object>();
            m.put("name", res.get(i).get(0));
            m.put("first line", res.get(i).get(1));
            m.put("second line", res.get(i).get(2));
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { "name", "first line",
                "second line" };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.textViewEventName, R.id.textViewTimeBounds1, R.id.textViewTimeBounds2};

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(getContext(), data, R.layout.list_day_events_item,
                from, to);

        // определяем список и присваиваем ему адаптер
        ListView lv =  (ListView) rootView.findViewById(R.id.listview_daily_events);
        lv.setAdapter(sAdapter);

        return rootView;
    }
}
