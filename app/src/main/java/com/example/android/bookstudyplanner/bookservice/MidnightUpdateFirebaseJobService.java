/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.bookstudyplanner.bookservice;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MidnightUpdateFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

        @Override
        public boolean onStartJob(final JobParameters params) {
            mBackgroundTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    Context context = MidnightUpdateFirebaseJobService.this;
                    MidnightUpdateTask.executeTask(context, MidnightUpdateTask.ACTION_MIDNIGHT_UPDATE);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    jobFinished(params, false);
                }
            };
            mBackgroundTask.execute();
            return true;
        }

    @Override
    public boolean onStopJob(JobParameters params) {
            if(mBackgroundTask != null) {
                mBackgroundTask.cancel(true);
            }
        return true;
    }

}
