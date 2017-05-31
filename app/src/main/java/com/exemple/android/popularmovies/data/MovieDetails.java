package com.exemple.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MovieDetails implements Parcelable{

    public Movie movie;
    public ArrayList<Review> reviews;
    public ArrayList<Video> videos;

    /**
     * Public constructor
     */
    public MovieDetails(){}

    /**
     * It's a bit mask. We don't use it in our implementation.
     * It's indicating the set of special object types marshaled by this Parcelable object instance.
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Shaping the object into a Parcel
     *
     * @param parcel the parcel
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeValue(this.movie);
        parcel.writeTypedList(this.reviews);
        parcel.writeTypedList(this.videos);
    }

    /**
     * Private constructor
     * Unwrapping the parcel
     *
     * @param in the parcel
     */
    protected MovieDetails(Parcel in){
        this.movie = (Movie) in.readValue(Movie.class.getClassLoader());
        this.reviews = new ArrayList<Review>();
        in.readTypedList(this.reviews, Review.CREATOR);
        in.readTypedList(this.videos, Video.CREATOR);
    }

    /**
     * Public CREATOR field that generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>(){

        /**
         * Create a new instance of the Parcelable class,
         * instantiating it from the given Parcel whose data had previously been written
         * by writeToParcel().
         *
         * @param parcel filled by writeToParcel
         * @return the new instance
         */
        @Override
        public MovieDetails createFromParcel(Parcel parcel) {
            return new MovieDetails(parcel);
        }

        /**
         * Create a new array of the Parcelable class.
         * @param size of the array
         * @return the new array
         */
        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    /*******************
     ** THE ACCESSORS **
     *******************/

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }
}
