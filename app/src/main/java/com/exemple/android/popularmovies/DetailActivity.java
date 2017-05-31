package com.exemple.android.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class DetailActivity extends AppCompatActivity
            implements TrailerAdapter.VideoListItemClickListener {


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
    @BindView(R.id.bt_share_trailer) Button mShareButton;

    @BindView(R.id.error_message_tv) TextView mErrorMessageTextView;
    @BindView(R.id.loading_indicator_pb) ProgressBar mLoadingIndicator;


    private Movie mMovie;
    private Toast mToast;
    private MovieDetails mMovieDetails;
    private Context mContext;
    private Activity mActivity;
    private String mToShare;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    private RecyclerView mReviewListRecyclerView;
    private RecyclerView mTrailerListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* We retrieve a Movie from the Intent and look for
         for the details of the selected movie and display it
         In the same tim we query the movieDB data base for metadata on the reviews
          and trailers */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*Binding whit ButterKnife*/
        ButterKnife.bind(this);

        mContext = this;
        mActivity = DetailActivity.this;

        /* Setting Review RecyclerView*/
        mReviewListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_review);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mReviewListRecyclerView.setLayoutManager(layoutManager);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewListRecyclerView.setAdapter(mReviewAdapter);

        /* Setting Trailer RecyclerView*/
        mTrailerListRecyclerView = (RecyclerView) findViewById(R.id.rv_trailer);
        mTrailerListRecyclerView.
                setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mTrailerAdapter = new TrailerAdapter(this,this);
        mTrailerListRecyclerView.setAdapter(mTrailerAdapter);

        showLoadingIndicator();
        if(mToast != null){
            mToast.cancel();
        }
        String message = getString(R.string.toast_waiting_data);
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();

        boolean online = NetworkUtils.isOnline(this);

        /*Check if we have something useful in savedInstanceState*/
        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.movie_details_key))) {
            mMovieDetails = new MovieDetails();
            Log.i("DetailsActi","savedInstanceState == null");
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
            if (mMovie == null){
                throw new NullPointerException(getString(R.string.failed_details_Intent));
            }
            if (online){
                loadReviewNTrailerData(Long.toString(mMovie.getMovieId()));
                showDataView();
            }else {
                showErrorMessage();
            }
        }else {
            /* Here the onSaveInstance Bundle already containts the data we need*/
            Log.i("DetailsActi","savedInstanceState != null");
            mMovieDetails = savedInstanceState
                    .getParcelable(getString(R.string.parcelable_movie_key));
            mMovie = mMovieDetails.getMovie();
            mReviewAdapter.swapMovieList(mMovieDetails.getReviews());
            mTrailerAdapter.swapTrailerList(mMovieDetails.getVideos());
            mToShare = mMovieDetails.getVideos().get(0).getKey();
            mShareButton.setVisibility(View.VISIBLE);

            try {
                if (mMovieDetails.getReviews() != null) showDataView();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }

        /* Filling all the Non-Recycler views and setting the button onClickListeners*/
        bindingParty();

    }

    /**
     * Filling all the Non-Recycler views and setting the button onClickListeners
     */
    private void bindingParty(){
        mOriginalTitleTextView.setText(mMovie.getOriginalTitle());
        bind(mMovie.getPosterPath());
        mReleaseDateTextView.setText(mMovie.getReleaseDate());
        String rate = Double.toString(mMovie.getVoteAverage()) + "/10";
        mRateTextView.setText(rate);
        mOverviewTextView.setText(mMovie.getOverview());
        addButtonClickListener();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.parcelable_movie_key),mMovieDetails);
        super.onSaveInstanceState(outState);
    }

    /**
     *Initiating the asynchronous task that gets our data from internet
     *
     * @param movieID identify the movie we want details from
     */
    private void loadReviewNTrailerData(String movieID){
        URL movieDetailsSearchURL = NetworkUtils.buildMovieDetailURL(movieID);
        new FetchMovieDetailsTask().execute(movieDetailsSearchURL);
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
     * Our OnClickListener for mFavoriteButton and mShareButton
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
        /**
         * The shareButton is VISIBLE only after the data are retrieved from internet or
         * from the onSavedInstance bundle
         */
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mimeType = getString(R.string.text_plain_mime_type);

                String title = getString(R.string.shared_intent_title);

                String textToShare = NetworkUtils.getYoutubeURL(mToShare);
        /* ShareCompat.IntentBuilder provides a fluent API for creating Intents */
                ShareCompat.IntentBuilder
                /* The from method specifies the Context from which this share is coming from */
                        .from(mActivity)
                        .setType(mimeType)
                        .setChooserTitle(title)
                        .setText(textToShare)
                        .startChooser();
            }
        });
    }

    /**
     * Responding to clicks on our trailer list. Pick trailer key and build an Intent to start
     * a youtube app or a browser
     *
     * @param movieKey the trailer key on Youtube
     */
    @Override
    public void onVideoListItemClick(String movieKey) {
        NetworkUtils.watchYoutubeVideo(movieKey, mContext);
        if(mToast != null){
            mToast.cancel();
        }
        String message = getString(R.string.toast_waiting_youtube);
        mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        mToast.show();
    }

    /**
     * The asynchronous task that gets our data from internet.
     * Take the URL localizing the data we need and return an MovieDetails
     * containing the reviews list and the trailers list
     */
    public class FetchMovieDetailsTask extends AsyncTask<URL,Void, MovieDetails>{

        /**
         * Fetching a JSON containing the data from the internet using NetworkUtils and then
         * factoring it into an MovieDetails
         *
         * @param params containing the URL to perform the search on internet
         * @return the MovieDetails containing the movies data
         */
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

        /**
         * Filling our review and trailer list
         * Emptiness of movieDetails indicated a internet connection problem
         * or an unusable JSON return from the internet query
         * @param movieDetails the movies data, reviews and trailers
         */
        @Override
        protected void onPostExecute(MovieDetails movieDetails) {
            if (movieDetails != null){
                Log.i("OnPostExecute", "movieDetails != null");
                mMovieDetails = movieDetails;
                mReviewAdapter.swapMovieList(mMovieDetails.getReviews());
                mTrailerAdapter.swapTrailerList(mMovieDetails.getVideos());
                bindingParty();
                mToShare = mMovieDetails.getVideos().get(0).getKey();
                mShareButton.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(movieDetails);
        }
    }

    /**
     * Showing an error message
     */
    private void showErrorMessage(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mTrailerListRecyclerView.setVisibility(View.INVISIBLE);
        mReviewListRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Showing the recycler view
     */
    private void showDataView(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mTrailerListRecyclerView.setVisibility(View.VISIBLE);
        mReviewListRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show a loading indicator, hiding the data view
     */
    private void showLoadingIndicator(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mTrailerListRecyclerView.setVisibility(View.INVISIBLE);
        mReviewListRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

}

