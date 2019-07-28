package com.example.android.bookstudyplanner.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by vanessa on 10/07/2019.
 */

@Dao
public interface BookDao {

    @Query("SELECT * FROM book")
    LiveData<List<BookEntity>> loadAllBooks();

    @Insert
    long insertBook(BookEntity bookEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBook(BookEntity bookEntry);

    @Query("UPDATE book SET nbPagesRead=:nbPagesRead, percentRead=:percentRead WHERE id = :id")
    void updateBookReadingForBookId(int id, int nbPagesRead, double percentRead);

    @Delete
    void deleteBook(BookEntity bookEntry);

    @Query("DELETE FROM book WHERE id = :id")
    void deleteBookById(int id);

    @Query("SELECT * FROM book WHERE id = :id")
    LiveData<BookEntity> loadBookById(int id);

    @Query("SELECT nbPagesToRead FROM book WHERE id = :id")
    Integer loadNbPagesToReadBookById(int id);

    @Query("SELECT nbPagesRead FROM book WHERE id = :id")
    Integer loadNbPagesReadBookById(int id);

    @Query("SELECT percentRead FROM book WHERE id = :id")
    Double loadPercentReadBookById(int id);

    @Query("SELECT * FROM book WHERE isbn = :isbn")
    BookEntity loadBookByIsbn(int isbn);

}