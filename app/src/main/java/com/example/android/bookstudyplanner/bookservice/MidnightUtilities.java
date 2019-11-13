
package com.example.android.bookstudyplanner.bookservice;


import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.bookstudyplanner.Utils;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class MidnightUtilities {

    private static final int UPDATE_INTERVAL_HOURS = 24;
    private static final int UPDATE_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(UPDATE_INTERVAL_HOURS));
    private static final int SYNC_FLEXTIME_SECONDS = 300;//five minutes

    private static final String UPDATE_JOB_TAG = "update-midnight-tag";

    private static boolean sInitialized; // true if the job has already been launch
    private static boolean sFirstJob = true; // true if the job has already been launch

    synchronized public static void scheduleMidnightUpdate(@NonNull final Context context) {

        if(sFirstJob) {
            long diff = Utils.getDiffForMidnightThirty();

            int startSeconds = (int) (diff / 1000); // tell the start seconds
            int endSeconds = startSeconds + SYNC_FLEXTIME_SECONDS; // within delay

            Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

            //First Job, the first day at Midnight
            Job updatePlanningsAtMidnightJob = dispatcher.newJobBuilder()
                    .setService(MidnightUpdateFirebaseJobService.class)
                    .setTag(UPDATE_JOB_TAG)
                    .setRecurring(false)
                    .setTrigger(Trigger.executionWindow(startSeconds,
                            endSeconds))
                    .setReplaceCurrent(true)
                    .build();
            dispatcher.schedule(updatePlanningsAtMidnightJob);

            sFirstJob = false;
        } else {
            if(sInitialized) return;

            Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

            Job updatePlanningsAtMidnightJob = dispatcher.newJobBuilder()
                    .setService(MidnightUpdateFirebaseJobService.class)
                    .setTag(UPDATE_JOB_TAG)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(UPDATE_INTERVAL_SECONDS,
                            UPDATE_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                    .setReplaceCurrent(true)
                    .build();
            dispatcher.schedule(updatePlanningsAtMidnightJob);
            sInitialized = true;
        }

    }
}
