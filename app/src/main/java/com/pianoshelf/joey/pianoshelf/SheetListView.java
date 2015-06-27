package com.pianoshelf.joey.pianoshelf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

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

    private String SERVER_SHEETMUSIC_SUFFIX = "api/sheetmusic/";
    private String QUERY_PREFIX = "?";

    private String QUERY_PAGE = "page";
    private String QUERY_PAGE_SIZE = "page_size";

    private String QUERY_ADD_ARG = "&";
    private String QUERY_ASSIGN = "=";

    private String DEFAULT_QUERY_TYPE = "order_by";
    private int DEFAULT_PAGE_BEGIN = 1;
    private int DEFAULT_PAGE_SIZE = 20;


    private String composerUrl;
    private String server;
    private String sheetMusicEndpoint;

    private int composerCount;
    private JSONArray composers;
    public static final String composerDescription = "A Romantic Composer born in Germany in 1885.";
    private String QUERY_TYPE = "composer_name";

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

        server = intent.getStringExtra("server");
        composerUrl = intent.getStringExtra("composersUrl");
        sheetMusicEndpoint = intent.getStringExtra("sheetMusicEndpoint");

        loadSheetmusicList(query, queryType, queryListBegin, queryListSize);
    }

    public void loadSheetmusicList(String query, String queryType, int queryListBegin, int queryListSize){
        ProgressFragment progressFragment = new ProgressFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.single_frame, progressFragment).commit();

        String jsonQueryUrl = "";
        // Compose the queryURL by parsing the extras from the intent
        if (query.isEmpty()) {
            throw new RuntimeException("Empty query given to SheetListView.java");
        } else if (queryType.isEmpty()){
            jsonQueryUrl = parseQuery(query, queryListBegin, queryListSize);
        } else {
            jsonQueryUrl = parseQuery(query, queryType, queryListBegin, queryListSize);
        }

        // Making the JSON request
        Log.e("RyanLog", jsonQueryUrl);
        JsonObjectRequest getJsonSheetList = new JsonObjectRequest
                (Request.Method.GET, jsonQueryUrl, null, new Response.Listener<JSONObject>(){
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

                            ((ProgressBar) findViewById(R.id.single_frame_progress))
                                    .setVisibility(View.GONE);
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
                (Request.Method.GET, composerUrl, null, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        //Populate the list with JSON objects
                        try {
                            Log.e("RyanLog", "test2");
                            System.out.println(response);

                            ((ProgressBar) findViewById(R.id.single_frame_progress))
                                    .setVisibility(View.GONE);
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
        Log.e("Test", "loadSheetmusic");
        Button button = (Button) view.findViewById(R.id.sheetmusic_tab);
        loadSheetmusicList(query, queryType, queryListBegin, queryListSize);
    }

    public void loadComposer(View view){
        Log.e("Test", "loadComposer");
        // Load SheetList Fragment
        loadComposerList();
    }

}
