package com.navispeed.greg.common.utils;

import android.support.v7.app.AppCompatActivity;

/**
 * Activity using a PageRetriever. Takes care of starting and stopping the auto-fetch.
 * You just have to create the PageRetriever instance and set the auto-fetching frequency.
 */
public abstract class PRAutoFetchingActivity extends AppCompatActivity {

    protected PageRetriever pr = null;

    protected abstract PageRetriever initPr();

    protected abstract int getAutoFetchFreq();

    @Override
    protected void onStart() {
        super.onStart();
        pr = initPr();
        pr.startAutoFetch(getAutoFetchFreq());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pr.stopAutoFetch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pr.stopAutoFetch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pr.startAutoFetch(getAutoFetchFreq());
    }

    @Override
    protected void onStop() {
        super.onStop();
        pr.stopAutoFetch();
    }

}
