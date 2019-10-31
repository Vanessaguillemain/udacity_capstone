package com.example.android.bookstudyplanner.bookservice;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.bookstudyplanner.BookStudyPlannerWidget;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.AppExecutor;
import com.example.android.bookstudyplanner.database.PlanningEntity;

import java.util.List;

/**
 * Created by vanessa on 29/07/2019.
 */

public class WidgetService extends IntentService {

    // Constant for logging
    private static final String TAG = WidgetService.class.getSimpleName();
    //for DB access
    private static AppDatabase mDb;
    public static final String ACTION_UPDATE_WIDGETS = "ACTION_UPDATE_WIDGETS";

    public WidgetService(String name) {
        super(name);
    }

    public WidgetService() {
        super("WidgetService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGETS.equals(action)) {
                handleActionUpdateTodayWidgets(this);
            }
        }
    }

    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    public static void handleActionUpdateTodayWidgets(final Context context) {
        //Query to get the plannings of the day
        mDb = AppDatabase.getInstance(context);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BookStudyPlannerWidget.class));

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String imgPlanningToday = Utils.RESULT_NO_PLANNING_TODAY;
                int bookId = Utils.INTENT_VAL_BOOK_ID_EMPTY;
                int nbPagesToRead = 0;
                //load Data
                List<PlanningEntity> listPlannings = mDb.planningDao().loadAllWidgetPlanningsForDate(Utils.getToday(), false);
                if(listPlannings != null && !listPlannings.isEmpty()) {
                    PlanningEntity planning = listPlannings.get(0);
                    imgPlanningToday = planning.getImageLink();
                    bookId = planning.getBookId();
                    nbPagesToRead = planning.getNbPagesToRead();
                }
                BookStudyPlannerWidget.updateTodayWidgets(context, appWidgetManager, imgPlanningToday, bookId, nbPagesToRead, appWidgetIds);
            }
        });
    }
}
