package com.example.test;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.CheckedInputStream;

/**
 * Created by Илья on 24.02.2016.
 */
public class RepeatDialog extends DialogFragment {

    private int whatDialog;
    private int checkedPosition;
    private static String [] strings1 = {"Don't repeat", "Every day", "Every week", "Every 2nd week", "Every month"};
    private static String [] strings2 = {"No notification", "10 minutes", "30 minutes", "One hour", "One day"};

    public RepeatDialog(int dialog){
        super();
        whatDialog = dialog;
        if (dialog == 2)
            checkedPosition = EventSetterActivity.notificationChoose;
        else
            checkedPosition = EventSetterActivity.repeatChoose;

    }

    public interface NoticeDialogListener {
        void onRepeatDialogStateChanged(Context context, DialogFragment dialog, int whatDialog, int position, String choose);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.repeat_dialog, null);

        final List<String> words;
        if (whatDialog == 1)
            words = new ArrayList<String>(Arrays.asList(strings1));
        else
            words = new ArrayList<String>(Arrays.asList(strings2));
        //RepeatAdapter <String> t =  new RepeatAdapter(getActivity(), R.layout.repeat_list_item, R.id.repeat_text_view, words);
        final ListView lv = (ListView) rootView.findViewById(R.id.repeatListView);
        final int icon_check = R.drawable.ic_check;

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lv.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.repeat_list_item,
                R.id.repeat_text_view, words) {
                          @Override
                          public View getView(int position, View convertView, ViewGroup parent) {
                              View v = super.getView(position, convertView, parent);
                              ImageView icon = (ImageView) v.findViewById(R.id.imageView2);
                              if (position == checkedPosition) {
                                  icon.setImageResource(icon_check);
                              } else
                              if (position == 0){
                                  icon.setVisibility(View.INVISIBLE);
                              }
                              return v;
                          }
                      });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //editor.putString("repeatState", words.get(position));
                //editor.commit();
                mListener.onRepeatDialogStateChanged(getContext(), RepeatDialog.this, whatDialog, position, words.get(position));
                getDialog().dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder

                .setView(rootView)
                /*.setSingleChoiceItems(t, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("repeatState", words.get(which));
                        editor.commit();
                        mListener.onRepeatDialogStateChanged(RepeatDialog.this);
                        dialog.dismiss();
                    }
                })*/
        ;
        // Create the AlertDialog object and return it
        return builder.create();
    }
    @Override
    public void onStart()
    {
        super.onStart();
        Button pButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        Button nButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(Color.WHITE);
        pButton.setTextColor(Color.WHITE);
    }
}