package com.exemple.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.exemple.android.popularmovies.data.Movie;
import com.exemple.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * MovieAdapter exposes a list of movie poster from a Cursor containing the path to those posters
 * to a recycler view
 */

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final Context mContext;
    private String mPosterSize;

    /*reference to a list item click listener*/
    private final ListItemClickListener mOnClickListener;

    private List<Movie> mMovieList;

    /**
     * Constructor
     *
     * @param context the context used to talk to the UI and app resources
     * @param listener the click handler
     */
    MovieAdapter(@NonNull Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    /**
     * Called when each view holder are created.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType only used if the recycler view has more that on type of item
     *                 ( not the case here)
     * @return a new MovieAdapterViewHolder that hold the view for each list item
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.movie_list_item,viewGroup,false);
        view.setFocusable(true);

        return new MovieAdapterViewHolder(view);
    }

    /**
     * Update the content of the view holder
     *
     * @param movieAdapterViewHolder The ViewHolder which should be updated to represent
     *                               the contents of the item at that given position
     *                               in the data set
     * @param position              The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {

//        mCursor.moveToPosition(position);
        Movie currentMovie = mMovieList.get(position);
        movieAdapterViewHolder.itemView.setTag(currentMovie.getMovieId());

//        String pathToPoster =mCursor.getString(MainActivity.INDEX_MOVIE_POSTER);
        String pathToPoster = currentMovie.getPosterPath();
        movieAdapterViewHolder.bind(pathToPoster);

    }

    /**
     * Returns the number of items to display
     *
     * @return the number of items available in our ArrayList<Movie>
     */
    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    /**
     * Change the content of the movie list
     *
     * @param newMovieList the new movie list to use as MovieAdapter's data source
     * @param posterSize the argument used to choose the poster resolution
     *                   we get with the Picasso methods
     */
    void swapMovieList(ArrayList<Movie> newMovieList, String posterSize){
        mMovieList = newMovieList;
        mPosterSize = posterSize;
        notifyDataSetChanged();
    }
    /**
     * The view holder. Behave as a cache of the child view for the movies poster.
     * We also set an OnClickListener here.
     */
    class MovieAdapterViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

        ImageView listItemMovieView;

        MovieAdapterViewHolder(View itemView) {
            super(itemView);

            listItemMovieView = (ImageView) itemView.findViewById(R.id.iv_item_movie);

            itemView.setOnClickListener(this);
        }

        /*Binding the movie poster to his image view with the Picasso library*/
        void bind (String pathToImage){
//            Log.i("AdapterBind ",pathToImage);
            URL urlToFirstPoster = NetworkUtils.buildURL(pathToImage, mPosterSize);
            Picasso.with(mContext)
                    .load(urlToFirstPoster.toString())
                    .placeholder(R.drawable.ic_file_download_black_48dp)
                    .error(R.drawable.ic_error_black_48dp)
                    .into(listItemMovieView);
        }
        /* Given the position of the adapter, we get the corresponding Movie ID from the cursor
         * and pass it to the onListItemClick method */
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
//            Movie currentMovie = mMovieList.get(clickedPosition);
//            mCursor.moveToPosition(clickedPosition);
//            int movieIdInteger = mCursor.getInt(MainActivity.INDEX_MOVIE_ID);
//            long movieIdInteger = currentMovie.getMovieId();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }


   /* The interface that receives onClick messages*/

    interface ListItemClickListener {
        void onListItemClick(int clickedPosition);

    }
}
