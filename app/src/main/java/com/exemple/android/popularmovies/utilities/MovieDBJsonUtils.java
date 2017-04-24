package com.exemple.android.popularmovies.utilities;


import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDBJsonUtils {

    private static final String MDB_PAGE = "page";
    private static final String MDB_RESULTS = "results";

    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_ADULT = "adult";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_RELEASE_DATE = "release_date";
    private static final String MDB_GENRE_IDS = "genre_ids";
    private static final String MDB_MOVIE_ID = "movie_id";
    private static final String MDB_ORIGINAL_TITLE = "original_title";
    private static final String MDB_ORIGINAL_LANGUAGE = "original_language";
    private static final String MDB_TITLE = "title";
    private static final String MDB_BACKDROP_PATH = "backdrop_path";
    private static final String MDB_POPULARITY = "popularity";
    private static final String MDB_VOTE_COUNT = "vote_count";
    private static final String MDB_VIDEO = "video";
    private static final String MDB_VOTE_AVERAGE = "vote_average";

    private static final String MDB_TOTAL_RESULTS = "total_results";
    private static final String MDB_TOTAL_PAGES = "total_pages";


    public static String[] getMoviePathToPosterFromJson(Context context, String movieDBJsonStr)
        throws JSONException {

        JSONObject movieDBJson = new JSONObject(movieDBJsonStr);

        //What would be an unambiguous flag for an error JSON ?

        JSONArray jsonMovieArray = movieDBJson.getJSONArray(MDB_RESULTS);

        String[] movieDBPathToPoster = new String[jsonMovieArray.length()];

        for (int i = 0; i < jsonMovieArray.length(); i++){
            String posterPath;

            JSONObject movie = jsonMovieArray.getJSONObject(i);

            posterPath = movie.getString(MDB_POSTER_PATH);
            movieDBPathToPoster[i] = posterPath;
        }
        return movieDBPathToPoster;
    }

    public static ContentValues[] getMovieContentValuesFromJson(Context context, String movieDBJsonStr)
        throws JSONException {

        JSONObject movieDBJson = new JSONObject(movieDBJsonStr);

        // What would be an unambiguous flag to detect an error JSON ?

        JSONArray jsonMovieArray = movieDBJson.getJSONArray(MDB_RESULTS);

        ContentValues[] movieDBContentValues = new ContentValues[jsonMovieArray.length()];

        for (int i = 0; i < jsonMovieArray.length(); i++) {

            String posterPath;
            String overview;
            String release_date;
            String originalTitle;
            double voteAverage;
            int movieId;

            /*Get the JSON object representing the movie */
            JSONObject movie = jsonMovieArray.getJSONObject(i);

            posterPath = movie.getString(MDB_POSTER_PATH);
            overview = movie.getString(MDB_OVERVIEW);
            release_date = movie.getString(MDB_RELEASE_DATE);
            originalTitle = movie.getString(MDB_ORIGINAL_TITLE);
            voteAverage = movie.getDouble(MDB_VOTE_AVERAGE);
            movieId = movie.getInt(MDB_MOVIE_ID);

            ContentValues movieValues = new ContentValues();
            movieValues.put(MDB_POSTER_PATH,posterPath);
            movieValues.put(MDB_OVERVIEW,overview);
            movieValues.put(MDB_RELEASE_DATE,release_date);
            movieValues.put(MDB_ORIGINAL_TITLE,originalTitle);
            movieValues.put(MDB_VOTE_AVERAGE,voteAverage);
            movieValues.put(MDB_MOVIE_ID,movieId);

            movieDBContentValues[i] = movieValues;
        }
    return movieDBContentValues;
    }

}
