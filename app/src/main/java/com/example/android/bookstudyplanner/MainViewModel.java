package com.example.android.bookstudyplanner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.BookEntity;
import com.example.android.bookstudyplanner.database.PlanningEntity;

import java.util.List;

/**
 * Created by vanessa on 11/07/2019.
 */

public class MainViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<BookEntity>> books;
    private LiveData<List<BookEntity>> todays;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the books from the DataBase");
        books = database.bookDao().loadAllBooks();
        todays = database.planningDao().loadAllBooksForDate(Utils.getToday());
    }

    public LiveData<List<BookEntity>> getBooks() {
        return books;
    }

    public LiveData<List<BookEntity>> getTodays() {
        return todays;
    }
}
