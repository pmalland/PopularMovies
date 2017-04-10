package com.exemple.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private String mInfoTestingPurpose;
    private TextView mOriginalTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mOriginalTitleTextView = (TextView) findViewById(R.id.original_title_tv);

        Intent intentTriggeringDetailActivity = getIntent();
        if (intentTriggeringDetailActivity != null) {
            if (intentTriggeringDetailActivity.hasExtra(Intent.EXTRA_TEXT)) {

                mOriginalTitleTextView
                        .setText(intentTriggeringDetailActivity.getStringExtra(Intent.EXTRA_TEXT));
            }

        }
    }
}
