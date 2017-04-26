/*
* Hi,
* My name is Paul Malland from Belgium. I build this app on the context of my nanodegree in Android.
* Courses are provided by Udacity and sponsored by Google All Mighty.
* This is project 1
*
* */


package com.exemple.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.popularmovies.data.MovieListContract;
import com.exemple.android.popularmovies.data.MoviePreferences;
import com.exemple.android.popularmovies.utilities.MovieDBJsonUtils;
import com.exemple.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;



public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{


//    Number of columns handled by the Grid Layout Manager
    private static final int SPAN_COUNT = 3;

    private MovieAdapter mMovieAdapter;

    private RecyclerView mMovieListRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;



    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    private Toast mToast;

    // key for saved instance
    private static final String LIFECYCLE_CALLBACKS_TEXT_KEY = "callbacks";

    // Main Loader ID
    private static final int ID_MOVIE_LOADER = 23;

    // reference to the DB
    SQLiteDatabase mDb;

    /*********************
     ** MAIN PROJECTION **
     *********************/
    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieListContract.MovieListEntry.COLUMN_POSTER_PATH,
            MovieListContract.MovieListEntry.COLUMN_MOVIE_ID,
    };

    /***********
     ** INDEX **
     ***********/
    public static final int INDEX_MOVIE_POSTER = 0;
    public static final int INDEX_MOVIE_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mMovieListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movie);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),SPAN_COUNT);

        mMovieListRecyclerView.setLayoutManager(layoutManager);
        mMovieListRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);

        mMovieListRecyclerView.setAdapter(mMovieAdapter);

        mErrorMessageTextView = (TextView) findViewById(R.id.error_message_tv);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator_pb);
   ;
   //     MovieListDbHelper dbHelper = new MovieListDbHelper(this);

       loadMovieData(MoviePreferences.getPreferredSortingCriterion(this));

        showLoadingIndicator();

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER,null,this);


    }


    private void loadMovieData(String queryKey){
      //  showDataView();
        URL theMovieDBSearchURL = NetworkUtils.buildUrl(queryKey);
        new FetchMovieTask().execute(theMovieDBSearchURL);


    }

    private void showDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieListRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loadArgs) {

        switch (loaderId){
            case ID_MOVIE_LOADER:
                Uri movieQueryUri = MovieListContract.MovieListEntry.CONTENT_URI;

                String sortOrder = null;
                String selection = null;

                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        selection,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader not implemented : " + loaderId);
        }
/*
        return new AsyncTaskLoader<String[]>(this) {

            String[] mMovieData = null;

            @Override
            protected void onStartLoading() {

                if(mMovieData != null){
                    deliverResult(mMovieData);
                }else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }

            @Override
            public String[] loadInBackground() {
                Context context = MainActivity.this;
                String queryCriterion = MoviePreferences.getPreferredSortingCriterion(context);
                URL theMovieDBSearchURL = NetworkUtils.buildUrl(queryCriterion);
                String jsonMovieDBResults = null;

                try {
                    jsonMovieDBResults = NetworkUtils.getResponseFromHttpUrl(theMovieDBSearchURL);
                    String[] simplePathToPosterList = MovieDBJsonUtils.getMoviePathToPosterFromJson(context, jsonMovieDBResults);
                    return simplePathToPosterList;
                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void deliverResult(String[] data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        String posterResolution = MoviePreferences.getPreferredPosterResolution(MainActivity.this);
        mMovieAdapter.swapCursor(data, posterResolution);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        mMovieListRecyclerView.smoothScrollToPosition(mPosition);

        if (data.getCount() != 0) showDataView();


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        String posterResolution = MoviePreferences.getPreferredPosterResolution(MainActivity.this);
        mMovieAdapter.swapCursor(null, posterResolution);
    }
    private void invalidateDate(){
        String posterResolution = MoviePreferences.getPreferredPosterResolution(MainActivity.this);
        mMovieAdapter.swapCursor(null, posterResolution);
    }

    public class FetchMovieTask extends AsyncTask<URL,Void,String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(URL... params) {
            URL searchURL = params[0];
            String jsonMovieDBResults = null;
            try {
                jsonMovieDBResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
                String[] simplePathToPosterList = MovieDBJsonUtils.getMoviePathToPosterFromJson(MainActivity.this, jsonMovieDBResults);
                ContentValues[] dataFromJson = MovieDBJsonUtils.getMovieContentValuesFromJson(MainActivity.this, jsonMovieDBResults);
                return simplePathToPosterList;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String[] moviePathToPosterListStr) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(moviePathToPosterListStr != null){
                showDataView();

              //  mMovieAdapter.setPathToPoster(moviePathToPosterListStr,"w342");

            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.movie,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWhatSelected = item.getItemId();
        Context context = MainActivity.this;
        String message = null;
        if(menuItemThatWhatSelected == R.id.action_sort_by_popularity){

            MoviePreferences.setPreferredSortingCriterion(context,getString(R.string.criterion_popular));
            message = getString(R.string.toast_popular_sort);
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();

        }else if(menuItemThatWhatSelected == R.id.action_sort_by_rate){

            MoviePreferences.setPreferredSortingCriterion(context,getString(R.string.criterion_most_rated));
            message = getString(R.string.toast_rate_sort);
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();

        }
        mMovieAdapter = new MovieAdapter(this,this);
        mMovieListRecyclerView.setAdapter(mMovieAdapter);
        // restarting the loader
        getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER,null,this);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int movieIdInteger) {
        String movieIdString = Integer.toString(movieIdInteger);
        if(mToast != null){
            mToast.cancel();
        }

        String toastMessage = "movie ID :" + movieIdString + " selected";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);

        mToast.show();


        Uri detailUri = MovieListContract.MovieListEntry.CONTENT_URI.buildUpon()
                .appendPath(movieIdString)
                .build();

        Intent startDetailActivityIntent = new Intent(MainActivity.this, DetailActivity.class);

        startDetailActivityIntent.setData(detailUri);

        startActivity(startDetailActivityIntent);
    }

    private void showLoadingIndicator(){
        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

}
