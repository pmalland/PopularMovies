package com.exemple.android.popularmovies.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.exemple.android.popularmovies.R;

public class MoviePreferences {


    private static String getDefaultSortingCriterion(Context context){
        return context.getString(R.string.pref_sort_criterion_default);
    }

    public static String getPreferredSortingCriterion(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortingCriterion = context.getString(R.string.pref_sort_criterion_key);

        return sp.getString(keyForSortingCriterion,getDefaultSortingCriterion(context));
    }

    public static void setPreferredSortingCriterion(Context context, String newCriterion){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForSortingCriterion = context.getString(R.string.pref_sort_criterion_key);

        editor.putString(keyForSortingCriterion,newCriterion);
        editor.apply();


    }


    public static void resetPreferredSortingCriterion(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForSortingCriterion = context.getString(R.string.pref_sort_criterion_key);

        editor.remove(keyForSortingCriterion);
        editor.apply();

    }

    private static String getDefaultPosterResolution(Context context){
        return context.getString(R.string.pref_poster_resolution_default);
    }

    public static String getPreferredPosterResolution(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForPosterResolution = context.getString(R.string.pref_poster_resolution_key);

        return sp.getString(keyForPosterResolution,getDefaultPosterResolution(context));
    }

    public static void setPreferredPosterResolution(Context context, String newPosterResolution){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForPosterResolution = context.getString(R.string.pref_poster_resolution_key);

        editor.putString(keyForPosterResolution,newPosterResolution);
        editor.apply();


    }


    public static void resetPreferredPosterResolution(Context context){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForPosterResolution = context.getString(R.string.pref_poster_resolution_key);

        editor.remove(keyForPosterResolution);
        editor.apply();

    }

}


