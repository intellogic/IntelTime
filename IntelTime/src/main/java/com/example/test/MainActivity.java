package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static ListView lv;
    private static Calendar cal = Calendar.getInstance();
    private static ArrayAdapter<String> t;
    public static String [] months = {"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("WE CREATE NEW", "FRAGMENT");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        assert fabSpeedDial != null;
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, EventSetterActivity.class);
                startActivity(intent);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }




    public static class MainActivityFragment extends Fragment {

        public MainActivityFragment() {
        }

        public List<String> words;
        public List<Integer> diff = new ArrayList<Integer>();
        DBHelper dbHelper;


        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            DBHelper dbHelper = new DBHelper(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            String[] strings = new String[30];
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 30; i++) {
                cal.add(Calendar.DAY_OF_YEAR, i - 10);
                String query = cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR);
                Cursor c = null;
                String selection = DBHelper.KEY_DATE +" = ?";
                String[] selectionArgs = {query};
                if (db != null) {
                    //Log.d("WE CHECK THIS SHit", "-");
                    c = db.query(DBHelper.TABLE_DATES, null, selection, selectionArgs, null, null, null);
                    int numberEvents = c.getCount();
                    // Log.d("NUMER OF EVENTS ", strings[i] + " " + ((Integer)numberEvents).toString());
                    strings[i] = query + " - " + numberEvents;
                }
                cal = Calendar.getInstance();
                diff.add(diff.size(), i - 10);
                c.close();
            }
            db.close();



            words = new ArrayList<String>(Arrays.asList(strings));
            t = new MyMainListAdapter(getActivity(), words);
            t.notifyDataSetChanged();
            lv = (ListView) rootView.findViewById(R.id.listview_forecast);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DateActivity.class);
                    intent.putExtra("currentDate", words.get(position));
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("Date", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("currentDate", words.get(position).substring(0, words.get(position).indexOf(' ')));
                    editor.apply();
                    startActivity(intent);
                }
            });
            lv.setOnScrollListener(new MyScrollListener() {
                @Override
                public boolean onLoadMore(int page) {
                    Log.d("SDADASDS", "");

                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to your AdapterView
                    customLoadMoreDataFromApi(page);
                    // or customLoadMoreDataFromApi(totalItemsCount);
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                }
            });
            lv.setAdapter(t);
            lv.setSelection(10);
            return rootView;
        }

        public void customLoadMoreDataFromApi(int page) {
           // DBHelper dbHelper = new DBHelper(getContext());
           // SQLiteDatabase db = dbHelper.getWritableDatabase();
            // Cursor c = null;
            boolean isZero = false;
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date date = cal.getTime();
            Log.d(TAG, "---------------");
            Log.d(TAG, "---------------");
            if (page == 0) {
                Log.d("SDADASDS", "");
               try {
                    date = format.parse(words.get(0));
                    Log.d("NEW", words.get(0));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, diff.get(0) - 1);
                diff.add(0, diff.get(0) - 1);
                String query = cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR);

                BackgroundManager bgm = new BackgroundManager();
                Object [] params = {getContext(), query};
                int numOfEvents = (Integer) bgm.doInBackground(params);
                /*String selection = DBHelper.KEY_DATE + " = ?";
                String[] selectionArgs = {query};
                c = db.query(DBHelper.TABLE_DATES, null, selection, selectionArgs, null, null, null);
                int numOfEvents = c.getCount();*/
                 String putString = query + " - " + numOfEvents;
                words.add(0, putString);
                //words.remove(words.size() - 1);
                isZero = true;

                /*String selection = DBHelper.KEY_DATE + " = ?";
                String[] selectionArgs = {query};
                c = db.query(DBHelper.TABLE_DATES, null, selection, selectionArgs, null, null, null);
                int numOfEvents = c.getCount();
                String putString = query + " - " + numOfEvents;
                words.add(0, putString);
                isZero = true;
                c.close();
                db.close();*/

            } else {
                try {
                    date = format.parse(words.get(words.size() - 1));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, diff.get(diff.size() - 1) + 1);
                diff.add(diff.size(), diff.get(diff.size() - 1) + 1);

                String query = cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR);
                BackgroundManager bgm = new BackgroundManager();
                Object [] params = {getContext(), query};
                int numOfEvents = (Integer) bgm.doInBackground(params);
                /*String selection = DBHelper.KEY_DATE + " = ?";
                String[] selectionArgs = {query};
                c = db.query(DBHelper.TABLE_DATES, null, selection, selectionArgs, null, null, null);
                int numOfEvents = c.getCount();*/
                String putString = query + " - " + numOfEvents;
                words.add(words.size(), putString);
                //words.remove(0);
                /*c.close();
                db.close();*/
            }
            t.notifyDataSetChanged();
            if (isZero)
                lv.setSelection(9);
        }
    }
}
