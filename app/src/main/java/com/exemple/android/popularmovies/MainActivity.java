package com.exemple.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.exemple.android.popularmovies.data.MoviePreferences.getPreferredSortingCriterion;

/*
* Hi,
* My name is Paul Malland from Belgium. I build this app on the context of my nanodegree in Android.
* Courses are provided by Udacity and sponsored by Google All Mighty.
* This is Popular Movies, Stage 1
*
* */

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private MovieAdapter mMovieAdapter;

    private RecyclerView mMovieListRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(R.id.error_message_tv) TextView mErrorMessageTextView;
    // A loading indicator that will took the front stage while the data are loaded
    @BindView(R.id.loading_indicator_pb) ProgressBar mLoadingIndicator;

    private Toast mToast;

    /*Main Loader ID*/
    private static final int ID_MOVIE_LOADER = 23;

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
        /*Checking for internet status*/
        if(isOnline()) {
         /*Filling the database asynchronously, using MoviePreferences.getPreferredSortingCriterion
         to get the actual criterion saved in the preferences*/
            loadMovieData(getPreferredSortingCriterion(this));
        }

        mMovieListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movie);
           /* Number of columns handled by the Grid Layout Manager according to the device dimension
           * since we are in onCreate, that mean we can change the grid span count
           * between portrait and landscape mode*/
        final int gridSpanCount = getResources().getInteger(R.integer.movie_grid_span_count) ;

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),gridSpanCount);

        mMovieListRecyclerView.setLayoutManager(layoutManager);
        mMovieListRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);

        mMovieListRecyclerView.setAdapter(mMovieAdapter);

        ButterKnife.bind(this);

        showLoadingIndicator();

        // Initialize a loader or re use the already started one if it exists
        /* Connect the activity whit le Loader life cycle  */
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER,null,MainActivity.this);
    }



    /**
     *Initiating the asynchronous task that gets our data from internet
     *
     * @param queryKey determine the sorting criterion of the movies data
     */
    private void loadMovieData(String queryKey){
        URL theMovieDBSearchURL = NetworkUtils.buildUrl(queryKey);
        new FetchMovieTask().execute(theMovieDBSearchURL);


    }




    /**
     * Showing the recycler view
     */
    private void showDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieListRecyclerView.setVisibility(View.VISIBLE);
    }


    /**
     * Showing an error message
     */
    private void showErrorMessage(){
        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Cursor Loader called  when a new Loader needs to be created to fetch data from de data base
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param loadArgs   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loadArgs) {

        switch (loaderId){
            case ID_MOVIE_LOADER:
                Uri movieQueryUri = MovieListContract.MovieListEntry.CONTENT_URI;

                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader not implemented : " + loaderId);
        }

    }

    /**
     * Called  on the main trade when a Loader has finished loading data.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        // I chose to put the poster resolution in the preferences with some other features in mind
        // that maybe the memory size of the images would cause problems.
        // It turns out the features are not implemented yet and I let myself that door open
        String posterResolution = MoviePreferences.getPreferredPosterResolution(MainActivity.this);

        mMovieAdapter.swapCursor(data, posterResolution);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        mMovieListRecyclerView.smoothScrollToPosition(mPosition);

        if (data.getCount() != 0) showDataView();


    }

    /**
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        String posterResolution = MoviePreferences.getPreferredPosterResolution(MainActivity.this);
        mMovieAdapter.swapCursor(null, posterResolution);
    }

    /**
     * The asynchronous task that gets our data from internet
     */

    public class FetchMovieTask extends AsyncTask<URL,Void,ContentValues[]>{
        /**
         * Flushing the existing data base
         */
        @Override
        protected void onPreExecute() {
            MainActivity.this.getContentResolver().delete(MovieListContract.MovieListEntry.CONTENT_URI,null,null);
            super.onPreExecute();

        }

        /**
         * Fetching a JSON containing the data from the internet using NetworkUtils and then
         * factoring it into a ContentValues[]
         *
         * @param params containing the URL to perform the search on internet
         * @return dataFromJson the ContentValues[] containing the movies data
         */
        @Override
        protected ContentValues[] doInBackground(URL... params) {
            URL searchURL = params[0];
            String jsonMovieDBResults;
            try {
                jsonMovieDBResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
           /*MovieDBJsonUtils.getMovieContentValuesFromJson returns a ContentValues[]
                 whit all the data extracted from the Json*/
                return MovieDBJsonUtils.getMovieContentValuesFromJson(jsonMovieDBResults);
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }


        }

        /**
         * Filling the database with movieData.
         * Emptiness of movieData indicated a internet connection problem
         * or an unusable JSON return from the internet query
         * @param movieData the movies data
         */
        @Override
        protected void onPostExecute(ContentValues[] movieData) {
            if(movieData != null){
                MainActivity.this.getContentResolver().bulkInsert(MovieListContract.MovieListEntry.CONTENT_URI,movieData);




            } else {
                showErrorMessage();
            }
        }
    }

    /**
     * Inflating the menu
     *
     * @param menu The options menu in which we place our items.
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.movie,menu);

        return true;
    }

    /**
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWhatSelected = item.getItemId();
        Context context = MainActivity.this;
        String message;
        String oldPreference = MoviePreferences.getPreferredSortingCriterion(this);
        boolean preferenceChanged = false;
        if(mToast != null){
            mToast.cancel();
        }

        //Check if we are already displaying the wright data
        if(menuItemThatWhatSelected == R.id.action_sort_by_popularity){
            if (!(oldPreference.equals(getString(R.string.criterion_popular)))) {
                MoviePreferences.setPreferredSortingCriterion(context, getString(R.string.criterion_popular));
                message = getString(R.string.toast_popular_sort);
                mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                mToast.show();
                preferenceChanged = true;
            }

        }else if(menuItemThatWhatSelected == R.id.action_sort_by_rate){
            if (!(oldPreference.equals(getString(R.string.criterion_most_rated)))){
                MoviePreferences.setPreferredSortingCriterion(context,getString(R.string.criterion_most_rated));
                message = getString(R.string.toast_rate_sort);
                mToast = Toast.makeText(context,message,Toast.LENGTH_LONG);
                mToast.show();
                preferenceChanged = true;
            }
        }

        // Only if changes are needed, load the new data in the data base and restart the loader
        if (preferenceChanged){
            MainActivity.this.getContentResolver().delete(MovieListContract.MovieListEntry.CONTENT_URI,null,null);
            loadMovieData(MoviePreferences.getPreferredSortingCriterion(this));
            mMovieAdapter = new MovieAdapter(this,this);
            mMovieListRecyclerView.setAdapter(mMovieAdapter);

            // restarting the loader
            getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER,null,this);
            return true;

        }else {
            message = getString(R.string.toast_no_sort_needed);
            mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            mToast.show();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Responding to clicks on our list.
     *
     * @param movieIdInteger the internet Id of the particular movie the user focused on
     */
    @Override
    public void onListItemClick(int movieIdInteger) {
        //Casting the id into a string and building the query uri
        String movieIdString = Integer.toString(movieIdInteger);

        Uri detailUri = MovieListContract.MovieListEntry.CONTENT_URI.buildUpon()
                .appendPath(movieIdString)
                .build();

        // Assembling the Intent to DetailActivity
        Intent startDetailActivityIntent = new Intent(MainActivity.this, DetailActivity.class);

        startDetailActivityIntent.setData(detailUri);

        startActivity(startDetailActivityIntent);
    }

    /**
     * Show a loading indicator, hiding the data view
     */
    private void showLoadingIndicator(){
        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Checking for internet connection using the connectivity manager.
     * This is were the permission "ACCESS_NETWORK_STATE" is needed
     *
     * @return true if a connection is up or pending
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
