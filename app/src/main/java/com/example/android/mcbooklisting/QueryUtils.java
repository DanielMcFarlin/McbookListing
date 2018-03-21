package com.example.android.mcbooklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    //Set up a Error_LOG_TAG for trouble shooting
    public static String Error_LOG_TAG = QueryUtils.class.getSimpleName();

    //The GOOGLE HTTP/URL Query (Where the list info is coming from)
    static String GOOGLE_URL = "https://www.googleapis.com/books/v1/volumes?q=search+";

    public static List<Book> getBookData(String searchedText) {

        //Creates a new URL from the info in the search bar
        URL url = createURL(searchedText);
        String jsonResponse = null;

        //Try to make the HTTPRequest here but if there is a URL error it will be seen here
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(Error_LOG_TAG, "Error in input stream", e);
        }

        //The List<Book> gets info from the JSONResponse then return as list of books info
        return outOfJson(jsonResponse);
    }

    private static URL createURL(String searchedText) {

        //Replace the info between the added parts to URL to "+" to create a valid URL
        URL url = null;
        String SpecifiedURL = searchedText.trim().replaceAll("\\s+", "+");
        try {
            //Put the additional parts of the URL from the search to narrow it down to more specific
            url = new URL(GOOGLE_URL + SpecifiedURL);
        } catch (MalformedURLException e) {
            Log.e(Error_LOG_TAG, "Error creating URL ", e);
        }
        //If the URL is invalid, this return will simply be NULL or EMPTY
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return.
        if (url == null) {
            return jsonResponse;
        }

        //Now the the URL is checked, request HTTP and make connection for info
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //Try to connect and open connection
            connection = (HttpURLConnection) url.openConnection();
            //Requesting a "GET" because we are retrieving info from GOOGLE's Servers
            connection.setRequestMethod("GET");
            //Finally Connected!
            connection.connect();

            // If the request was successful (aka. code 200), then read the input stream and parse it.
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = fromStream(inputStream);
                //If there is a problem with connecting, then display the repsonse code here
            } else {
                Log.e(Error_LOG_TAG, "Error response code: " + connection.getResponseCode());
            }
            //If there is a problem with parsing the JSON throw this error
        } catch (IOException e) {
            Log.e(Error_LOG_TAG, "Problem retrieving JSON results on Books.", e);
            //If there there was a connection disconnect now and close the stream
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //Handling the input stream as according to the Udacity Videos
    private static String fromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Book> outOfJson(String bookJSON) {

        //Create new List to fill with the info from JSON
        List<Book> bookList = new ArrayList<>();
        // If the JSON string is empty or null, the return.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        try {
            //convert the JSON response (string) into JSONObject
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            int count = baseJsonResponse.getInt("totalItems");
            //If the count is "0" this means there were not books found
            if (count == 0) {
                return null;
            }

            JSONArray booksArray = baseJsonResponse.getJSONArray("items");

            //loop through the bookArray and retrieve the info needed for each book in JSON
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject currentBook = booksArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String Title = volumeInfo.getString("title");
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                String totalAuthors = getAllAuthors(authorsArray);
                String Date = volumeInfo.getString("publishedDate");

                //Add the new book to the bookList
                Book book = new Book(Title, totalAuthors, Date);
                bookList.add(book);

            }
        } catch (JSONException e) {
            Log.e(Error_LOG_TAG, "Problem parsing the Book JSON results", e);
        }
        return bookList;
    }

    // Method used to take out the author info from the JSONArray and place it into string format
    private static String getAllAuthors(JSONArray authorsArray) throws JSONException {

        StringBuilder authorsList = null;

        if (authorsArray.length() == 0)
            authorsList = new StringBuilder("No Author Found");

        for (int i = 0; i < authorsArray.length(); i++) {
            if (i == 0)
                authorsList = new StringBuilder("- " + authorsArray.getString(0));
            else
                authorsList.append(", ").append(authorsArray.getString(i));
        }

        assert authorsList != null;
        return authorsList.toString();
    }

}
