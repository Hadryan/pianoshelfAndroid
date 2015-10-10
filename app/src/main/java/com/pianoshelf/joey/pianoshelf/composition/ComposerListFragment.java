package com.pianoshelf.joey.pianoshelf.composition;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.JsonArray;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.JSONAdapter;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ryan on 2015-06-26.
 */
public class ComposerListFragment extends android.support.v4.app.ListFragment {
    private JSONArray composers;
    private String server;
    private static final String JSON_ARRAY = "JSONARRAY";
    private static final String LOG_TAG = "ComposerListFragment";
    private JSONArray jsonArray;

    public ComposerListFragment() {}

    public static ComposerListFragment newInstance(JSONArray jsonArray) {
        ComposerListFragment sheetList = new ComposerListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    public static ComposerListFragment newInstance(JsonArray jsonArray) {
        ComposerListFragment sheetList = new ComposerListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                jsonArray = new JSONArray(getArguments().getString(JSON_ARRAY));
                System.out.println(jsonArray);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, ex.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_composerview, container, false);
        setListAdapter(new ComposerAdapter(getActivity(), R.layout.adapter_composerview_item,
                jsonArray));
        return view;
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
                composerPortraitUrl = C.SERVER_ADDR + composer.getString("thumbnail_path");
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
