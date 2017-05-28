package com.exemple.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MovieDetails implements Parcelable{

    public Movie movie;
    public List<Review> reviews =  null;
    public Video video;

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
        parcel.writeList(this.reviews);
        parcel.writeValue(this.video);
    }

    /**
     * Private constructor
     * Unwrapping the parcel
     *
     * @param in the parcel
     */
    protected MovieDetails(Parcel in){
        this.movie = (Movie) in.readValue(Movie.class.getClassLoader());
        in.readList(this.reviews, Review.class.getClassLoader());
        this.video = (Video) in.readValue(Video.class.getClassLoader());
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

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
