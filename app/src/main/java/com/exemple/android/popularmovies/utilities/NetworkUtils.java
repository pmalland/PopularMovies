package com.exemple.android.popularmovies.utilities;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
//    Used in Log
    private static final String TAG = NetworkUtils.class.getSimpleName();

    //    Used for the movie DB request
    private final static String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3";

    private final static String PARAM_POPULAR ="/movie/popular";
    private final static String PARAM_RATE = "/movie/top_rated";
    private final static String POPULAR_QUERY_BASE_URL = THEMOVIEDB_BASE_URL + PARAM_POPULAR ;
    private final static String RATE_QUERY_BASE_URL = THEMOVIEDB_BASE_URL + PARAM_RATE;
    private final static String PARAM_API_KEY = "api_key";

    private final static int POPULAR_KEY = 100;
    private final static int RATE_KEY= 110;

    private final static String API_KEY_VALUE = "";

//    Used for the poster request

    private final static String POSTER_BASE_URL= "http://image.tmdb.org/t/p/";


    public static URL buildUrl(int REQUEST_KEY){
        String QUERY_BASE_URL = null;
        if(REQUEST_KEY == POPULAR_KEY){
            QUERY_BASE_URL = POPULAR_QUERY_BASE_URL;
        } else if (REQUEST_KEY == RATE_KEY){
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

    public static String getResponseFromHttpUrl(URL url) throws IOException {

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




