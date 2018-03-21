package com.example.android.mcbooklisting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    //Using ButterKnife library to reduce repetitious code
    @BindView(R.id.search_input)
    android.support.v7.widget.SearchView mSearchInput;
    @BindView(R.id.no_internet)
    TextView mNoInternet;
    @BindView(R.id.no_books)
    TextView mNoBooks;
    @BindView(R.id.progress_bar)
    ProgressBar mProgress;

    //BookAdapter used to create Book<List>
    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Unbinder unbinder = ButterKnife.bind(this);

        adapter = new BookAdapter(this);
        mSearchInput.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                View v = getCurrentFocus();
                if (v != null) {
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert im != null;
                    im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                adapter.clear();

                //Show error message if the searchButton is clicked with no input entered
                if (query.toString().trim().matches("")) {
                    mNoBooks.setVisibility(View.VISIBLE);
                    mNoInternet.setVisibility(View.GONE);
                    mNoBooks.setText(R.string.no_search);
                    //Continue if the network is connected AND something is in the SearchInput
                } else {
                    if (isNetworkConnected()) {
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute();
                        //if the internet is not connected,
                        // but there is an something in SearchInput
                    } else {
                        mNoInternet.setVisibility(View.VISIBLE);
                        mNoBooks.setVisibility(View.GONE);
                    }
                }

                //Put the booklist into the the designed ListView by ID  "list_items"
                ListView bookListView = (ListView) findViewById(R.id.list_items);
                bookListView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    //Method used to check Network Connectivity of device being used
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    // Method used to get the text from the SearchInput and translate to String
    public String getText() {
        return mSearchInput.getQuery().toString();
    }


    //AsyncTask used clear out the main thread or (UI)
    @SuppressLint("StaticFieldLeak")
    private class BookAsyncTask extends AsyncTask<Void, Void, List<Book>> {

        @Override
        protected void onPreExecute() {
            // Hide No Internet and No Books Message
            mNoInternet.setVisibility(View.INVISIBLE);
            mNoBooks.setVisibility(View.INVISIBLE);
            // Show the Loading Progress
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Book> doInBackground(Void... voids) {
            //Get the Book data, get the text (string) and return that as result
            return QueryUtils.getBookData(getText());
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            // Hide the progress bar
            mProgress.setVisibility(View.INVISIBLE);

            if (books == null) {
                mNoBooks.setVisibility(View.VISIBLE);
            } else {
                mNoBooks.setVisibility(View.GONE);
                adapter.addAll(books);
            }
        }
    }
}
