package com.exemple.android.popularmovies;

import android.content.Context;
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

    public MovieAdapter(Context context) {
//        mNumberItems = numberOfItem;
        mContext = context;}

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup,shouldAttachToParentImmediately);
        MovieAdapterViewHolder viewHolder = new MovieAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        String pathToPoster = mPosterPath[position];
        movieAdapterViewHolder.bind(pathToPoster);


    }

    @Override
    public int getItemCount() {
        if (mPosterPath == null){
            return 0;
        } else {
            return mPosterPath.length;
        }

    }

    public void setPathToPoster(String[] pathToPoster, String posterSize){
        mPosterPath = pathToPoster;
        mPosterSize = posterSize;
        notifyDataSetChanged();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView listItemMovieView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            listItemMovieView = (ImageView) itemView.findViewById(R.id.iv_item_movie);
        }

        void bind (String pathToImage){
            URL urlToFirstPoster = NetworkUtils.buildURL(pathToImage, mPosterSize);
            Picasso.with(mContext).load(urlToFirstPoster.toString()).into(listItemMovieView);

        }
    }

}
