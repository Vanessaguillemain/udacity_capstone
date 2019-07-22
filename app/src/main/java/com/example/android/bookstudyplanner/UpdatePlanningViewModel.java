package com.example.android.bookstudyplanner;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.PlanningEntity;

import java.util.Date;

/**
 * Created by vanessa on 11/07/2019.
 */

public class UpdatePlanningViewModel extends ViewModel {

    private LiveData<PlanningEntity> planning;
    public UpdatePlanningViewModel(AppDatabase appDatabase, int  bookId, Date date) {
        planning = appDatabase.planningDao().loadPlanningsForBookAndDate(bookId, date);
    }
    public LiveData<PlanningEntity> getPlanning() {
        return planning;
    }
}
