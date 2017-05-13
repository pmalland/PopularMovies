package com.exemple.android.popularmovies.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.exemple.android.popularmovies.R;

/**
 * Helper methods for preferences handling
 */
public class MoviePreferences {

    /**
     * Return the default sorting criterion
     *
     * @param context Context used to access the Preferences
     * @return the default sorting criterion
     */
    private static String getDefaultSortingCriterion(Context context){
        return context.getString(R.string.pref_sort_criterion_default);
    }

    /**
     * Return the sorting criterion set in Preference
     *
     * @param context Context used to accessPreference
     * @return the sorting criterion
     */
    public static String getPreferredSortingCriterion(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortingCriterion = context.getString(R.string.pref_sort_criterion_key);

        return sp.getString(keyForSortingCriterion,getDefaultSortingCriterion(context));
    }

    /**
     * Setting the preference for the sorting criterion
     *
     * @param context Context used to access Preference
     * @param newCriterion the new criterion to save in Preference
     */
    public static void setPreferredSortingCriterion(Context context, String newCriterion){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForSortingCriterion = context.getString(R.string.pref_sort_criterion_key);

        editor.putString(keyForSortingCriterion,newCriterion);
        editor.apply();
    }

    /**
     * Reset the sorting preference
     *
     * @param context Context used to access Preference
     */
    public static void resetPreferredSortingCriterion(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForSortingCriterion = context.getString(R.string.pref_sort_criterion_key);

        editor.remove(keyForSortingCriterion);
        editor.apply();

    }

    /**
     * Accessor for poster resolution
     *
     * @param context Context used to access Preference
     * @return default poster resolution
     */
    private static String getDefaultPosterResolution(Context context){
        return context.getString(R.string.pref_poster_resolution_default);
    }

    /**
     * Accessor for poster resolution
     *
     * @param context Context used to access Preference
     * @return poster resolution
     */
    public static String getPreferredPosterResolution(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForPosterResolution = context.getString(R.string.pref_poster_resolution_key);

        return sp.getString(keyForPosterResolution,getDefaultPosterResolution(context));
    }

    /**
     * Setter for poster resolution preference
     *
     * @param context Context used to access Preference
     * @param newPosterResolution new poster resolution
     */
    public static void setPreferredPosterResolution(Context context, String newPosterResolution){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForPosterResolution = context.getString(R.string.pref_poster_resolution_key);

        editor.putString(keyForPosterResolution,newPosterResolution);
        editor.apply();


    }

    /**
     * Return to default poster resolution
     *
     * @param context Context used to access Preference
     */
    public static void resetPreferredPosterResolution(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForPosterResolution = context.getString(R.string.pref_poster_resolution_key);

        editor.remove(keyForPosterResolution);
        editor.apply();

    }

}


