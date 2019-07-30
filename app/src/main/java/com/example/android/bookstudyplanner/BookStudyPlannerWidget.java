package com.example.android.bookstudyplanner;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import com.example.android.bookstudyplanner.bookservice.WidgetService;
import com.example.android.bookstudyplanner.uis.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of App Widget functionality.
 */
public class BookStudyPlannerWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager, final String imgRes,
                                int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.book_study_planner_widget);

        //Test String image
        if(imgRes == null || Utils.RESULT_NO_PLANNING_TODAY.equals(imgRes)) {
            views.setImageViewResource(R.id.widget_book_image,R.drawable.palmier);
        } else {
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable(){
                @Override
                public void run() {
                    Picasso.with(context)
                            .load(imgRes)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    views.setImageViewBitmap(R.id.widget_book_image, bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    //do something when loading failed
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    //do something while loading
                                }
                            });
                }
            });
        }
        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Utils.INTENT_KEY_WIDGET_PAGE, Utils.INTENT_VAL_WIDGET_PAGE_1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_book_image, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetService.handleActionUpdateTodayWidgets(context);
    }

    public static void updateTodayWidgets(Context context, AppWidgetManager appWidgetManager,
                                          String imgRes, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, imgRes, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

