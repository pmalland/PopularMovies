package com.exemple.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  An object for storing our Movie Data. we will use it in InstanceSaved
 */
public class Movie implements Parcelable {

    private String posterPath;
    private String overview;
    private String releaseDate;
    private String originalTitle;
    private double voteAverage;
    private long movieId;

    /**
     * Public constructor
     */
    public Movie(){}

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
        parcel.writeString(this.posterPath);
        parcel.writeString(this.overview);
        parcel.writeString(this.releaseDate);
        parcel.writeString(this.originalTitle);
        parcel.writeDouble(this.voteAverage);
        parcel.writeLong(this.movieId);
    }

    /**
     * Private constructor
     * Unwrapping the parcel
     *
     * @param in the parcel
     */
    protected Movie(Parcel in){
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.originalTitle = in.readString();
        this.voteAverage = in.readDouble();
        this.movieId = in.readLong();
    }

    /**
     * Public CREATOR field that generates instances of your Parcelable class from a Parcel.
     */
    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        /**
         * Create a new instance of the Parcelable class,
         * instantiating it from the given Parcel whose data had previously been written
         * by writeToParcel().
         *
         * @param parcel filled by writeToParcel
         * @return the new instance
         */
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        /**
         * Create a new array of the Parcelable class.
         * @param size of the array
         * @return the new array
         */
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /*******************
     ** THE ACCESSORS **
     *******************/

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }


}
