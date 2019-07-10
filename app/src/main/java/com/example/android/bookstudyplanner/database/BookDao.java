package com.example.android.bookstudyplanner.database;

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
    List<BookEntity> loadAllBooks();

    @Insert
    void insertBook(BookEntity bookEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBook(BookEntity bookEntry);

    @Delete
    void deleteBook(BookEntity bookEntry);

    @Query("SELECT * FROM book WHERE id = :id")
    BookEntity loadBookById(int id);

    @Query("SELECT * FROM book WHERE isbn = :isbn")
    BookEntity loadBookByIsbn(int isbn);

}