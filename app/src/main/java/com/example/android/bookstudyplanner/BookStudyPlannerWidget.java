package com.example.android.bookstudyplanner;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.ImageView;
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

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final String imgRes, int bookId,
                                final int appWidgetId) {

        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BookStudyPlannerWidget.class));

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.book_study_planner_widget);

        //test book
        if(bookId == Utils.INTENT_VAL_BOOK_ID_EMPTY) {
            views.setImageViewResource(R.id.widget_book_image,R.drawable.palmier);
            views.setTextViewText(R.id.widget_title, context.getText(R.string.nothing_today));
        } else {
            views.setTextViewText(R.id.widget_title, context.getText(R.string.your_next_reading));
            //Test String image
            if (imgRes == null) {
                views.setImageViewResource(R.id.widget_book_image, R.drawable.photobook);
            } else {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(context)
                                .load(imgRes)
                                .into(views, R.id.widget_book_image, appWidgetIds);
                    }
                });
            }
        }
        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Utils.INTENT_KEY_WIDGET_PAGE, Utils.INTENT_VAL_WIDGET_PAGE_1);
        intent.putExtra(Utils.INTENT_KEY_WIDGET_BOOK_ID, bookId);
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
                                          String imgRes, int bookId, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, imgRes, bookId, appWidgetId);
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

