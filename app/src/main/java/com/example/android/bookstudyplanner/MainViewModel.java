package com.example.android.bookstudyplanner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.BookEntity;

import java.util.List;

/**
 * Created by vanessa on 11/07/2019.
 */

public class MainViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<BookEntity>> books;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        books = database.bookDao().loadAllBooks();
    }

    public LiveData<List<BookEntity>> getBooks() {
        return books;
    }
}
