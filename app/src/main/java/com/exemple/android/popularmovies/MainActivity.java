package com.exemple.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


//    A text view for testing purposes
    private TextView mSearchResultDisplay;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mSearchResultDisplay = (TextView) findViewById(R.id.test_text_text_view);
        mErrorMessageTextView = (TextView) findViewById(R.id.error_message_tv);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator_pb);
    }


    private void makeTheMovieDBSearchQuery(int queryCOde){
        URL theMovieDBSearchURL = NetworkUtils.buildUrl(queryCOde);
        new MovieDBQueryTask().execute(theMovieDBSearchURL);


    }

    private void showDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mSearchResultDisplay.setVisibility(View.VISIBLE);
    }

    private void shorErrorMessage(){
        mSearchResultDisplay.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    public class MovieDBQueryTask extends AsyncTask<URL,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchURL = params[0];
            String movieDBSearchResults = null;
            try {
                movieDBSearchResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
            } catch (IOException e){
                e.printStackTrace();
            }

            return movieDBSearchResults;
        }

        @Override
        protected void onPostExecute(String movieDBSearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieDBSearchResults != null && !movieDBSearchResults.equals("")){
                mSearchResultDisplay.setText(movieDBSearchResults);
            } else {
                shorErrorMessage();
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
            makeTheMovieDBSearchQuery(100);
            Context context = MainActivity.this;
            String message = getString(R.string.toast_sort);
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
