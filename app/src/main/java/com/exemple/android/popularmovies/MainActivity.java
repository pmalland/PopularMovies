package com.exemple.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.popularmovies.utilities.MovieDBJsonUtils;
import com.exemple.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener{


//    Number of columns handled by the Grid Layout Manader
    private static final int SPAN_COUNT = 3;

    private MovieAdapter mMovieAdapter;

    private RecyclerView mMovieListRecyclerView;



    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mMovieListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movie);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),SPAN_COUNT);

        mMovieListRecyclerView.setLayoutManager(layoutManager);
        mMovieListRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);

        mMovieListRecyclerView.setAdapter(mMovieAdapter);

        mErrorMessageTextView = (TextView) findViewById(R.id.error_message_tv);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator_pb);

    }


    private void loadMovieData(int queryCOde){
        showDataView();
        URL theMovieDBSearchURL = NetworkUtils.buildUrl(queryCOde);
        new FetchMovieTask().execute(theMovieDBSearchURL);


    }

    private void showDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieListRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
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
        protected void onPostExecute(String[] moviePathToPosterListStr) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(moviePathToPosterListStr != null){
                showDataView();
//                mSearchResultDisplay.setText("");
//                String[] moviePathToPosterUrlStr = new String[moviePathToPosterListStr.length];
//                for(String pathToPosterString : moviePathToPosterListStr){
//                    URL urlToPoster = NetworkUtils.buildURL(pathToPosterString, "w185");
//
//                    mSearchResultDisplay.append((urlToPoster.toString()) + "\n\n");
////                    mSearchResultDisplay.append((pathToPosterString) + "\n\n");
//
//                }
                mMovieAdapter.setPathToPoster(moviePathToPosterListStr,"w342");
//                URL urlToFirstPoster = NetworkUtils.buildURL(moviePathToPosterListStr[0], "w185");
//                Picasso.with(MainActivity.this).load(urlToFirstPoster.toString()).into(mTestPosterDisplay);
//                mTestPosterDisplay.setVisibility(View.VISIBLE);
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
//            mSearchResultDisplay.setText("");
//            mTestPosterDisplay.setVisibility(View.INVISIBLE);
            loadMovieData(100);
            Context context = MainActivity.this;
            String message = getString(R.string.toast_sort);
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if(mToast != null){
            mToast.cancel();
        }

        String toastMessage = "item #" + clickedItemIndex + " clicked";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);

        mToast.show();
    }

}
