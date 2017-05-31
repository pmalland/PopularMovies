package com.exemple.android.popularmovies;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exemple.android.popularmovies.data.Video;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>{

    private Context mContext;

    private ArrayList<Video> mTrailerList;

    private final VideoListItemClickListener mOnClickListener;

    /**
     *
     * Constructor
     * @param context  the context used to talk to the UI and app resources
     * @param listener the clickListener for the views
     */
    TrailerAdapter(@NonNull Context context, VideoListItemClickListener listener){
        this.mContext = context;
        mOnClickListener = listener;
    }

    /**
     * Called when each view holder are created.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType only used if the recycler view has more that on type of item
     *                 ( not the case here)
     * @return a new TrailerAdapterViewHolder that hold the view for each list item
     */
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.trailer_list_item,viewGroup,false);
        view.setFocusable(true);
        return new TrailerAdapterViewHolder(view);
    }

    /**
     * Update the content of the view holder
     *
     * @param holder The ViewHolder which should be updated to represent
     *                               the contents of the item at that given position
     *                               in the data set
     * @param position              The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        Video currentTrailer = mTrailerList.get(position);
        holder.itemView.setTag(currentTrailer.getKey());
        holder.bind(currentTrailer.getName());
    }

    /**
     * Returns the number of items to display
     *
     * @return the number of items available in our ArrayList<Video>
     */
    @Override
    public int getItemCount() {
        if(null == mTrailerList) return 0;
        return mTrailerList.size();
    }

    /**
     * Change the content of the trailer list
     *
     * @param newTrailerList the new review list to use as TrailerAdapter's data source
     */
    public void swapTrailerList(ArrayList<Video> newTrailerList){
        mTrailerList = newTrailerList;
        notifyDataSetChanged();
    }

    /**
     * The view holder. Behave as a cache of the child view for the review data.
     */
    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.tv_trailer_type) TextView listVideoType;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        /*Binding the video data to the corresponding TextView*/
        void bind(String videoName){
            listVideoType.setText(videoName);
        }

        @Override
        public void onClick(View view) {
            String videoKey = (String) view.getTag();
            Log.i("onclickViewHolder", videoKey);
            mOnClickListener.onVideoListItemClick(videoKey);
        }
    }

    interface VideoListItemClickListener {
        void onVideoListItemClick(String movieKey);

    }
}
