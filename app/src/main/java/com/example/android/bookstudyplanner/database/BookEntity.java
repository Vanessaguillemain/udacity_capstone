package com.example.android.bookstudyplanner.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by vanessa on 10/07/2019.
 */
@Entity (tableName = "book")
public class BookEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int isbn;
    private String title;
    private int pageCount;

    public BookEntity(int isbn, String title, int pageCount) {
        this.isbn = isbn;
        this.title = title;
        this.pageCount = pageCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public String toString() {
        return "BookEntity{" +
                "id=" + id +
                ", isbn=" + isbn +
                ", title='" + title + '\'' +
                ", pageCount=" + pageCount +
                '}';
    }
}
