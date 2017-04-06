package com.exemple.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.popularmovies.utilities.MovieDBJsonUtils;
import com.exemple.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MainActivity extends AppCompatActivity {


//    A text view for testing purposes
    private TextView mSearchResultDisplay;
    private ImageView mTestPosterDisplay;

    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mSearchResultDisplay = (TextView) findViewById(R.id.test_text_text_view);
        mErrorMessageTextView = (TextView) findViewById(R.id.error_message_tv);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator_pb);
        mTestPosterDisplay =(ImageView) findViewById(R.id.first_poster_iv);
    }


    private void loadMovieData(int queryCOde){
        showDataView();
        URL theMovieDBSearchURL = NetworkUtils.buildUrl(queryCOde);
        new FetchMovieTask().execute(theMovieDBSearchURL);


    }

    private void showDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mSearchResultDisplay.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mSearchResultDisplay.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    public class FetchMovieTask extends AsyncTask<URL,Void,String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(URL... params) {
            URL searchURL = params[0];
            String jsonMovieDBResults = null;
            try {
                jsonMovieDBResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
                String[] simplePathToPosterList = MovieDBJsonUtils.getMoviePathToPosterFromJson(MainActivity.this, jsonMovieDBResults);
                return simplePathToPosterList;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String[] moviePathToPosterList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(moviePathToPosterList != null){
                showDataView();
                mSearchResultDisplay.setText("");

                for(String pathToPosterString : moviePathToPosterList){
                    URL urlToPoster = NetworkUtils.buildURL(pathToPosterString, "w185");

                    mSearchResultDisplay.append((urlToPoster.toString()) + "\n\n");
//                    mSearchResultDisplay.append((pathToPosterString) + "\n\n");

                }

                URL urlToFirstPoster = NetworkUtils.buildURL(moviePathToPosterList[0], "w185");
                Picasso.with(MainActivity.this).load(urlToFirstPoster.toString()).into(mTestPosterDisplay);
                mTestPosterDisplay.setVisibility(View.VISIBLE);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.movie,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWhatSelected = item.getItemId();
        if(menuItemThatWhatSelected == R.id.action_sort){
            mSearchResultDisplay.setText("");
            mTestPosterDisplay.setVisibility(View.INVISIBLE);
            loadMovieData(100);
            Context context = MainActivity.this;
            String message = getString(R.string.toast_sort);
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
