package com.example.android.bookstudyplanner.database;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.bookstudyplanner.AddBookViewModel;

/**
 * Created by vanessa on 11/07/2019.
 */

public class AddBookViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mTaskId;

    public AddBookViewModelFactory(AppDatabase appDatabase, int bookId) {
        mDb = appDatabase;
        mTaskId = bookId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AddBookViewModel(mDb, mTaskId);
    }
}
