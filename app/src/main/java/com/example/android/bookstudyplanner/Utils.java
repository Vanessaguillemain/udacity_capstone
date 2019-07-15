package com.example.android.bookstudyplanner;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vanessa on 09/07/2019.
 */

public class Utils {

    // Constant for logging
    private static final String TAG = Utils.class.getSimpleName();

    public static final String INTENT_KEY_BOOK_DETAIL_ACTION = "INTENT_KEY_BOOK_DETAIL_ACTION";
    public static final String INTENT_VAL_BOOK_DETAIL_ACTION_CREATE = "INTENT_VAL_BOOK_DETAIL_ACTION_CREATE";
    public static final String INTENT_VAL_BOOK_DETAIL_ACTION_MODIF = "INTENT_VAL_BOOK_DETAIL_ACTION_MODIF";
    public static final String WEEK_EMPTY = "0000000";
    public static final String INTENT_KEY_BOOK = "INTENT_KEY_BOOK";
    public static final int ERROR_NB_PAGES_AVERAGE =-1;
    public static final String ERROR_NB_SECONDS_A_DAY = "ERROR_NB_SECONDS_A_DAY";
    private static int DURATION_OF_DAY_IN_MILISEC = 1000 * 60 * 60 * 24;

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

    public static String getFormatedDateFromDate(Date date, Context context){
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, getCurrentLocale(context));
        return df.format(date);
    }

    public static int getYearFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
    public static int getMonthFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }
    public static int getDayFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static java.util.Date getDateFromFormatedDate(String formatedDate, Context context){
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, getCurrentLocale(context));
        Date d = null;
        try {
            d = df.parse(formatedDate);
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }
        return d;
    }

    public static boolean dateIsBeforeToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        int compare = today.compareTo(date);
        if (compare <= 0) return false;
        return true;
    }

    public static boolean dateOneIsBeforeDateTwo(Date dateOne, Date dateTwo) {
        int compare = dateOne.compareTo(dateTwo);
        if (compare <= 0) return true;
        return false;
    }

    static Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    public static int calculateNbPagesAverage(int pagesToRead, Date fromDate, Date toDate, int[] weekPlanning, int nbDaysAWeek) {
        if(pagesToRead >0 && fromDate != null && toDate != null && nbDaysAWeek > 0) {
            int nbTotalDays = daysBetweenDatesIncluded(fromDate, toDate);
            int nbWeeks = (int)nbTotalDays/7;

            int indexFirstDay = getMyDayOfWeek(fromDate)-2; //-2 because Monday is 0 in weekPlanning[] and 2 in Calendar.MONDAY
            int nbDaysToReadDuringWeeks = nbDaysAWeek * nbWeeks;
            int nbDaysLeft = nbTotalDays - nbWeeks*7;
            int nbDaysLeftToRead = 0;
            for(int i = indexFirstDay; i< indexFirstDay+nbDaysLeft;i++) {
                int index = i%7;
                if(weekPlanning[index] == 1) nbDaysLeftToRead++;
            }
            int nbTotalDaysToRead = nbDaysToReadDuringWeeks + nbDaysLeftToRead;

            if(nbTotalDaysToRead > 0)
                return (int) Math.ceil((float)pagesToRead/nbTotalDaysToRead);
        }
        return ERROR_NB_PAGES_AVERAGE;
    }

    public static List<Date> getPlanning(Date fromDate, Date toDate, int[] weekPlanning, int nbDaysAWeek) {
        if(fromDate != null && toDate != null) {
            List<Date> planning = new ArrayList<Date>();
            if (nbDaysAWeek == 0) {
                return planning;
            }
            Date currentDate = fromDate;
            int i = getMyDayOfWeek(fromDate) - 2; //-2 because Monday is 0 in weekPlanning[] and 2 in Calendar.MONDAY
            while (currentDate.compareTo(toDate) <= 0) {
                int index = i % 7;
                if (weekPlanning[index] == 1) {
                    planning.add(currentDate);
                }
                currentDate = dateAfter(currentDate);
                i++;
            }
            return planning;
        }
        return null;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date dateAfter(Date date) {
        return addDays(date, 1);
    }

    /**
     * Gives the index of the Day (Calendar.DAY_OF_WEEK ) for the date given.
     * @param date : the date whe want to know the day of week
     * @return Calendar.DAY_OF_WEEK for the date
     */
    private static int getMyDayOfWeek(Date date) {
        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(date);
        return calendarFrom.get(Calendar.DAY_OF_WEEK);
    }

    public static int daysBetweenDatesIncluded(Date date1, Date date2 ){
        long diff = date2.getTime()-date1.getTime();
        long temp = diff/DURATION_OF_DAY_IN_MILISEC;
        return (int)temp +1;
    }

    public static int entireWeeksBetweenDates(Date date1, Date date2 ){
        int days = daysBetweenDatesIncluded(date1, date2);
        return (int)days/7;
    }

    public static String secondsToText(int seconds){
        if(seconds >= 24*3600) {
            return ERROR_NB_SECONDS_A_DAY;
        }
        int hours = (int)seconds/3600;
        int minutes = (seconds-hours*3600)/60;
        String result ="";
        if(hours>0) {
            result = hours+"h";
        }
        if(minutes >0) {
            if (minutes<10) result += "0";
            result += minutes + "min";
        }
        return result;
    }
}
