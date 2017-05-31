package com.exemple.android.popularmovies.utilities;


import android.content.ContentValues;
import android.util.Log;

import com.exemple.android.popularmovies.data.Movie;
import com.exemple.android.popularmovies.data.MovieDetails;
import com.exemple.android.popularmovies.data.MovieListContract;
import com.exemple.android.popularmovies.data.Review;
import com.exemple.android.popularmovies.data.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDBJsonUtils {

    private static final String MDB_RESULTS = "results";

    /********************************
     ** USED IN MAIN ACTIVITY JSON **
     ********************************/

    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_RELEASE_DATE = "release_date";
    private static final String MDB_MOVIE_ID = "id";
    private static final String MDB_ORIGINAL_TITLE = "original_title";
    private static final String MDB_VOTE_AVERAGE = "vote_average";

    /**********************************
     ** USED IN DETAIL ACTIVITY JSON **
     **********************************/

    private static final String MDB_REVIEWS ="reviews";
    private static final String MDB_REVIEW_ID ="id";
    private static final String MDB_AUTHOR ="author";
    private static final String MDB_CONTENT ="content";

    private static final String MDB_VIDEOS ="videos";
    private static final String MDB_KEY ="key";
    private static final String MDB_NAME ="name";
    private static final String MDB_SITE ="site";

    /**
     * Parsing the raw HTTP result into a JSON, we can then access the movies data
     * and store them in a convenient ContentValues []
     *
     * @param movieDBJsonStr raw response from the server in a string
     * @return the structured data in a ContentValues
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getMovieContentValuesFromJson(String movieDBJsonStr)
        throws JSONException {

        JSONObject movieDBJson = new JSONObject(movieDBJsonStr);

        JSONArray jsonMovieArray = movieDBJson.getJSONArray(MDB_RESULTS);

        ContentValues[] movieDBContentValues = new ContentValues[jsonMovieArray.length()];

        for (int i = 0; i < jsonMovieArray.length(); i++) {

            String posterPath;
            String overview;
            String release_date;
            String originalTitle;
            double voteAverage;
            int movieId;

            /*Get the JSON object representing the movie */
            JSONObject movie = jsonMovieArray.getJSONObject(i);

            posterPath = movie.getString(MDB_POSTER_PATH);
            overview = movie.getString(MDB_OVERVIEW);
            release_date = movie.getString(MDB_RELEASE_DATE);
            originalTitle = movie.getString(MDB_ORIGINAL_TITLE);
            voteAverage = movie.getDouble(MDB_VOTE_AVERAGE);
            movieId = movie.getInt(MDB_MOVIE_ID);

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_POSTER_PATH,posterPath);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_OVERVIEW,overview);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_RELEASE_DATE,release_date);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_ORIGINAL_TITLE,originalTitle);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_VOTE_AVERAGE,voteAverage);
            movieValues.put(MovieListContract.MovieListEntry.COLUMN_MOVIE_ID,movieId);

            movieDBContentValues[i] = movieValues;
        }
    return movieDBContentValues;
    }

    /**
     * Parsing the raw HTTP result into a JSON, we can then access the movies data
     * and store them in a convenient ArrayList<Movie>
     *
     * @param movieDBJsonStr raw response from the server in a string
     * @return the structured data in an ArrayList<Movie>
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<Movie> getMovieArrayListFromJson(String movieDBJsonStr)
        throws JSONException {

        JSONObject movieDBJson = new JSONObject(movieDBJsonStr);

        JSONArray jsonMovieArray = movieDBJson.getJSONArray(MDB_RESULTS);

        ArrayList<Movie> movieDBMovieList = new ArrayList<>();

        for (int i = 0; i < jsonMovieArray.length(); i++) {

            String posterPath;
            String overview;
            String release_date;
            String originalTitle;
            double voteAverage;
            int movieId;

            /*Get the JSON object representing the movie */
            JSONObject movie = jsonMovieArray.getJSONObject(i);

            posterPath = movie.getString(MDB_POSTER_PATH);
            overview = movie.getString(MDB_OVERVIEW);
            release_date = movie.getString(MDB_RELEASE_DATE);
            originalTitle = movie.getString(MDB_ORIGINAL_TITLE);
            voteAverage = movie.getDouble(MDB_VOTE_AVERAGE);
            movieId = movie.getInt(MDB_MOVIE_ID);

            Movie movieData = new Movie();
            movieData.setPosterPath(posterPath);
            movieData.setOverview(overview);
            movieData.setReleaseDate(release_date);
            movieData.setOriginalTitle(originalTitle);
            movieData.setVoteAverage(voteAverage);
            movieData.setMovieId(movieId);

            movieDBMovieList.add(movieData);
        }
        return movieDBMovieList;

    }

    public static MovieDetails getMovieDetailsFromDetailJson(String movieDetailsJsonStr)
            throws JSONException {
        JSONObject movieDetailsJson = new JSONObject(movieDetailsJsonStr);
        JSONObject reviewsJson = movieDetailsJson.getJSONObject(MDB_REVIEWS);
        JSONArray reviewsArray = reviewsJson.getJSONArray(MDB_RESULTS);

        ArrayList<Review> reviewArrayList = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++){

            String id;
            String author;
            String content;

            /*Get the JSON object representing the review */

            JSONObject review = reviewsArray.getJSONObject(i);

            id = review.getString(MDB_REVIEW_ID);
            author = review.getString(MDB_AUTHOR);
            content = review.getString(MDB_CONTENT);

            Review reviewData = new Review();
            reviewData.setId(id);
            reviewData.setAuthor(author);
            Log.i("JsonUtilReview", reviewData.getAuthor());
            reviewData.setContent(content);

            reviewArrayList.add(reviewData);
        }

        JSONObject videosJson = movieDetailsJson.getJSONObject(MDB_VIDEOS);
        JSONArray videoArray = videosJson.getJSONArray(MDB_RESULTS);

        ArrayList<Video> videoArrayList = new ArrayList<>();
        for (int j = 0; j < videoArray.length(); j++){

            String key;
            String name;
            String site;

            JSONObject firstTrailerInArray = videoArray.getJSONObject(j);
            key = firstTrailerInArray.getString(MDB_KEY);
            name = firstTrailerInArray.getString(MDB_NAME);;
            site = firstTrailerInArray.getString(MDB_SITE);

            Video video = new Video();
            video.setKey(key);
            video.setName(name);
            Log.i("JsonUtilVideo", video.getName());
            video.setSite(site);

            videoArrayList.add(video);
        }


        MovieDetails movieDetails = new MovieDetails();
        movieDetails.setVideos(videoArrayList);
        movieDetails.setReviews(reviewArrayList);

        return movieDetails;
    }
}
