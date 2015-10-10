package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.composition.ComposerListFragment;
import com.pianoshelf.joey.pianoshelf.ProgressFragment;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Semaphore;

/**
 * Created by joey on 12/29/14.
 */

// View for basically everything. - Responsible for swapping fragments
public class SheetListView extends BaseActivity {
    private static final String LOG_TAG = "Sheet list view";
    // Information sent to the server
    private String query;       // A query (i.e. composers)
    private String queryType;   // A type of query (i.e. order_by)
    private int queryListBegin;
    private int queryListSize;

    // Information received from the server
    private int sheetListCount;

    private String nextPageUrl;
    private String prevPageUrl;

    private final String SERVER_SHEETMUSIC_SUFFIX = "api/sheetmusic/";
    private final String QUERY_PREFIX = "?";

    private final String QUERY_PAGE = "page";
    private final String QUERY_PAGE_SIZE = "page_size";

    private final String QUERY_ADD_ARG = "&";
    private final String QUERY_ASSIGN = "=";

    private final String DEFAULT_QUERY_TYPE = "order_by";
    private final int DEFAULT_PAGE_BEGIN = 1;
    private final int DEFAULT_PAGE_SIZE = 20;

    private String composerUrl;

    // Current state of list view
    private enum SheetListState {INVALID, SHEETMUSIC, COMPOSER}
    private SheetListState mState = SheetListState.INVALID;
    private Semaphore mStateSem = new Semaphore(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_list_view);

        getSupportActionBar().setTitle("Sheet Music");

        // Fetch intent information
        Intent intent = getIntent();
        //TODO write a function that computes this query
        query = intent.getStringExtra("query");
        queryType = intent.getStringExtra("queryType");
        queryListBegin = intent.getIntExtra("pageBegin", DEFAULT_PAGE_BEGIN);
        queryListSize = intent.getIntExtra("pageSize", DEFAULT_PAGE_SIZE);
        composerUrl = intent.getStringExtra("composersUrl");

        loadSheetmusicList(query, queryType, queryListBegin, queryListSize);
    }

    public void loadSheetmusicList(String query, String queryType, int queryListBegin, int queryListSize){
        // Change state to sheetmusic
        if (mStateSem.tryAcquire()) {
            ProgressFragment progressFragment = new ProgressFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.single_frame, progressFragment).commit();

            String jsonQueryUrl;
            // Compose the queryURL by parsing the extras from the intent
            if (query.isEmpty()) {
                throw new RuntimeException("Empty query given to SheetListView.java");
            } else if (queryType.isEmpty()) {
                jsonQueryUrl = parseQuery(query, queryListBegin, queryListSize);
            } else {
                jsonQueryUrl = parseQuery(query, queryType, queryListBegin, queryListSize);
            }

            // Making the JSON request
            JsonObjectRequest getJsonSheetList = new JsonObjectRequest
                    (Request.Method.GET, jsonQueryUrl, (String) null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    mState = SheetListState.SHEETMUSIC;
                                    //Populate the list with JSON objects
                                    try {
                                        if (!(response.getString("next")).equals("null")) {
                                            nextPageUrl = response.getString("next");
                                        } else {
                                            nextPageUrl = null;
                                        }
                                        if (!(response.getString("previous")).equals("null")) {
                                            prevPageUrl = response.getString("previous");
                                        } else {
                                            prevPageUrl = null;
                                        }
                                        sheetListCount = response.getInt("count");

                                        SheetListFragment sheetList = SheetListFragment.newInstance(
                                                response.getJSONArray("results"));
                                        FragmentManager fm = getSupportFragmentManager();
                                        fm.beginTransaction().
                                                replace(R.id.single_frame, sheetList).commit();
                                        // update view before unlocking
                                        fm.executePendingTransactions();

                                        TextView sheetMusicView = (TextView) findViewById(R.id.sheetmusic_tab);
                                        sheetMusicView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                                        TextView composer = (TextView) findViewById(R.id.composer_tab);
                                        composer.setTypeface(Typeface.DEFAULT);
                                        // Update UI
                                        highlightText(mState);
                                        //TODO implement thumbnailView
                                    } catch (JSONException ex) {
                                        throw new RuntimeException(ex);
                                    } finally {
                                        mStateSem.release();
                                    }
                                    //TODO write a catch block for casting integer
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //TODO Something Here
                            Log.e(LOG_TAG, "sheetmusic request error " + error.getMessage());
                            highlightText(mState);
                            mStateSem.release();
                        }
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(getJsonSheetList);
        }
    }

    public void loadComposerList() {
        if (mStateSem.tryAcquire()) {
            ProgressFragment progressFragment = new ProgressFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.single_frame, progressFragment).commit();

            JsonArrayRequest composersRequest = new JsonArrayRequest
                    (Request.Method.GET, composerUrl, (String) null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            mState = SheetListState.COMPOSER;
                            //Populate the list with JSON objects
                            try {
                                ComposerListFragment composerList = ComposerListFragment.newInstance(
                                        response);
                                FragmentManager fm = getSupportFragmentManager();
                                fm.beginTransaction().
                                        replace(R.id.single_frame, composerList).commit();
                                // update view before unlocking
                                fm.executePendingTransactions();
                                // update UI
                                highlightText(mState);
                            } catch (Error ex) {
                                throw new RuntimeException(ex);
                            } finally {
                                mStateSem.release();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //TODO Something Here
                            Log.e(LOG_TAG, "composer request error " + error.getMessage());
                            highlightText(mState);
                            mStateSem.release();
                        }
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(composersRequest);

        }
    }

    /**
     * Helper Functions
     */
    // Parse the query by type and page number
    // Example:  /api/sheetmusic/?order_by=popular&page_size=9
    private String parseQuery(String query, String queryType, int page, int pageSize) {
        return appendArguments(SERVER_ADDR + SERVER_SHEETMUSIC_SUFFIX
                + QUERY_PREFIX + queryType + QUERY_ASSIGN + query
                , QUERY_PAGE + QUERY_ASSIGN + page
                , QUERY_PAGE_SIZE + QUERY_ASSIGN + pageSize);
    }

    private String parseQuery(String query, int page, int pageSize) {
        return parseQuery(query, DEFAULT_QUERY_TYPE, page, pageSize);
    }

    // Helper function to chain additional arguments
    private String appendArguments(String prefix, String... arguments) {
        for(String arg : arguments) {
            prefix = prefix + QUERY_ADD_ARG + arg;
        }
        return prefix;
    }

    public void loadSheetmusic(View view){
        // disable clicking until view is updated
        findViewById(R.id.sheetmusic_tab).setClickable(false);
        findViewById(R.id.composer_tab).setClickable(false);
        loadSheetmusicList(query, queryType, queryListBegin, queryListSize);
    }

    public void loadComposer(View view){
        findViewById(R.id.sheetmusic_tab).setClickable(false);
        findViewById(R.id.composer_tab).setClickable(false);
        loadComposerList();
    }


    private void highlightText(SheetListState state) {
        TextView sheetMusicView = (TextView) findViewById(R.id.sheetmusic_tab);
        TextView composer = (TextView) findViewById(R.id.composer_tab);
        switch (state) {
            case SHEETMUSIC:
                sheetMusicView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                composer.setTypeface(Typeface.DEFAULT);
                break;
            case COMPOSER:
                sheetMusicView.setTypeface(Typeface.DEFAULT);
                composer.setTypeface(Typeface.DEFAULT_BOLD);

        }
        sheetMusicView.setClickable(true);
        composer.setClickable(true);

    }
}
