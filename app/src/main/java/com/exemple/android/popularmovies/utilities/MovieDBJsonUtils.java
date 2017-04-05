package com.exemple.android.popularmovies.utilities;


import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDBJsonUtils {

    private static final String MDB_PAGE = "page";
    private static final String MDB_RESULTS = "results";

    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_ADULT = "adult";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_RELEASE_DATE = "release_date";
    private static final String MDB_GENRE_IDS = "genre_ids";
    private static final String MDB_ID = "id";
    private static final String MDB_ORIGINAL_TITLE = "original_title";
    private static final String MDB_ORIGINAL_LANGUAGE = "original_language";
    private static final String MDB_TITLE = "title";
    private static final String MDB_BACKDROP_PATH = "backdrop_path";
    private static final String MDB_POPULARITY = "popularity";
    private static final String MDB_VOTE_COUNT = "vote_count";
    private static final String MDB_VIDEO = "video";
    private static final String MDB_VOTE_AVERAGE = "vote_average";

    private static final String MDB_TOTAL_RESULTS = "total_results";
    private static final String MDB_TOTAL_PAGES = "total_pages";


    public static String[] getMoviePathToPosterFromJson(Context context, String movieDBJsonStr)
        throws JSONException {

        JSONObject movieDBJson = new JSONObject(movieDBJsonStr);

        //What would be an unambiguous flag for an error JSON ?

        JSONArray jsonMovieArray = movieDBJson.getJSONArray(MDB_RESULTS);

        String[] movieDBPathToPoster = new String[jsonMovieArray.length()];

        for (int i = 0; i < jsonMovieArray.length(); i++){
            String posterPath;

            JSONObject movie = jsonMovieArray.getJSONObject(i);

            posterPath = movie.getString(MDB_POSTER_PATH);
            movieDBPathToPoster[i] = posterPath;
        }
        return movieDBPathToPoster;
    }

    public static ContentValues[] getMovieContentValuesFromJson(Context context, String movieDBJsonStr)
        throws JSONException {

        JSONObject movieDBJson = new JSONObject(movieDBJsonStr);

        // What would be an unambiguous flag for an error JSON ?

        JSONArray jsonMovieArray = movieDBJson.getJSONArray(MDB_RESULTS);

        ContentValues[] movieDBContentValues = new ContentValues[jsonMovieArray.length()];

        for (int i = 0; i < jsonMovieArray.length(); i++) {

            String posterPath;
            boolean adult;
            String overview;
            String release_date;
//            int[] genreIds;
            String genreIds = "";
            int id;
            String originalTitle;
            String originalLanguage;
            String title;
            String backdropPath;
            double popularity;
            double voteCount;
            boolean video;
            double voteAverage;

            /*Get the JSON object representing the movie */
            JSONObject movie = jsonMovieArray.getJSONObject(i);

            posterPath = movie.getString(MDB_POSTER_PATH);
            adult = movie.getBoolean(MDB_ADULT);
            overview = movie.getString(MDB_OVERVIEW);
            release_date = movie.getString(MDB_RELEASE_DATE);

//            JSONArray genreIdObject = movie.getJSONArray(MDB_GENRE_IDS);
//            genreIds = new int[genreIdObject.length()];
//            for (int j = 0; j < genreIdObject.length(); j++){
//                genreIds[j] = genreIdObject.getInt(j);
//            }

            JSONArray genreIdObject = movie.getJSONArray(MDB_GENRE_IDS);
            for (int j = 0; j < genreIdObject.length(); j++){
                genreIds += genreIdObject.getInt(j);
            }

            id = movie.getInt(MDB_ID);
            originalTitle = movie.getString(MDB_ORIGINAL_TITLE);
            originalLanguage = movie.getString(MDB_ORIGINAL_LANGUAGE);
            title = movie.getString(MDB_TITLE);
            backdropPath = movie.getString(MDB_BACKDROP_PATH);
            popularity = movie.getDouble(MDB_POPULARITY);
            voteCount = movie.getDouble(MDB_VOTE_COUNT);
            video = movie.getBoolean(MDB_VIDEO);
            voteAverage = movie.getDouble(MDB_VOTE_AVERAGE);

            ContentValues movieValues = new ContentValues();
            movieValues.put(MDB_POSTER_PATH,posterPath);
            movieValues.put(MDB_ADULT,adult);
            movieValues.put(MDB_OVERVIEW,overview);
            movieValues.put(MDB_RELEASE_DATE,release_date);
            movieValues.put(MDB_GENRE_IDS,genreIds);
            movieValues.put(MDB_ID,id);
            movieValues.put(MDB_ORIGINAL_TITLE,originalTitle);
            movieValues.put(MDB_ORIGINAL_LANGUAGE,originalLanguage);
            movieValues.put(MDB_TITLE,title);
            movieValues.put(MDB_BACKDROP_PATH,backdropPath);
            movieValues.put(MDB_POPULARITY,popularity);
            movieValues.put(MDB_VOTE_COUNT,voteCount);
            movieValues.put(MDB_VIDEO,video);
            movieValues.put(MDB_VOTE_AVERAGE,voteAverage);

            movieDBContentValues[i] = movieValues;
        }
    return movieDBContentValues;
    }

}
/*
Response for a single movie searh
        https://api.themoviedb.org/3/search/movie?api_key={api_key}&query=Jack+Reacher

        {
        "poster_path": "/IfB9hy4JH1eH6HEfIgIGORXi5h.jpg",
        "adult": false,
        "overview": "Jack Reacher must uncover the truth behind a major government conspiracy in order to clear his name. On the run as a fugitive from the law, Reacher uncovers a potential secret from his past that could change his life forever.",
        "release_date": "2016-10-19",
        "genre_ids": [
        53,
        28,
        80,
        18,
        9648
        ],
        "id": 343611,
        "original_title": "Jack Reacher: Never Go Back",
        "original_language": "en",
        "title": "Jack Reacher: Never Go Back",
        "backdrop_path": "/4ynQYtSEuU5hyipcGkfD6ncwtwz.jpg",
        "popularity": 26.818468,
        "vote_count": 201,
        "video": false,
        "vote_average": 4.19
        }

the id : 343611 item is used for querying the remaining details
        https://api.themoviedb.org/3/movie/343611?api_key={api_key}
*/
/*

The popular query return a JSON like that :

        "page": 1,
        "results": [{
        "poster_path": "\/tWqifoYuwLETmmasnGHO7xBjEtt.jpg",
        "adult": false,
        "overview": "A live-action adaptation of Disney's version of the classic 'Beauty and the Beast' tale of a cursed prince and a beautiful young woman who helps him break the spell.",
        "release_date": "2017-03-17",
        "genre_ids": [14,
        10402,
        10749],
        "id": 321612,
        "original_title": "Beauty and the Beast",
        "original_language": "en",
        "title": "Beauty and the Beast",
        "backdrop_path": "\/6aUWe0GSl69wMTSWWexsorMIvwU.jpg",
        "popularity": 180.45132,
        "vote_count": 1246,
        "video": false,
        "vote_average": 7.1
        },
        {
        "poster_path": "\/45Y1G5FEgttPAwjTYic6czC9xCn.jpg",
        "adult": false,
        "overview": "In the near future, a weary Logan cares for an ailing Professor X in a hide out on the Mexican border. But Logan's attempts to hide from the world and his legacy are up-ended when a young mutant arrives, being pursued by dark forces.",
        "release_date": "2017-02-28",
        "genre_ids": [28,
        18,
        878],
        "id": 263115,
        "original_title": "Logan",
        "original_language": "en",
        "title": "Logan",
        "backdrop_path": "\/5pAGnkFYSsFJ99ZxDIYnhQbQFXs.jpg",
        "popularity": 117.369877,
        "vote_count": 2075,
        "video": false,
        "vote_average": 7.6
        },

....
....
        "total_results": 19481,
        "total_pages": 975 */
