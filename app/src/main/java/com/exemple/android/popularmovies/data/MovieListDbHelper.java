package com.exemple.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MovieListDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movielist.db";

    private static final int DATABASE_VERSION = 1;

    public MovieListDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIELIST_TABLE = "CREATE TABLE " +
                MovieListContract.MovieListEntry.TABLE_NAME + " (" +
                MovieListContract.MovieListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieListContract.MovieListEntry.COLUMN_POSTER_PATH + " TEXT," +
                MovieListContract.MovieListEntry.COLUMN_OVERVIEW + " TEXT," +
                MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE + " TEXT," +
                MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE + " TEXT," +
                MovieListContract.MovieListEntry.COLUMN_TITLE + " TEXT," +
                MovieListContract.MovieListEntry.COLUMN_VOTE_AVERAGE + " INTEGER" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIELIST_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
