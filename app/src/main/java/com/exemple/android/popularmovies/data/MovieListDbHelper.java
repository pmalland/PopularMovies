package com.exemple.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Managing a data base for movie data
 */

public class MovieListDbHelper extends SQLiteOpenHelper {

    /* Name of the data base*/
    private static final String DATABASE_NAME = "movielist.db";

    /* Data base version */
    private static final int DATABASE_VERSION = 6;

    public MovieListDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /**
     * Creation of the table
     *
     * @param sqLiteDatabase the data base
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIELIST_TABLE = "CREATE TABLE " +
                MovieListContract.MovieListEntry.TABLE_NAME + " (" +
                MovieListContract.MovieListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieListContract.MovieListEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MovieListContract.MovieListEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL," +
                MovieListContract.MovieListEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                MovieListContract.MovieListEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIELIST_TABLE);


    }

    /**
     * Discard the data and call onCreate to recreate the table.
     *
     * @param sqLiteDatabase Database that been upgraded
     * @param i the old version
     * @param i1 the new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListContract.MovieListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
