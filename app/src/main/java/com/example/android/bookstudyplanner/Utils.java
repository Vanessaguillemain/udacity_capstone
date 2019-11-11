package com.example.android.bookstudyplanner;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.DatePicker;
import android.widget.Toast;

import java.lang.reflect.Method;
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
    public static final String INTENT_VAL_BOOK_DETAIL_ACTION_FROM_SEARCH = "INTENT_VAL_BOOK_DETAIL_ACTION_FROM_SEARCH";
    public static final String INTENT_VAL_BOOK_DETAIL_ACTION_MODIF = "INTENT_VAL_BOOK_DETAIL_ACTION_MODIF";
    public static final String INTENT_KEY_BOOK = "INTENT_KEY_BOOK";
    public static final String INTENT_KEY_METADATA = "INTENT_KEY_METADATA";
    public static final String INTENT_KEY_WIDGET_PAGE = "INTENT_KEY_WIDGET_PAGE";
    public static final int INTENT_VAL_WIDGET_PAGE_1 = 1;
    public static final String INTENT_KEY_WIDGET_BOOK_ID = "INTENT_KEY_WIDGET_BOOK_ID";
    public static final int INTENT_VAL_BOOK_ID_EMPTY = -1;

    public static final String BUNDLE_KEY_VOLUME_LIST = "BUNDLE_KEY_VOLUME_LIST";
    public static final String BUNDLE_KEY_ERROR_ISBN = "BUNDLE_KEY_ERROR_ISBN";
    public static final String BUNDLE_KEY_ERROR_NO_BOOK_FOUND = "BUNDLE_KEY_ERROR_NO_BOOK_FOUND";
    public static final int ERROR_NB_PAGES_AVERAGE =-1;
    public static final int ERROR_NB_DAYS_TO_READ =-1;
    public static final int ERROR_NB_DAYS_TO_READ_ZERO =-2;
    public static final String ERROR_NB_SECONDS_A_DAY = "ERROR_NB_SECONDS_A_DAY";
    public static final String RESULT_NO_PLANNING_TODAY = "RESULT_NO_PLANNING_TODAY";
    private static int DURATION_OF_DAY_IN_SEC = 60 * 60 * 24;
    public static final int SCREEN_BOOKS = 1;
    public static final int SCREEN_TODAY = 2;

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
        calendar.set(year, month, day, 0, 0, 0);
        //divide by 1000 to avoid milliseconds
        long lDate = calendar.getTime().getTime()/1000;
        Date d = new Date(lDate*1000);
        return d;
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
            Date today = getToday();
            int compare = today.compareTo(date);
            if (compare <= 0) return false;
            return true;
    }

    /**
     * Uses Date.compareTo() to compare the 2 dates in parameters.
     * @param dateOne : the date supposed to be before dateTwo
     * @param dateTwo : the date supposed to be after dateOne
     * @return <code>true</code> if dateOne is before dateTwo, or the same date.
     * @exception NullPointerException if <code>dateOne</code> or <code>dateTwo</code> is null.
     */
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
        if(pagesToRead >0) {
            int nbTotalDaysToRead = calculateNbDaysToRead(fromDate, toDate, weekPlanning, nbDaysAWeek);
            if(ERROR_NB_DAYS_TO_READ != nbTotalDaysToRead) {
                if (nbTotalDaysToRead > 0) {
                    return (int) Math.ceil((float) pagesToRead / nbTotalDaysToRead);
                } else {
                    return ERROR_NB_DAYS_TO_READ_ZERO;
                }
            }
        }
        return ERROR_NB_PAGES_AVERAGE;
    }

    public static int calculateNbDaysToRead(Date fromDate, Date toDate, int[] weekPlanning, int nbDaysAWeek) {
        if(fromDate != null && toDate != null && nbDaysAWeek > 0) {
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
            return nbTotalDaysToRead;
        }
        return ERROR_NB_DAYS_TO_READ;
    }

    /**
     * Calculate the date of current day, without current hours.minutes...
     * To be able to compare with date picked.
     * @return the date of current day, without current hours.minutes...
     */
    public static Date getToday() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day,0,0,0);
        // divide by 1000 to avoid milliseconds
        long lToday = cal.getTime().getTime()/1000;
        Date d = new Date(lToday*1000);
        return d;
    }

    /**
     * Calculate the date of current day, without year, with current hours.minutes
     * To be able to compare with date picked.
     * @return the date of current day, without current hours.minutes...
     */
    public static String getTodayHourMinute() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String date = day+"/"+month+" "+hour+":"+minute;
        return date;
    }

    public static List<Date> getPlanning(Date fromDate, Date toDate, int[] weekPlanning, int nbDaysAWeek) {
        if(fromDate != null && toDate != null) {
            List<Date> planning = new ArrayList<>();
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

    public static int calculateNbDaysAWeek(int[] weekPlanning) {
        int nbDaysAWeek = 0;
        for(int i=0; i<weekPlanning.length; i++){
            nbDaysAWeek += weekPlanning[i];
        }
        return nbDaysAWeek;
    }

    public static List<Date> getPlanning(Date fromDate, Date toDate, int[] weekPlanning) {
        int nbDaysAWeek = calculateNbDaysAWeek(weekPlanning);
        return getPlanning( fromDate,  toDate,  weekPlanning,  nbDaysAWeek);
    }

    public static int[] getTabWeekPlanningFromString(String sWeekPlanning) {
        if (sWeekPlanning != null) {
            int[] tab = new int[7];
            for(int i = 0; i< sWeekPlanning.length(); i++) {
                if(sWeekPlanning.charAt(i)=='0') {
                    tab[i] =0;
                } else {
                    tab[i] =1;
                }
            }
            return tab;
        }
        return null;
    }

    public static String getStringWeekPlanningFromTab(int[] tabWeekPlanning) {
        String sWeekPlanning = "";
        for(int i=0; i<7; i++) {
            sWeekPlanning += (tabWeekPlanning[i] == 1) ? "1" : "0";
        }
        return sWeekPlanning;
    }

    public static int getNbTotalWeekPlanningFromTab(int[] tabWeekPlanning) {
        int total = 0;
        for(int i=0; i<7; i++) {
            total += (tabWeekPlanning[i] == 1) ? 1 : 0;
        }
        return total;
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

    public static Date dateBefore(Date date) {
        return addDays(date, -1);
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
        //divide by 1000 because date.getTime() have milliseconds that causes
        //wrong calculation just for some milliseconds of differences
        long lDate1 = date1.getTime()/1000;
        long lDate2 = date2.getTime()/1000;
        long diff = lDate2-lDate1;
        long temp = diff/DURATION_OF_DAY_IN_SEC;
        return (int)temp +1;
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

    public static boolean isInteger(String string) {
        if(string != null && string.length() >0 ) {
            try {
                for (int i = 0; i < string.length(); i++) {
                    Integer.parseInt(String.valueOf(string.charAt(i)), 10);
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isValidISBN13(String string) {
        int[] validTab = {1,3,1,3,1,3,1,3,1,3,1,3};
        if(string != null && string.length() ==13 ) {
            int sum = 0;
            try {
                for (int i = 0; i < string.length()-1; i++) {
                    int digit = Integer.parseInt(String.valueOf(string.charAt(i)), 10);
                    sum += digit * validTab[i];
                }
                int s = sum%10;
                if(s != 0) {
                    s = 10-s;
                }
                if(s == Integer.parseInt(String.valueOf(string.charAt(12)), 10)) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    // Returns ISBN error syndrome, zero for a valid ISBN, non-zero for an invalid one.
    // digits[i] must be between 0 and 10.
    public static int CheckISBN10(char digits[]){
        int i, s = 0, t = 0;
        for (i = 0; i < 10; i++) {
            t += digits[i];
            s += t;
        }
        return s % 11;
    }

    public static boolean isValidISBN10(String isbn10) {
        if(isbn10 != null && isbn10.length() == 10) {
            char[] tab = isbn10.toCharArray();
            if(CheckISBN10(tab) == 0) {
                return true;
            }
        }
        return false;
    }

    public static String getTruncatedStringFor(String input, final int screen, Context context) {
        String result = input;
        int valMax = 0;
        if (screen == Utils.SCREEN_BOOKS) {
             valMax = context.getResources().getInteger(R.integer.books_title_width);
        }
        if (screen == Utils.SCREEN_TODAY) {
            valMax = context.getResources().getInteger(R.integer.today_title_width);
        }
        if(input.length() > valMax) {
            result = input.substring(0, valMax-1) + "...";
        }
        return result;
    }

    public static double getPercentRead(Integer nbPagesRead, Integer nbPagesToRead) {
        if (nbPagesRead !=null) {
            if (nbPagesToRead > 0 && nbPagesRead > 0) {
                Double percent = (new Double(nbPagesRead)/new Double(nbPagesToRead))*100;
                double roundOff = Math.round(percent * 100.0) / 100.0;
                return roundOff;
            }
        }
        return 0;
    }

    public static void getScreenSize(Context context, Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        int realWidth;
        int realHeight;

        if (Build.VERSION.SDK_INT >= 17){
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }

        Log.d("Display Info", "realWidth=" + realWidth);
        Log.d("Display Info", "realHeight=" + realHeight);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.d("Display Info", "dpWidth=" + dpWidth);
        Log.d("Display Info", "dpHeight=" + dpHeight);
    }

    public static long getDiffForMidnight() {
        Calendar now = Calendar.getInstance();
        Calendar midNight = Calendar.getInstance();
        midNight.set(Calendar.HOUR, 12);
        midNight.set(Calendar.MINUTE, 0);
        midNight.set(Calendar.SECOND, 0);
        midNight.set(Calendar.MILLISECOND, 0);
        midNight.set(Calendar.AM_PM, Calendar.AM);

        long diff = now.getTimeInMillis() - midNight.getTimeInMillis();

        if (diff < 0) {
            midNight.add(Calendar.DAY_OF_MONTH, 1);
            diff = midNight.getTimeInMillis() - now.getTimeInMillis();
        }
        return diff;
    }
}
