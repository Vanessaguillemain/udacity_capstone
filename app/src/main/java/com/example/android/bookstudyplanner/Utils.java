package com.example.android.bookstudyplanner;

import android.content.Context;
import android.widget.Toast;

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

}
