package com.exemple.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.popularmovies.data.Movie;
import com.exemple.android.popularmovies.data.MovieListContract;
import com.exemple.android.popularmovies.data.MoviePreferences;
import com.exemple.android.popularmovies.utilities.MovieUtils;
import com.exemple.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{


    /************************
     ** VIEWS REFERENCES **
     ************************/

    /* Using ButterKnife libraries*/
    @BindView(R.id.original_title_tv) TextView mOriginalTitleTextView;
    @BindView(R.id.movie_thumbnail_iv) ImageView mThumbnailImageView;
    @BindView(R.id.movie_release_tv) TextView mReleaseDateTextView;
    @BindView(R.id.movie_rate_tv) TextView mRateTextView;
    @BindView(R.id.overview_tv) TextView mOverviewTextView;
    @BindView(R.id.bt_favorite) Button mFavoriteButton;

    /************************
     ** DETAILS LOADER ID **
     ************************/
    private static final int ID_DETAIL_LOADER = 42;

    /************************
     ** DETAILS PROJECTION **
     ************************/
    public static final String[] DETAIL_MOVIE_PROJECTION = {
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
//    public static final int INDEX_MOVIE_MOVIE_ID = 5;

    private Uri mMovieDetailUri;
    private Movie mMovie;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* We retrieve a Movie from the Intent and look for
         for the details of the selected movie and display it       */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        /*Retrieving the needed Movie to get the data we want to display*/
        Intent triggeringIntent = getIntent();
        if (triggeringIntent != null) {

            try {
                mMovie = triggeringIntent.getExtras().getParcelable(getString(R.string.parcelable_movie_key));
            } catch (NullPointerException e){
                e.printStackTrace();
            }

        }
        if(mMovie == null) throw new NullPointerException("Failed to pass Movie via Intent");

        /* Binding party*/
        mOriginalTitleTextView.setText(mMovie.getOriginalTitle());
        bind(mMovie.getPosterPath());
        mReleaseDateTextView.setText(mMovie.getReleaseDate());
        String rate = Double.toString(mMovie.getVoteAverage()) + "/10";
        mRateTextView.setText(rate);
        mOverviewTextView.setText(mMovie.getOverview());

        addButtonClickListener();

        /* Connect the activity whit le Loader life cycle  */
//        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER,null,this);
    }

    /**
     * Cursor Loader called  when a new Loader needs to be created to fetch data from de data base
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param args Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId){
            case ID_DETAIL_LOADER:

                        return new CursorLoader(this,
                                mMovieDetailUri,
                                DETAIL_MOVIE_PROJECTION,
                                null,
                                null,
                                null);
            default:
                    throw new RuntimeException("Loader unknown :" + loaderId);
        }
    }

    /**
     * Called  on the main trade when a Loader has finished loading data.
     *
     * @param loader The Cursor loader that finished
     * @param cursor The Cursor that is being returned
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /* Checking if the cursor has valid data, return if it is nor the case*/
        boolean cursorHasValidDate = false;
        if (cursor != null && cursor.moveToFirst()){
            cursorHasValidDate = true;
        }
        if(!cursorHasValidDate){
            String message = getString(R.string.toast_empty_cursor);
            Toast.makeText(DetailActivity.this, message, Toast.LENGTH_LONG).show();
            return;
        }
        /* Binding party*/
        mOriginalTitleTextView.setText(cursor.getString(INDEX_MOVIE_ORIGINAL_TITLE));
        bind(cursor.getString(INDEX_MOVIE_POSTER));
        mReleaseDateTextView.setText(cursor.getString(INDEX_MOVIE_RELEASE_DATE));
        String rate = Double.toString(cursor.getDouble(INDEX_MOVIE_VOTE_AVERAGE)) + "/10";
        mRateTextView.setText(rate);
        mOverviewTextView.setText(cursor.getString(INDEX_MOVIE_OVERVIEW));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Binds the image to her image view using the Picasso library.
     *
     * @param pathToImage URL to the needed image
     */
    void bind (String pathToImage){
        String posterResolution = MoviePreferences.getPreferredPosterResolution(DetailActivity.this);
        URL urlToFirstPoster = NetworkUtils.buildURL(pathToImage, posterResolution);
        Picasso.with(this)
                .load(urlToFirstPoster.toString())
                .placeholder(R.drawable.ic_file_download_black_48dp)
                .error(R.drawable.ic_error_black_48dp)
                .into(mThumbnailImageView);

    }

    /**
     * Our OnClickListener for mFavoriteButton
     * the "UNIQUE (" + COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE" on the database settings
     * should deal with the doubles
     */
    void addButtonClickListener(){

        mFavoriteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                ContentValues movieContentValues = MovieUtils.getContentValuesFromMovie(mMovie);
                Uri uri = getApplicationContext().getContentResolver()
                        .insert(MovieListContract.MovieListEntry.CONTENT_URI,movieContentValues);

                if(mToast != null){
                    mToast.cancel();
                }
                String message = getString(R.string.toast_marked_as_favorite);
                mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                mToast.show();
            }

        });
    }
}
