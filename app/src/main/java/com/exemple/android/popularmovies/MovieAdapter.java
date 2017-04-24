package com.exemple.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.exemple.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

//    private int mNumberItems;
    private final Context mContext;
    private String[] mPosterPath;
    private String mPosterSize;

    //reference to a list item click listener
    private final ListItemClickListener mOnClickListener;

    private Cursor mCursor;

    public MovieAdapter(@NonNull Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.movie_list_item,viewGroup,false);
        view.setFocusable(true);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {

        mCursor.moveToPosition(position);

        String pathToPoster =mCursor.getString(MainActivity.INDEX_MOVIE_POSTER);
        movieAdapterViewHolder.bind(pathToPoster);

    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor, String posterSize){
        mCursor = cursor;
        mPosterSize = posterSize;
        notifyDataSetChanged();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

        ImageView listItemMovieView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            listItemMovieView = (ImageView) itemView.findViewById(R.id.iv_item_movie);

            itemView.setOnClickListener(this);
        }

        void bind (String pathToImage){
            URL urlToFirstPoster = NetworkUtils.buildURL(pathToImage, mPosterSize);
            Picasso.with(mContext).load(urlToFirstPoster.toString()).into(listItemMovieView);

        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);

            int movieIdInteger = mCursor.getInt(MainActivity.INDEX_MOVIE_ID);
            mOnClickListener.onListItemClick(movieIdInteger);
        }
    }


//    The interface that receives onClick messages

    public interface ListItemClickListener {
        void onListItemClick(int movieIdInteger);

    }
}
