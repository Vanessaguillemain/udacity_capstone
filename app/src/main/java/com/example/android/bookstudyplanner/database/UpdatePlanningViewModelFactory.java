package com.example.android.bookstudyplanner.database;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.bookstudyplanner.UpdatePlanningViewModel;

import java.util.Date;

/**
 * Created by vanessa on 11/07/2019.
 */

public class UpdatePlanningViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mBookId;
    private final Date mDate;

    public UpdatePlanningViewModelFactory(AppDatabase appDatabase, int bookId, Date date) {
        mDb = appDatabase;
        mBookId = bookId;
        mDate = date;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UpdatePlanningViewModel(mDb, mBookId, mDate);
    }
}
