package com.pianoshelf.joey.pianoshelf;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Each list item within the listActivity should have a constant height in dp
 * Created by root on 11/6/14.
 */
public class ComposerView extends ListActivity {
    private String composerUrl;
    private String server;
    private String sheetMusicEndpoint;
    private int composerCount;
    private JSONArray composers;
    public static final String composerDescription = "A Romantic Composer born in Germany in 1885.";
    private final String QUERY_TYPE = "composer_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composerview);

        // Get information from intent
        Intent intent = getIntent();
        server = intent.getStringExtra("server");
        composerUrl = intent.getStringExtra("composersUrl");
        sheetMusicEndpoint = intent.getStringExtra("sheetMusicEndpoint");

        // Request to get the array of composers
        JsonObjectRequest composersRequest = new JsonObjectRequest
                (composerUrl, null, new Response.Listener<JSONObject>(){
                    public void onResponse(JSONObject response){
                        try {
                            //TODO implement infinite scrolling for next/prev pages
                            composerCount = response.getInt("count");
                            composers = response.getJSONArray("results");
                            setListAdapter(new ComposerAdapter(getListView().getContext(),
                                    R.layout.adapter_composerview_item, composers));
                        } catch (JSONException ex) {
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

    @Override
    protected void onListItemClick (ListView listview, View view, int position, long id){
        // Example:  /api/sheetmusic/?composer_name=Chopin&page_size=200
        Intent getSheetsByComposer = new Intent(this, SheetListView.class);
        getSheetsByComposer.putExtra("query"
                , ((TextView) view.findViewById(R.id.name)).getText());
        getSheetsByComposer.putExtra("queryType", QUERY_TYPE);
        //TODO order by popularity, not achievable currently
        startActivity(getSheetsByComposer);
    }


    // Custom adapter class to handle populating each row
    private class ComposerAdapter extends JSONAdapter {
        public ComposerAdapter(Context context, int layout, JSONArray composers) {
            super(context, layout, composers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View parentView = super.getView(position, convertView, parent);
            // Unwrap the JSONObject
            JSONObject composer;
            String composerPortraitUrl;
            String composerName;
            try {
                composer = jsonArray.get(position);
                composerPortraitUrl = server + composer.getString("thumbnail_path");
                Log.i("URL", composerPortraitUrl);
                composerName = composer.getString("full_name");
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }

            // Set the portrait image
            final ImageView portraitImage = (ImageView) parentView.findViewById(R.id.portrait);

            ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();
            // TODO This command should utilize caching, currently there is noticeable lag when recycling list items
            imageLoader.get(composerPortraitUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        portraitImage.setImageBitmap(response.getBitmap());
                    } else {
                        //TODO display error image or message
                        //imageView.setImageResource();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Load an error image or display an error dialog
                }
                // Set the height and width of the image here for resizing
            }, portraitImage.getHeight(), portraitImage.getHeight());
            // Set the information for the text columns
            ((TextView) parentView.findViewById(R.id.name)).setText(composerName);
            // TODO replace the static string with an actual description once the api is updated
            ((TextView) parentView.findViewById(R.id.description))
                    .setText(ComposerView.composerDescription);
            return parentView;
        }
    }
}
