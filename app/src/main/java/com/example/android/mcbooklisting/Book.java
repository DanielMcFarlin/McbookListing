package com.example.android.mcbooklisting;

public class Book {

    //Title of the book
    private String mTitle;
    //Author(s) of the book
    private String mAuthors;
    //Date the book was published
    private String mDatePublished;

    /**
     * @param title   is the title of the book
     * @param authors is the author(s) of the book
     * @param date    is the title of the book
     */
    public Book(String title, String authors, String date) {
        mTitle = title;
        mAuthors = authors;
        mDatePublished = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getDate() {
        return mDatePublished;
    }
}
