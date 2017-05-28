package com.exemple.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Our Content Provider for all movie data
 */
public class MovieProvider extends ContentProvider {

    /* Constants used in the matching URI process */
    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_WITH_ID =101;

    private MovieListDbHelper mMovieHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Our URI Matcher
     *
     * @return A UriMatcher that correctly matches the constants
     * for CODE_MOVIES and CODE_MOVIES_WITH_ID
     */
    public static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieListContract.CONTENT_AUTHORITY;
        /*for a whole directory
         the Uri would looks like : content://com.exemple.android.popularmovies/movies
         */
        uriMatcher.addURI(authority,MovieListContract.PATH_MOVIE,CODE_MOVIES);
     /*   for a specific line in the data base
        the Uri would looks like : content://com.exemple.android.popularmovies/movies/#
        */
        uriMatcher.addURI(authority,MovieListContract.PATH_MOVIE + "/#", CODE_MOVIES_WITH_ID);

        return uriMatcher;
    }

    /**
     * Initialize the provider
     *
     * @return true after the provider is loaded
     */
    @Override
    public boolean onCreate() {

        mMovieHelper = new MovieListDbHelper(getContext());
        return true;
    }

    /**
     * Inserting a set of rows based on a ContentValues[]
     *
     * @param uri URI of the insertion request
     * @param values to be added to the database
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

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

    /**
     * Handles query requests from clients.
     *
     * @param uri The URI to query
     * @param projection The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs selection arguments
     * @param sortOrder How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

//        final SQLiteDatabase db = mMovieHelper.getReadableDatabase();

        Cursor returnCursor;

        switch (sUriMatcher.match(uri)){
            /**
             * In the case CODE_MOVIE we want all the rows of the data base
             */
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
            /**
             * In the case CODE_MOVIES_WHITH_ID we want a specific row of the data base
             * identified by the value in the column movie id, not the auto incremented one
             * but the one that comes from internet
             */
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


    /**
     * Not used in our implementation
     * @param uri uri
     * @return type
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert a single row in the data base
     *
     * @param uri URI of the insertion request
     * @param contentValues to be added to the database
     * @return the uri where the value had been inserted
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mMovieHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES: {

                long id = db.insert(MovieListContract.MovieListEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = MovieListContract.MovieListEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri:" + uri);
            }
        }
        /**
         * Turns out that when keep the getContentResolver().notifyChange() it forces the
         * main activity to reload the adapter with favorite data regardless of the preferences.
         * It could be a desired behaviour but I decided to skip it so user is not forced to see
         * the favorites panel each time he marks a move
         */
//        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;

    }

    /**
     * Delete data at a given URI
     * In our case the only case is the deleting of all the rows
     *
     * @param uri The URI to query
     * @param selection An optional restriction to apply to rows when deleting. Null means
     *                  all the rows will be deleted. But we have to pass "1" in order to
     *                  properly get the number of row deleted.
     *                  Source : SQLiteDatabase documentation
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieHelper.getWritableDatabase();
        int rowDeletedCount;
        if (null == selection){selection = "1";}

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES: {

                rowDeletedCount = db.delete(MovieListContract.MovieListEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case CODE_MOVIES_WITH_ID: {
                String movieId = String.valueOf(ContentUris.parseId(uri));
                rowDeletedCount = db.delete(MovieListContract.MovieListEntry.TABLE_NAME,
                        MovieListContract.MovieListEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movieId});
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

    /**
     * Not used in our implementation
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
