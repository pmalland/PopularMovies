package com.exemple.android.popularmovies.data;

import android.provider.BaseColumns;



public class MovieListContract {


    public class MovieListEntry implements BaseColumns{

        public static final String TABLE_NAME = "movieList";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE= "releaseDate";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_TITLE ="title";
        public static final String COLUMN_VOTE_AVERAGE ="voteAverage";
    }
}
