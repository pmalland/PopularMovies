package com.exemple.android.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MovieListContract {

    /*The authority say wich Content Provider we will access*/
    public static final String CONTENT_AUTHORITY = "com.exemple.android.popularmovies";

     /*The base content URI = "content://" + <authority>*/
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*The path for the "movies" directory*/
    public static final String PATH_MOVIE = "movies";

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieListEntry implements BaseColumns{

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        /*Movies table name and column names*/
        /*Used internally as the name of our movie table*/
        public static final String TABLE_NAME = "movies";
        /*Stored as String in data base. It represents a part of the path to the poster URL*/
        public static final String COLUMN_POSTER_PATH = "posterPath";
        /*Stored as String in data base. It contains the movie's summary*/
        public static final String COLUMN_OVERVIEW = "overview";
        /*Stored as String in data base. It represents the release date of the movie*/
        public static final String COLUMN_RELEASE_DATE= "releaseDate";
        /*Stored as String in data base.It contains the original title of the movie*/
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        /*Stored as an integer in data base.It represents the average rating of the movie*/
        public static final String COLUMN_VOTE_AVERAGE ="voteAverage";
        /*Stored as an integer in data base. It contains the movie ID on internet*/
        public static final String COLUMN_MOVIE_ID ="movieId";


        public static final Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);

            /**
             * Or it could be without the ContentUris class. Can't decide if it's better
             * or not.
             */
//            String movieIdString = Long.toString(id);
//            Uri detailUri = MovieListContract.MovieListEntry.CONTENT_URI.buildUpon()
//                    .appendPath(movieIdString)
//                    .build();

        }
    }


}
