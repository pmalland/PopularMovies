package com.exemple.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable{

    private String key;
    private String name;
    private String site;

    /**
     * Public constructor
     */
    public Video() {}

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
        parcel.writeString(this.key);
        parcel.writeString(this.name);
        parcel.writeString(this.site);
    }

    /**
     * Private constructor
     * Unwrapping the parcel
     *
     * @param in the parcel
     */
    protected Video(Parcel in){
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
    }

    /**
     * Public CREATOR field that generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>(){

        /**
         * Create a new instance of the Parcelable class,
         * instantiating it from the given Parcel whose data had previously been written
         * by writeToParcel().
         *
         * @param parcel filled by writeToParcel
         * @return the new instance
         */
        @Override
        public Video createFromParcel(Parcel parcel) {
            return new Video(parcel);
        }

        /**
         * Create a new array of the Parcelable class.
         * @param size of the array
         * @return the new array
         */
        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    /*******************
     ** THE ACCESSORS **
     *******************/

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
