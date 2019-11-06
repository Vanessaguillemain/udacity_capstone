package com.example.android.bookstudyplanner.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by vanessa on 15/07/2019.
 */
@Entity(tableName = "planning", primaryKeys = {"date", "bookId"})
public class PlanningEntity {
    @NonNull
    private Date date;
    @NonNull
    private int bookId;
    @NonNull
    private boolean done;
    @NonNull
    private Integer nbPagesToRead;
    private Integer nbMinutesReading;
    @NonNull
    private int firstPage;
    @NonNull
    private int lastPage;

    private String title;
    private String imageLink;

    @Ignore
    public PlanningEntity(Date date, int bookId, boolean done, Integer nbPagesToRead, int firstPage, int lastPage, Integer nbMinutesReading) {
        this.date = date;
        this.bookId = bookId;
        this.done = done;
        this.nbPagesToRead = nbPagesToRead;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.nbMinutesReading = nbMinutesReading;
    }


    public PlanningEntity(Date date, int bookId, boolean done, Integer nbPagesToRead, int firstPage, int lastPage, Integer nbMinutesReading, String title, String imageLink) {
        this.date = date;
        this.bookId = bookId;
        this.done = done;
        this.nbPagesToRead = nbPagesToRead;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.nbMinutesReading = nbMinutesReading;
        this.title = title;
        this.imageLink = imageLink;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Integer getNbPagesToRead() {
        return nbPagesToRead;
    }

    public void setNbPagesToRead(Integer nbPagesToRead) {
        this.nbPagesToRead = nbPagesToRead;
    }

    public Integer getNbMinutesReading() {
        return nbMinutesReading;
    }

    public void setNbMinutesReading(Integer nbMinutesReading) {
        this.nbMinutesReading = nbMinutesReading;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    @NonNull
    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(@NonNull int firstPage) {
        this.firstPage = firstPage;
    }

    @NonNull
    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(@NonNull int lastPage) {
        this.lastPage = lastPage;
    }
}
