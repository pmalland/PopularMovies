package com.exemple.android.popularmovies.utilities;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    /*Used in Log*/
    // private static final String TAG = NetworkUtils.class.getSimpleName();

     /*Used for the internet request*/
    private final static String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3";
    private final static String PARAM_POPULAR ="/movie/popular";
    private final static String PARAM_RATE = "/movie/top_rated";
    private final static String POPULAR_QUERY_BASE_URL = THEMOVIEDB_BASE_URL + PARAM_POPULAR ;
    private final static String RATE_QUERY_BASE_URL = THEMOVIEDB_BASE_URL + PARAM_RATE;
    private final static String PARAM_API_KEY = "api_key";

    private final static String POPULAR_KEY = "popular";
    private final static String RATE_KEY= "rate";

    private final static String API_KEY_VALUE = "";

    /*Used for the poster request*/

    private final static String POSTER_BASE_URL= "http://image.tmdb.org/t/p/";

    /**
     * Building the proper URL to query movies data.
     *
     * @param REQUEST_KEY indicate which one between order by popularity and order by rate
     *                    the user chose.
     * @return URL to query movies data
     */
    public static URL buildUrl(String REQUEST_KEY){
        String QUERY_BASE_URL = null;
        if(REQUEST_KEY.equals(POPULAR_KEY)){
            QUERY_BASE_URL = POPULAR_QUERY_BASE_URL;
        } else if (REQUEST_KEY.equals(RATE_KEY)){
            QUERY_BASE_URL = RATE_QUERY_BASE_URL;
        }
        Uri ourBuiltUri = Uri.parse(QUERY_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY,API_KEY_VALUE)
                .build();

        URL url = null;

        try{
            url= new URL(ourBuiltUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }

//        Log.v(TAG, "Built URI " + url);
        return url;
    }

    /**
     * Building the proper URL to query for a movie's poster
     *
     * @param posterPath final part of the path needed to build the URL, unique to
     *                   each movie
     * @param posterSize part of the path that indicate the resolution we want for the poster.
     *                   That part comes right before the posterPath
     * @return URL to query movie poster
     */
    public static URL buildURL(String posterPath, String posterSize){

        Uri ourBuiltUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(posterSize)
                .appendEncodedPath(posterPath)
                .build();

        URL url = null;

        try{
            url = new URL(ourBuiltUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

//        Log.v(TAG, "Built URI " + url);
        return url;
    }

    /**
     * Returns the entire result from the http response in the form of a String
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        /* Open a connection.
         * Use a scanner for handling the input and cast it into a String.
         * Close the connection.
         * This is were the permission "INTERNET" is needed
         * */
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
           scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if(hasInput){
                response = scanner.next();
            }
            scanner.close();
            return response;
        }finally {
            urlConnection.disconnect();
       }

   }
}




