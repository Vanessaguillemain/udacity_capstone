package com.example.android.bookstudyplanner.bookservice;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.bookstudyplanner.BookStudyPlannerWidget;

/**
 * Created by vanessa on 29/07/2019.
 */

public class WidgetService extends IntentService {

    //public static final String ACTION_UPDATE_TODAY_WIDGETS = "ACTION_UPDATE_TODAY_WIDGETS";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WidgetService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            /*
            if (ACTION_UPDATE_TODAY_WIDGETS.equals(action)) {
                //handleActionUpdateTodayWidgets();
            }*/
        }
    }

    /**
     * Starts this service to perform UpdateTodayWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    /*
    public static void startActionUpdateTodayWidgets(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_UPDATE_TODAY_WIDGETS);
        context.startService(intent);
    }*/

    public static void handleActionUpdateTodayWidgets(Context context) {
        //Query to get the book
        String imgString = "http://books.google.com/books/content?id=2MmoqvmP0WkC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api";

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BookStudyPlannerWidget.class));

        Uri imgRes = Uri.parse(imgString);

        //Now update all widgets
        BookStudyPlannerWidget.updateTodayWidgets(context, appWidgetManager, imgString, appWidgetIds);
    }
}
