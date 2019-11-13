
package com.example.android.bookstudyplanner.bookservice;

import android.content.Context;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.AppExecutor;
import com.example.android.bookstudyplanner.database.PlanningEntity;
import com.example.android.bookstudyplanner.uis.TabTodayFragment;

import java.util.Date;
import java.util.List;

class MidnightUpdateTask {

    public static final String ACTION_MIDNIGHT_UPDATE = "ACTION_MIDNIGHT_UPDATE";
    private static AppDatabase mDb;

    // Constant for logging
    private static final String TAG = TabTodayFragment.class.getSimpleName();

    public static void executeTask(Context context, String action) {
        manageMidnightUpdates(context);
    }

    private static void manageMidnightUpdates(Context context) {
        //Update Base
        updateAllOldPlannings(context);

        //launch next update if necessary
        MidnightUtilities.scheduleMidnightUpdate(context);
    }

    private static void updateAllOldPlannings(final Context context) {
        //for all plannings from yesterday
        mDb = AppDatabase.getInstance(context);

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int mBookId;
                Date mDateToday = Utils.getToday();
                Date mDateYesterday = Utils.dateBefore(mDateToday);
                //load plannings
                List<PlanningEntity> listPlannings = mDb.planningDao().loadAllWidgetPlanningsForDate(mDateYesterday, false);

                if(listPlannings != null && !listPlannings.isEmpty()) {
                    for(PlanningEntity planning : listPlannings) {
                        mBookId = planning.getBookId();

                        final int pagesCount = 0;
                        int minutes = 0;
                        final int pageFrom = planning.getFirstPage() - 1;
                        final int pageTo = pageFrom;

                        //PlanningEntity
                        final PlanningEntity planningYesterday = new PlanningEntity(mDateYesterday, mBookId, true, pagesCount, pageFrom, pageTo, minutes);

                        //delete all plannings from yesterday for recalculating
                        mDb.planningDao().deletePlanningByBookIdAfterIncludeDate(mBookId, mDateYesterday);

                        //insert planning for yesterday
                        mDb.planningDao().insertPlanning(planningYesterday);

                        //get data for new plannings after yesterday
                        Integer nbPagesToRead = mDb.bookDao().loadNbPagesToReadBookById(mBookId);
                        Integer nbPagesAlreadyReadBefore = mDb.bookDao().loadNbPagesReadBookById(mBookId);

                        int nbPagesLastToReadAfterToday = nbPagesToRead - nbPagesAlreadyReadBefore ;
                        String mSTabWeekPlanning = mDb.bookDao().loadWeekPlanningById(mBookId);
                        int[] mITabWeekPlanning = Utils.getTabWeekPlanningFromString(mSTabWeekPlanning);
                        int mTotalDaysByWeek = Utils.calculateNbDaysAWeek(mITabWeekPlanning);

                        //mBeginDate = today for next plannings
                        Date mBeginDate = mDateToday;
                        Date mEndDate = mDb.bookDao().loadEndDateById(mBookId);
                        int lastBookPage = mDb.bookDao().loadToPageNbById(mBookId);
                        int mAvgNbSecByPage = context.getResources().getInteger(R.integer.avg_nb_sec_by_page);
                        List<Date> planningDates = Utils.getPlanning(mBeginDate, mEndDate, mITabWeekPlanning);
                        int mNbPagesToReadByDay = Utils.calculateNbPagesAverage(nbPagesLastToReadAfterToday, mBeginDate, mEndDate, mITabWeekPlanning, mTotalDaysByWeek);

                        //insert all other plannings after yesterday
                        int firstPage = pageTo + 1;
                        int lastPage = firstPage + mNbPagesToReadByDay - 1;
                        //inserting new planning
                        int nbMinutesReading = mNbPagesToReadByDay * mAvgNbSecByPage / 60;
                        for (Date d : planningDates) {
                            PlanningEntity p = new PlanningEntity(d, mBookId, false, mNbPagesToReadByDay, firstPage, lastPage, nbMinutesReading);
                            mDb.planningDao().insertPlanning(p);
                            firstPage = lastPage + 1;
                            lastPage = firstPage + mNbPagesToReadByDay - 1;
                            if (lastPage > lastBookPage) {
                                lastPage = lastBookPage;
                                mNbPagesToReadByDay = lastPage - firstPage + 1;
                                nbMinutesReading = mNbPagesToReadByDay * mAvgNbSecByPage / 60;
                            }
                        }
                    }
                    //Update widget
                    WidgetService.startActionUpdateWidgets(context);
                }
            }
        });

    }

}