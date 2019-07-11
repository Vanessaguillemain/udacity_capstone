package com.example.android.bookstudyplanner;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.BookEntity;

/**
 * Created by vanessa on 11/07/2019.
 */

public class AddBookViewModel extends ViewModel {

    private LiveData<BookEntity> book;
    public  AddBookViewModel(AppDatabase appDatabase, int  bookId) {
        book = appDatabase.bookDao().loadBookById(bookId);
    }
    public LiveData<BookEntity> getBook() {
        return book;
    }
}
