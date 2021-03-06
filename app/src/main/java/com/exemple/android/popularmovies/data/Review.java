package com.exemple.android.popularmovies.data;


import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable{

    private String id;
    private String author;
    private String content;

    /**
     * Public constructor
     */
    public Review(){}

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
        parcel.writeString(this.id);
        parcel.writeString(this.author);
        parcel.writeString(this.content);
    }

    /**
     * Private constructor
     * Unwrapping the parcel
     *
     * @param in the parcel
     */
    protected Review(Parcel in){
        this.id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
    }

    /**
     * Public CREATOR field that generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){

        /**
         * Create a new instance of the Parcelable class,
         * instantiating it from the given Parcel whose data had previously been written
         * by writeToParcel().
         *
         * @param parcel filled by writeToParcel
         * @return the new instance
         */
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        /**
         * Create a new array of the Parcelable class.
         * @param size of the array
         * @return the new array
         */
        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    /*******************
     ** THE ACCESSORS **
     *******************/

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
