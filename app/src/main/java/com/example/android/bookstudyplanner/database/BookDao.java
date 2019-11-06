package com.example.android.bookstudyplanner.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
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

    @Query("UPDATE book SET nbPagesRead=:nbPagesRead, percentRead=:percentRead, lastPageRead=:lastPageRead  WHERE id = :id")
    void updateBookReadingForBookId(int id, int nbPagesRead, double percentRead, int lastPageRead);

    @Delete
    void deleteBook(BookEntity bookEntry);

    @Query("DELETE FROM book WHERE id = :id")
    void deleteBookById(int id);

    @Query("SELECT * FROM book WHERE id = :id")
    LiveData<BookEntity> loadBookById(int id);

    @Query("SELECT * FROM book WHERE id = :id")
    BookEntity loadBookEntityById(int id);

    @Query("SELECT nbPagesToRead FROM book WHERE id = :id")
    Integer loadNbPagesToReadBookById(int id);

    @Query("SELECT nbPagesRead FROM book WHERE id = :id")
    Integer loadNbPagesReadBookById(int id);

    @Query("SELECT weekPlanning FROM book WHERE id = :id")
    String loadWeekPlanningById(int id);

    @Query("SELECT beginDate FROM book WHERE id = :id")
    Date loadBeginDateById(int id);

    @Query("SELECT endDate FROM book WHERE id = :id")
    Date loadEndDateById(int id);

    @Query("SELECT readTimeInSeconds FROM book WHERE id = :id")
    Integer loadReadTimeInSecondsById(int id);

    @Query("SELECT toPageNb FROM book WHERE id = :id")
    Integer loadToPageNbById(int id);

    @Query("UPDATE book SET nbPagesRead=0, percentRead=0, lastPageRead=0")
    void updateAllBooksEraseReadings();

    @Query("SELECT percentRead FROM book WHERE id = :id")
    Double loadPercentReadBookById(int id);

    @Query("SELECT * FROM book WHERE isbn = :isbn")
    BookEntity loadBookByIsbn(int isbn);

}