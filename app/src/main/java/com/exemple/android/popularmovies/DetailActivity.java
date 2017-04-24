package com.exemple.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.exemple.android.popularmovies.data.MovieListContract;
import com.exemple.android.popularmovies.data.MoviePreferences;
import com.exemple.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private String mInfoTestingPurpose;

    /************************
     ** VIEWS REFERENCES **
     ************************/
    private TextView mOriginalTitleTextView;
    private ImageView mThumbnailImageView;
    private TextView mReleaseDateTextView;
    private TextView mRateTextView;
    private TextView mOverviewTextView;

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
    public static final int INDEX_MOVIE_MOVIE_ID = 5;

    private Uri mMovieDetailUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mOriginalTitleTextView = (TextView) findViewById(R.id.original_title_tv);
        mThumbnailImageView = (ImageView) findViewById(R.id.movie_thumbnail_iv);
        mReleaseDateTextView = (TextView) findViewById(R.id.movie_release_tv);
        mRateTextView = (TextView) findViewById(R.id.movie_rate_tv);
        mOverviewTextView = (TextView) findViewById(R.id.overview_tv);

        Intent triggeringIntent = getIntent();
        if (triggeringIntent != null) {
            mMovieDetailUri = triggeringIntent.getData();
        }
        if(mMovieDetailUri == null) throw new NullPointerException("Movie ID for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER,null,this);
    }


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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        boolean cursorHasValidDate = false;
        if (cursor != null && cursor.moveToFirst()){
            cursorHasValidDate = true;
        }
        if(!cursorHasValidDate){
            return;
        }

        mOriginalTitleTextView.setText(cursor.getString(INDEX_MOVIE_ORIGINAL_TITLE));
        bind(cursor.getString(INDEX_MOVIE_POSTER));
        mReleaseDateTextView.setText(cursor.getString(INDEX_MOVIE_RELEASE_DATE));
        mRateTextView.setText(Integer.toString(cursor.getInt(INDEX_MOVIE_VOTE_AVERAGE)) + "/10");
        mOverviewTextView.setText(cursor.getString(INDEX_MOVIE_OVERVIEW));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    void bind (String pathToImage){
        String posterResolution = MoviePreferences.getPreferredPosterResolution(DetailActivity.this);
        URL urlToFirstPoster = NetworkUtils.buildURL(pathToImage, posterResolution);
        Picasso.with(this).load(urlToFirstPoster.toString()).into(mThumbnailImageView);

    }
}
