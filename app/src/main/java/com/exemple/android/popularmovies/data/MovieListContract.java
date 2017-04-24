package com.exemple.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;



public class MovieListContract {

    // The authority say wich Content Provider we will access
    public static final String CONTENT_AUTHORITY = "com.exemple.android.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // The path for the "movies" directory
    public static final String PATH_MOVIE = "movies";

    public static final class MovieListEntry implements BaseColumns{

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        // Movies table name and column names

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE= "releaseDate";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_VOTE_AVERAGE ="voteAverage";
        public static final String COLUMN_MOVIE_ID ="movieId";

    }
}
