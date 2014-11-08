package com.pianoshelf.joey.pianoshelf;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Each list item within the listActivity should have a constant height in dp
 * Created by root on 11/6/14.
 */
public class ComposerView extends ListActivity {
    private String composerUrl;
    private String server;
    private JSONArray composers;
    public static final String composerDescription = "A Romantic Composer born in Germany in 1885.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composerview);

        Intent intent = getIntent();

        server = intent.getStringExtra("server");
        composerUrl = intent.getStringExtra("composersUrl");
        final Context context = getListView().getContext();

        // Request to get the array of composers
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (composerUrl, new Response.Listener<JSONArray>(){
                    public void onResponse(JSONArray response){
                        composers = response;
                        setListAdapter(new ComposerAdapter(context, R.layout.activity_composerview_item, composers));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO Something Here
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    // Custom adapter class to handle populating each row
    private class ComposerAdapter extends BaseAdapter {
        private Context context;
        private List<JSONObject> composers;
        private int layout = R.layout.activity_composerview_item;

        public ComposerAdapter(JSONArray composers) {
            this.composers = new ArrayList<JSONObject>();
            for(int i=0;i<composers.length();++i) {
                try {
                    this.composers.add(composers.getJSONObject(i));
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        public ComposerAdapter(Context context, int layout, JSONArray composers) {
            this.context = context;
            this.layout = layout;
            this.composers = new ArrayList<JSONObject>();
            for(int i=0;i<composers.length();++i) {
                try {
                    this.composers.add(composers.getJSONObject(i));
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Unwrap the JSONObject
            JSONObject composer;
            String composerPortraitUrl;
            String composerName;
            try {
                composer = composers.get(position);
                composerPortraitUrl = server + composer.getString("thumbnail_path");
                composerName = composer.getString("full_name");
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }

            // ConvertView is the view being recycled as one item in the listView goes out of screen
            // Only instantiate the convertView when its null
            if (convertView == null) {
                convertView = ((LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(layout, parent, false);
            }

            // Set the portrait image
            final ImageView portraitImage = (ImageView) convertView.findViewById(R.id.portrait);

            ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();
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
            ((TextView) convertView.findViewById(R.id.name)).setText(composerName);
            // TODO replace the static string with an actual description once the api is updated
            ((TextView) convertView.findViewById(R.id.description))
                    .setText(ComposerView.composerDescription);
            return convertView;
        }

        @Override
        public int getCount(){
            return composers.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return composers.get(position);
        }

        @Override
        // There may not be a trivial 1 to 1 mapping between the itemId
        // in the adapter versus the backend storage, this is the mapping function
        public long getItemId(int position) {
            try {
                JSONObject composer = composers.get(position);
                return composer.getInt("id");
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
