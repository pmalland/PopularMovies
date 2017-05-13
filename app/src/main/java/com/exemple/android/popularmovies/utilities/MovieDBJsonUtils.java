package com.exemple.android.popularmovies.utilities;


import android.content.ContentValues;

import com.exemple.android.popularmovies.data.MovieListContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDBJsonUtils {

    private static final String MDB_RESULTS = "results";

    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_RELEASE_DATE = "release_date";
    private static final String MDB_MOVIE_ID = "id";
    private static final String MDB_ORIGINAL_TITLE = "original_title";
    private static final String MDB_VOTE_AVERAGE = "vote_average";


    /**
     * Parsing the raw HTTP result into a JSON, we can access the movies data
     * and store them in a convenient ContentValues []
     *
     * @param movieDBJsonStr raw response from the server in a string
     * @return the structured data in a ContentValues^$
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getMovieContentValuesFromJson(String movieDBJsonStr)
        throws JSONException {

        JSONObject movieDBJson = new JSONObject(movieDBJsonStr);

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
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_POSTER_PATH,posterPath);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_OVERVIEW,overview);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE,release_date);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE,originalTitle);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_VOTE_AVERAGE,voteAverage);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_MOVIE_ID,movieId);

            movieDBContentValues[i] = movieValues;
        }
    return movieDBContentValues;
    }

}
