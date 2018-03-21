package com.example.android.mcbooklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Set up a custom adapter to for the Book List
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Initialize ViewHolder
        ViewHolder vHolder;

        //If the view is empty, then inflate the new list
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.book_list_items, parent, false);

            //Set up the ViewHolder store/find the views by ID
            vHolder = new ViewHolder();
            vHolder.title = (TextView) view.findViewById(R.id.book_name);
            vHolder.author = (TextView) view.findViewById(R.id.author_name);
            vHolder.date = (TextView) view.findViewById(R.id.publish_date);

            // Set the tag for the ViewHolder.
            view.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) view.getTag();
        }
        //Get info on the book selected
        Book currentBook = getItem(position);

        if (currentBook != null) {
            //Set the text to the views found by their ID's
            vHolder.title.setText(currentBook.getTitle());
            vHolder.author.setText(currentBook.getAuthors());
            vHolder.date.setText(currentBook.getDate());
        }

        return view;
    }

    // Creating A ViewHolder
    static class ViewHolder {
        TextView title;
        TextView author;
        TextView date;
    }
}