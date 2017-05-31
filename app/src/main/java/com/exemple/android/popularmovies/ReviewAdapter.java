package com.exemple.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exemple.android.popularmovies.data.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    private Context mContext;

    private ArrayList<Review> mReviewList;

    /**
     * Constructor
     *
     * @param context the context used to talk to the UI and app resources
     */
    ReviewAdapter(@NonNull Context context){
        this.mContext = context;

    }

    /**
     * Called when each view holder are created.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType only used if the recycler view has more that on type of item
     *                 ( not the case here)
     * @return a new ReviewAdapterViewHolder that hold the view for each list item
     */
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.review_list_item,viewGroup,false);
        view.setFocusable(true);

        return new ReviewAdapterViewHolder(view);
    }

    /**
     * Update the content of the view holder
     *
     * @param reviewAdapterViewHolder The ViewHolder which should be updated to represent
     *                               the contents of the item at that given position
     *                               in the data set
     * @param position              The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {

        Review currentReview = mReviewList.get(position);
        reviewAdapterViewHolder.bind(currentReview.getAuthor(),currentReview.getContent());
    }

    /**
     * Returns the number of items to display
     *
     * @return the number of items available in our ArrayList<Review>
     */
    @Override
    public int getItemCount() {
        if(null == mReviewList) return 0;
        return mReviewList.size();
    }

    /**
     * Change the content of the review list
     *
     * @param newReviewList the new review list to use as ReviewAdapter's data source
     */
    void swapMovieList(ArrayList<Review> newReviewList){
        mReviewList = newReviewList;
        notifyDataSetChanged();
    }

    /**
     * The view holder. Behave as a cache of the child view for the review data.
     */
    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_author) TextView listReviewAuthorTextView;
        @BindView(R.id.tv_review_content) TextView listReviewContentTextView;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        /*Binding the review data to the corresponding TextView*/
        void bind(String reviewAuthor, String reviewContent){
            listReviewAuthorTextView.setText(reviewAuthor + " wrote:");
            listReviewContentTextView.setText(reviewContent);
        }
    }

}
