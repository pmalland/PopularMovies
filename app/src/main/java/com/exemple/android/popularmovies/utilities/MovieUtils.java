package com.exemple.android.popularmovies.utilities;

import android.content.ContentValues;
import android.database.Cursor;

import com.exemple.android.popularmovies.data.Movie;
import com.exemple.android.popularmovies.data.MovieListContract;

import java.util.ArrayList;

/**
 * Created by paulm on 24-05-17.
 */

public class MovieUtils {

    public static ArrayList<Movie> getArrayListFromCursor(Cursor data){

        ArrayList<Movie> movieArrayList = new ArrayList<Movie>();
        int posterPathIndex = data
                .getColumnIndexOrThrow(MovieListContract.MovieListEntry.COLUMN_POSTER_PATH);
        int overviewIndex = data
                .getColumnIndexOrThrow(MovieListContract.MovieListEntry.COLUMN_OVERVIEW);
        int release_dateIndex = data
                .getColumnIndexOrThrow(MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE);
        int originalTitleIndex = data
                .getColumnIndexOrThrow(MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE);
        int voteAverageIndex = data
                .getColumnIndexOrThrow(MovieListContract.MovieListEntry.COLUMN_VOTE_AVERAGE);
        int movieIdIndex = data
                .getColumnIndexOrThrow(MovieListContract.MovieListEntry.COLUMN_MOVIE_ID);

        while (data.moveToNext()){
            Movie currentMovie = new Movie();

            currentMovie.setPosterPath(data.getString(posterPathIndex));
            currentMovie.setOverview(data.getString(overviewIndex));
            currentMovie.setReleaseDate(data.getString(release_dateIndex));
            currentMovie.setOriginalTitle(data.getString(originalTitleIndex));
            currentMovie.setVoteAverage(data.getDouble(voteAverageIndex));
            currentMovie.setMovieId(data.getLong(movieIdIndex));

            movieArrayList.add(currentMovie);
        }

        return movieArrayList;
    }

    public static ContentValues getContentValuesFromMovie(Movie movie){

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieListContract.MovieListEntry.COLUMN_POSTER_PATH,movie.getPosterPath());
        movieValues.put(MovieListContract.MovieListEntry.COLUMN_OVERVIEW,movie.getOverview());
        movieValues.put(MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE,movie.getReleaseDate());
        movieValues.put(MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE,movie.getOriginalTitle());
        movieValues.put(MovieListContract.MovieListEntry.COLUMN_VOTE_AVERAGE,movie.getVoteAverage());
        movieValues.put(MovieListContract.MovieListEntry.COLUMN_MOVIE_ID,movie.getMovieId());
        return movieValues;
    }
}
