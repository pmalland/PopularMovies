package com.exemple.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.popularmovies.data.Movie;
import com.exemple.android.popularmovies.data.MovieDetails;
import com.exemple.android.popularmovies.data.MovieListContract;
import com.exemple.android.popularmovies.data.MoviePreferences;
import com.exemple.android.popularmovies.utilities.MovieDBJsonUtils;
import com.exemple.android.popularmovies.utilities.MovieUtils;
import com.exemple.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {


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

    @BindView(R.id.error_message_tv) TextView mErrorMessageTextView;
    @BindView(R.id.loading_indicator_pb)
    ProgressBar mLoadingIndicator;

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
    private MovieDetails mMovieDetails;

    private ReviewAdapter mReviewAdapter;

    private RecyclerView mReviewListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* We retrieve a Movie from the Intent and look for
         for the details of the selected movie and display it       */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mReviewListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_review);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mReviewListRecyclerView.setLayoutManager(layoutManager);

        mReviewAdapter = new ReviewAdapter(this);
        mReviewListRecyclerView.setAdapter(mReviewAdapter);

        ButterKnife.bind(this);

        showloadingIndicator();

        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.movie_details_key))) {
        /*Retrieving the needed Movie to get the data we want to display*/
            Intent triggeringIntent = getIntent();
            if (triggeringIntent != null) {

                try {
                    mMovie = triggeringIntent.getExtras()
                            .getParcelable(getString(R.string.parcelable_movie_key));
                    mMovieDetails.setMovie(mMovie);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
            if (mMovie == null) throw new NullPointerException("Failed to pass Movie via Intent");
            if (NetworkUtils.isOnline(this)){
                loadReviewData();
            }else {
                showErrorMessage();
            }
        }else {
            mMovieDetails = savedInstanceState
                    .getParcelable(getString(R.string.parcelable_movie_key));
            mMovie = mMovieDetails.getMovie();
            mReviewAdapter.swapMovieList(mMovieDetails.getReviews());


        }
        /* Binding party*/
        mOriginalTitleTextView.setText(mMovie.getOriginalTitle());
        bind(mMovie.getPosterPath());
        mReleaseDateTextView.setText(mMovie.getReleaseDate());
        String rate = Double.toString(mMovie.getVoteAverage()) + "/10";
        mRateTextView.setText(rate);
        mOverviewTextView.setText(mMovie.getOverview());

        showDataView();

        addButtonClickListener();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void loadReviewData(){

    }

    private void showErrorMessage(){

    }

    private void showDataView(){

    }

    private void showloadingIndicator(){

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

    public class FetchMovieDetailsTask extends AsyncTask<URL,Void, MovieDetails>{


        @Override
        protected MovieDetails doInBackground(URL... params) {

            URL searchURL = params[0];
            String jsonMovieDbDetailsResult;
            try{
                jsonMovieDbDetailsResult = NetworkUtils.getResponseFromHttpUrl(searchURL);

                return MovieDBJsonUtils.getMovieDetailsFromDetailJson(jsonMovieDbDetailsResult);
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }


        @Override
        protected void onPostExecute(MovieDetails movieDetails) {
            if (movieDetails != null){
                mMovieDetails = movieDetails;

            }
            super.onPostExecute(movieDetails);
        }
    }
}
