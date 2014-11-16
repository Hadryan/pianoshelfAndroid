package com.pianoshelf.joey.pianoshelf;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

/**
 * Created by root on 11/8/14.
 * This class deals with displaying a list of sheet music
 * This class reacts in a query format
 */
public class SheetListView extends ListActivity {
    // Information sent to the server
    private String SERVER;
    private String query;       // A query (i.e. composers)
    private String queryType;   // A type of query (i.e. order_by)
    private int queryListBegin;
    private int queryListSize;

    // Information received from the server
    private int listSize;

    private String nextPageUrl;
    private String prevPageUrl;

    private String SERVER_SHEETMUSIC_PREFIX = "/api/sheetmusic/";
    private String QUERY_PREFIX = "?";
    private String QUERY_TYPE_PREFIX = "order_by=";

    private String QUERY_PAGE = "page";
    private String QUERY_PAGE_SIZE = "page_size";

    private String QUERY_ADD_ARG = "&";
    private String QUERY_ASSIGN = "=";

    private String DEFAULT_QUERY_TYPE = "order_by";
    private int DEFAULT_PAGE_BEGIN = 1;
    private int DEFAULT_PAGE_SIZE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_listview);

        // Fetch intent information
        Intent intent = getIntent();
        SERVER = intent.getStringExtra("server");
        //TODO write a function that computes this query
        query = intent.getStringExtra("query");
        queryType = intent.getStringExtra("queryType");
        queryListBegin = intent.getIntExtra("pageBegin", DEFAULT_PAGE_BEGIN);
        queryListSize = intent.getIntExtra("pageSize", DEFAULT_PAGE_SIZE);


        final Context context = getListView().getContext();
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
        JsonObjectRequest getJsonSheetList = new JsonObjectRequest
                (Request.Method.GET, jsonQueryUrl, null, new Response.Listener<JSONObject>(){
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
                            listSize = response.getInt("count");
                            //TODO implement thumbnailView
                            setListAdapter(new SheetListAdapter(context
                                    , R.layout.adapter_sheet_list_item
                                    , response.getJSONArray("results")));
                            // Cancel the progress bar
                            ((ProgressBar) findViewById(R.id.sheet_list_progress))
                                    .setVisibility(View.GONE);
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

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        Intent openSheet = new Intent(this, SheetView.class);
        openSheet.putExtra("sheetMusicUrl"
                , SERVER + SERVER_SHEETMUSIC_PREFIX + Integer.toString((int) id));
        startActivity(openSheet);
    }

    /**
     * Helper Functions
     */
    // Parse the query by type and page number
    // Example:  /api/sheetmusic/?order_by=popular&page_size=9
    private String parseQuery(String query, String queryType, int page, int pageSize) {
        return appendArguments(SERVER + SERVER_SHEETMUSIC_PREFIX
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

    /**
     * Custom adapter for a list of sheet music
     */
    private class SheetListAdapter extends JSONAdapter {
        public SheetListAdapter(Context context, int layout, JSONArray sheetList) {
            super(context, layout, sheetList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View parentView = super.getView(position, convertView, parent);
            // Unwrap the JSONObject
            JSONObject sheet;
            int id, fileSize, downloadCount, difficulty;
            String title, style, key, date, composer, submitter, thumbnailUrl, uniqueUrl;

            // Unwrapping the JSON object
            try {
                sheet = jsonArray.get(position);
                id = sheet.getInt("id");
                title = sheet.getString("title");
                style = sheet.getString("style");
                key = sheet.getString("key");
                date = sheet.getString("date");
                fileSize = sheet.getInt("file_size");
                composer = sheet.getString("composer_name");
                submitter = sheet.getString("submitted_by");
                thumbnailUrl = sheet.getString("thumbnail_url");
                downloadCount = sheet.getInt("pop");
                uniqueUrl = sheet.getString("uniqueurl");
                difficulty = sheet.getInt("difficulty");
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }

            // Populate textViews with information
            ((TextView) parentView.findViewById(R.id.sheet_list_item_title)).setText(title);
            ((TextView) parentView.findViewById(R.id.sheet_list_item_composer_name))
                    .setText(composer);
            ((TextView) parentView.findViewById(R.id.sheet_list_item_style)).setText(style);
            ((TextView) parentView.findViewById(R.id.sheet_list_item_key)).setText(key);
            ((TextView) parentView.findViewById(R.id.sheet_list_item_date)).setText(date);
            ((TextView) parentView.findViewById(R.id.sheet_list_item_download_count))
                    .setText(parseDownloadCount(downloadCount));
            TextView difficultyText =
                    (TextView) parentView.findViewById(R.id.sheet_list_item_difficulty);
            difficultyText.setText(parseDifficulty(difficulty));
            difficultyText.setTextColor(getDifficultyColor(difficulty));
            return parentView;
        }

        // Parse a difficulty integer to a difficulty string
        private String parseDifficulty(int difficulty) {
            switch (difficulty) {
                case 0: return "No Rating";
                case 1: return "Beginner";
                case 2: return "Novice";
                case 3: return "Intermediate";
                case 4: return "Advanced";
                case 5: return "Expert";
                default : throw new RuntimeException("Invalid difficulty");
            }
        }

        private int getDifficultyColor(int difficulty) {
            switch (difficulty) {
                case 0: return Color.GRAY;
                case 1: return Color.GREEN;
                case 2: return Color.CYAN;
                case 3: return Color.YELLOW;
                case 4: return Color.MAGENTA;
                case 5: return Color.RED;
                default: throw new RuntimeException("Invalid difficulty");
            }
        }

        private String parseDownloadCount(int downloadCount) {
            int thousandth = downloadCount;
            int thousandthPrev = 0;
            int counter = -1;
            while (thousandth != 0) {
                thousandthPrev = thousandth;
                thousandth = thousandth / 1000;
                ++counter;
            }
            String parsedDownloadCount = Integer.toString(thousandthPrev);
            switch (counter) {
                case -1: return parsedDownloadCount;
                case 0: return parsedDownloadCount;
                case 1: return parsedDownloadCount + 'K';
                case 2: return parsedDownloadCount + 'M';
                case 3: return parsedDownloadCount + 'B';
                default: return parsedDownloadCount + 'T';
            }
        }

    }

}
