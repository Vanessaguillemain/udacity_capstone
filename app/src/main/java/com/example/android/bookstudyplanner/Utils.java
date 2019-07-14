package com.example.android.bookstudyplanner;

import android.content.Context;
import android.os.Build;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vanessa on 09/07/2019.
 */

public class Utils {
    public static final String INTENT_KEY_BOOK_DETAIL_ACTION = "INTENT_KEY_BOOK_DETAIL_ACTION";
    public static final String INTENT_VAL_BOOK_DETAIL_ACTION_CREATE = "INTENT_VAL_BOOK_DETAIL_ACTION_CREATE";
    public static final String INTENT_VAL_BOOK_DETAIL_ACTION_MODIF = "INTENT_VAL_BOOK_DETAIL_ACTION_MODIF";
    public static final String INTENT_KEY_TAB_POSITION = "INTENT_KEY_TAB_POSITION";
    public static final int INTENT_VAL_TAB_POSITION_BOOKS = 0;
    public static final int INTENT_VAL_TAB_POSITION_TODAY = 1;
    public static final int INTENT_VAL_TAB_POSITION_PLANNING = 2;


    public static void tostL(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
    }

    public static void tostS(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getTime(int totalSecs, String sHours, String sMinutes) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        String sMin = String.valueOf(minutes);
        if(minutes <10) {
            sMin = "0" + minutes;
        }
        String result = String.valueOf(hours) + " " + sHours+ " " + sMin + " " + sMinutes;
        return result;
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public static String getFormatedDateFromDatePicker(DatePicker datePicker, Context context){
        java.util.Date date = getDateFromDatePicker(datePicker);
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, getCurrentLocale(context));
        return df.format(date);
    }

    public static boolean dateIsBeforeToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        int compare = today.compareTo(date);
        if (compare <= 0) return false;
        return true;
    }

    static Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }
}
