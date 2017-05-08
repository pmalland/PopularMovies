package com.exemple.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_WITH_ID =101;

    private MovieListDbHelper mMovieHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieListContract.CONTENT_AUTHORITY;
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

        mMovieHelper = new MovieListDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mMovieHelper.getWritableDatabase();


        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                db.beginTransaction();
                int rowInsertedCount = 0;
                try{

                    for (ContentValues value : values){
                        long id = db.insert(MovieListContract.MovieListEntry.TABLE_NAME,null,value);
                        if ( id != -1){
                            rowInsertedCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowInsertedCount > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowInsertedCount;



            default:
                return super.bulkInsert(uri, values);
        }


    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

//        final SQLiteDatabase db = mMovieHelper.getReadableDatabase();

        Cursor returnCursor;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:{
                returnCursor = mMovieHelper.getReadableDatabase().query(MovieListContract.MovieListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_MOVIES_WITH_ID:{
                String movieIdentifier = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{movieIdentifier};
                returnCursor = mMovieHelper.getReadableDatabase().query(
                        MovieListContract.MovieListEntry.TABLE_NAME,
                        projection,
                        MovieListContract.MovieListEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieHelper.getWritableDatabase();
        int rowDeletedCount = 0;
        if (null == selection){selection = "1";}

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES: {

                rowDeletedCount = db.delete(MovieListContract.MovieListEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowDeletedCount != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowDeletedCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
