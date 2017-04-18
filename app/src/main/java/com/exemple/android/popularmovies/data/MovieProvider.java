package com.exemple.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_WITH_ID =101;

    private MovieListDbHelper mMovieHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieListContract.CONTENT_AUTHORITY;
        /*for a whole directory
         the Uri would looks like : content://com.exemple.android.popularmovies/movies
         */
        uriMatcher.addURI(authority,MovieListContract.PATH_MOVIE,CODE_MOVIES);
     /*   for a specific line
        the Uri would looks like : content://com.exemple.android.popularmovies/movies/#
        */
        uriMatcher.addURI(authority,MovieListContract.PATH_MOVIE + "/#", CODE_MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieHelper = new MovieListDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mMovieHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match){
            case CODE_MOVIES:
                returnCursor = db.query(MovieListContract.MovieListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}