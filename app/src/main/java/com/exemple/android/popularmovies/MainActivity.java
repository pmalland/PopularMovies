package com.exemple.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.popularmovies.data.Movie;
import com.exemple.android.popularmovies.data.MovieListContract;
import com.exemple.android.popularmovies.data.MoviePreferences;
import com.exemple.android.popularmovies.utilities.MovieDBJsonUtils;
import com.exemple.android.popularmovies.utilities.MovieUtils;
import com.exemple.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.exemple.android.popularmovies.R.menu.movie;
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

    private ArrayList<Movie> mMovieList;

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
            MovieListContract.MovieListEntry.COLUMN_OVERVIEW,
            MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE,
            MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE,
            MovieListContract.MovieListEntry.COLUMN_VOTE_AVERAGE,
            MovieListContract.MovieListEntry.COLUMN_MOVIE_ID,
    };

    /***********
     ** INDEX **
     ***********/
    public static final int INDEX_MOVIE_POSTER = 0;
    public static final int INDEX_MOVIE_OVERVIEW = 1;
    public static final int INDEX_MOVIE_RELEASE_DATE = 2;
    public static final int INDEX_MOVIE_ORIGINAL_TITLE = 3;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 4;
    public static final int INDEX_MOVIE_MOVIE_ID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie);


        mMovieListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movie);
           /* Number of columns handled by the Grid Layout Manager according to the device dimension
           * since we are in onCreate, that mean we can change the grid span count
           * between portrait and landscape mode*/
        final int gridSpanCount = getResources().getInteger(R.integer.movie_grid_span_count) ;

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),gridSpanCount);

        mMovieListRecyclerView.setLayoutManager(layoutManager);
        mMovieListRecyclerView.setHasFixedSize(true);
//        mMovieListRecyclerView.setItemViewCacheSize(10);
//        mMovieListRecyclerView.setDrawingCacheEnabled(true);
//        mMovieListRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mMovieAdapter = new MovieAdapter(this, this);

        mMovieListRecyclerView.setAdapter(mMovieAdapter);

        ButterKnife.bind(this);
        /* Attach a ItemTouchHelper to the RecyclerView to recognize when a user swipes to delete a Movie
          from the favorite database. */
        addItemTouchHelper();

        showLoadingIndicator();

        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.movie_list_key))){
            mMovieList = new ArrayList<>();
            /*Checking for internet status*/
            if(NetworkUtils.isOnline(this)) {
         /*Filling the movie list asynchronously, using MoviePreferences.getPreferredSortingCriterion
         to get the actual criterion saved in the preferences*/
                loadMovieData(getPreferredSortingCriterion(this));
            } else {
                showErrorMessage();
            }
        }else {
            mMovieList = savedInstanceState.getParcelableArrayList(getString(R.string.movie_list_key));
            String posterResolution = MoviePreferences
                    .getPreferredPosterResolution(MainActivity.this);
            mMovieAdapter.swapMovieList(mMovieList,posterResolution);
            showDataView();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.movie_list_key),mMovieList);
        super.onSaveInstanceState(outState);
    }

    /**
     *Initiating the asynchronous task that gets our data from internet
     *
     * @param queryKey determine the sorting criterion of the movies data
     */
    private void loadMovieData(String queryKey){
        if ((queryKey.equals(getString(R.string.criterion_popular)))
                || (queryKey.equals(getString(R.string.criterion_most_rated)))){
            URL theMovieDBSearchURL = NetworkUtils.buildUrl(queryKey);
            new FetchMoviesTask().execute(theMovieDBSearchURL);

        }else if (queryKey.equals(getString(R.string.criterion_favorite))){
            // Initialize a loader or re use the already started one if it exists
        /* Connect the activity whit le Loader life cycle  */
            getSupportLoaderManager().initLoader(ID_MOVIE_LOADER,null,MainActivity.this);
        }

    }

    /**
     * Showing the recycler view
     */
    private void showDataView(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieListRecyclerView.setVisibility(View.VISIBLE);
    }


    /**
     * Showing an error message
     */
    private void showErrorMessage(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
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

        mMovieList = MovieUtils.getArrayListFromCursor(data);
//        mMovieAdapter.swapCursor(data, posterResolution);
        mMovieAdapter.swapMovieList(mMovieList,posterResolution);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        mMovieListRecyclerView.smoothScrollToPosition(mPosition);

       if (data.getCount() != 0){
           showDataView();
       } else Log.i("onLoadFinished", "data.getCount() = 0");

    }

    /**
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        String posterResolution = MoviePreferences.getPreferredPosterResolution(MainActivity.this);
//        mMovieAdapter.swapMovieList(null, posterResolution);
//        Log.i("onLoadReset", "called");
    }

    /**
     * The asynchronous task that gets our data from internet.
     * Take the URL localizing the data we need and return an ArrayList<Movie>
     */
    public class FetchMoviesTask extends AsyncTask<URL,Void,ArrayList<Movie>>{
        /**
         * Flushing the existing data base if any
         */
        @Override
        protected void onPreExecute() {
            mMovieList = null;
            super.onPreExecute();

        }

        /**
         * Fetching a JSON containing the data from the internet using NetworkUtils and then
         * factoring it into an ArrayList<Movie>
         *
         * @param params containing the URL to perform the search on internet
         * @return dataFromJson the ArrayList<Movie> containing the movies data
         */
        @Override
        protected ArrayList<Movie> doInBackground(URL... params) {
            URL searchURL = params[0];
            String jsonMovieDBResults;
            try {
                jsonMovieDBResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
           /*MovieDBJsonUtils.getMovieArrayLIsFromJson returns an ArrayList<Movie>
                 whit all the data extracted from the Json*/
                return MovieDBJsonUtils.getMovieArrayListFromJson(jsonMovieDBResults);
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }


        }

        /**
         * Filling the dataset with movieData.
         * Emptiness of movieData indicated a internet connection problem
         * or an unusable JSON return from the internet query
         * @param movieData the movies data
         */
        @Override
        protected void onPostExecute(ArrayList<Movie> movieData) {
            if(movieData != null){
                mMovieList = movieData;
                String posterResolution = MoviePreferences
                        .getPreferredPosterResolution(MainActivity.this);
                mMovieAdapter.swapMovieList(mMovieList,posterResolution);
                showDataView();
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

        getMenuInflater().inflate(movie,menu);

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
        switch (menuItemThatWhatSelected){
            case R.id.action_sort_by_popularity: {
                if (!(oldPreference.equals(getString(R.string.criterion_popular)))) {
                    MoviePreferences.setPreferredSortingCriterion(context, getString(R.string.criterion_popular));
                    message = getString(R.string.toast_popular_sort);
                    mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                    mToast.show();
                    preferenceChanged = true;
                }
                break;
            }
            case R.id.action_sort_by_rate: {
                if (!(oldPreference.equals(getString(R.string.criterion_most_rated)))){
                    MoviePreferences.setPreferredSortingCriterion(context,getString(R.string.criterion_most_rated));
                    message = getString(R.string.toast_rate_sort);
                    mToast = Toast.makeText(context,message,Toast.LENGTH_LONG);
                    mToast.show();
                    preferenceChanged = true;
                }
                break;
            }
            case R.id.action_display_favorites: {
                if (!(oldPreference.equals(getString(R.string.criterion_favorite)))){
                    MoviePreferences.setPreferredSortingCriterion(context,getString(R.string.criterion_favorite));
                    message = getString(R.string.toast_display_favorites);
                    mToast = Toast.makeText(context,message,Toast.LENGTH_LONG);
                    mToast.show();
                    preferenceChanged = true;
                }
            }
        }

        // Only if changes are needed, load the new data in the data base and restart the loader
        if (preferenceChanged){
            String newCriterion = MoviePreferences.getPreferredSortingCriterion(this);
            mMovieAdapter = new MovieAdapter(this,this);
            mMovieListRecyclerView.setAdapter(mMovieAdapter);
            loadMovieData(newCriterion);

            if (newCriterion.equals(getString(R.string.criterion_favorite))) {
//             restarting the loader
                getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);
            }
            return true;

        }else {
            message = getString(R.string.toast_no_sort_needed);
            mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            mToast.show();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Responding to clicks on our list. Pick the wright Movie and build an Intent to start
     * DetailActivity
     *
     * @param clickedPosition the position of the particular movie in the ArrayList<Movie>
     */
    @Override
    public void onListItemClick(int clickedPosition) {

        Movie currentMovie = mMovieList.get(clickedPosition);
//        Uri detailUri = MovieListContract.MovieListEntry.CONTENT_URI.buildUpon()
//                .appendPath(movieIdString)
//                .build();
        if (currentMovie != null) {
            // Assembling the Intent to DetailActivity
            Log.i("onListItemClick",currentMovie.getOriginalTitle());
            Intent startDetailActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
            startDetailActivityIntent.putExtra(getString(R.string.parcelable_movie_key), currentMovie);
//        startDetailActivityIntent.setData(detailUri);

            startActivity(startDetailActivityIntent);

        }
    }

    /**
     * Show a loading indicator, hiding the data view
     */
    private void showLoadingIndicator(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }


    /**
     * Adding an ItemTouchHelper to manage the swipe action. We make sure that it works only
     * on the favorite screen
     */
    private void addItemTouchHelper(){

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            /* onMove is not used in our implementation*/
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * Called when a user swipes left or right on a ViewHolder
             * @param viewHolder corresponding to the item clicked
             * @param direction the swipe direction
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                /*Calling delete on data base only if we are on the favorite screen*/
                String sortCriterion = MoviePreferences
                        .getPreferredSortingCriterion(MainActivity.this);
                if(sortCriterion.equals(getString(R.string.criterion_favorite))) {

                /* Retrieve the id of the movie to delete. The tag was set to the
                 * MovieId in the MovieAdapter.onBindViewHolder */
                    long movieIdLong = (long) viewHolder.itemView.getTag();
                    Uri deleteQueryUri = MovieListContract.MovieListEntry.buildMovieUri(movieIdLong);

                    getContentResolver().delete(deleteQueryUri, null, null);

                    getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, MainActivity.this);
                } else {
                    mMovieAdapter.notifyDataSetChanged();
                }
            }
        }
        ).attachToRecyclerView(mMovieListRecyclerView);
    }
}
