package com.pianoshelf.joey.pianoshelf;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joey on 12/29/14.
 */

// View for basically everything. - Responsible for swapping fragments
public class SheetListView extends BaseActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_frame);
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
        ProgressFragment progressFragment = new ProgressFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.single_frame, progressFragment).commit();

        String jsonQueryUrl;
        // Compose the queryURL by parsing the extras from the intent
        if (query.isEmpty()) {
            throw new RuntimeException("Empty query given to SheetListView.java");
        } else if (queryType.isEmpty()){
            jsonQueryUrl = parseQuery(query, queryListBegin, queryListSize);
        } else {
            jsonQueryUrl = parseQuery(query, queryType, queryListBegin, queryListSize);
        }

        // Making the JSON request
        JsonObjectRequest getJsonSheetList = new JsonObjectRequest
                (Request.Method.GET, jsonQueryUrl, (String) null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
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
                            getSupportFragmentManager().beginTransaction().
                                    replace(R.id.single_frame, sheetList).commit();
                            //TODO implement thumbnailView
                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }
                        //TODO write a catch block for casting integer
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO Something Here
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(getJsonSheetList);
    }

    public void loadComposerList() {
        ProgressFragment progressFragment = new ProgressFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.single_frame, progressFragment).commit();

        JsonArrayRequest composersRequest = new JsonArrayRequest
                (Request.Method.GET, composerUrl, (String) null, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        //Populate the list with JSON objects
                        try {
                            ComposerListFragment composerList = ComposerListFragment.newInstance(
                                    response);
                            getSupportFragmentManager().beginTransaction().
                                    replace(R.id.single_frame, composerList).commit();

                        } catch (Error ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO Something Here
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(composersRequest);
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
        loadSheetmusicList(query, queryType, queryListBegin, queryListSize);
        TextView sheetMusicView = (TextView) findViewById(R.id.sheetmusic_tab);
        sheetMusicView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
        TextView composer = (TextView) findViewById(R.id.composer_tab);
        composer.setTypeface(Typeface.DEFAULT);
    }

    public void loadComposer(View view){
        loadComposerList();
        TextView sheetMusicView = (TextView) findViewById(R.id.sheetmusic_tab);
        sheetMusicView.setTypeface(Typeface.DEFAULT);
        TextView composer = (TextView) findViewById(R.id.composer_tab);
        composer.setTypeface(Typeface.DEFAULT_BOLD);
    }

}
