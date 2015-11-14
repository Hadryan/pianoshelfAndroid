package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.JSONAdapter;
import com.pianoshelf.joey.pianoshelf.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 11/8/14.
 * This class deals with displaying a list of sheet music
 * This class reacts in a query format
 */
public class SheetArrayListFragment extends android.support.v4.app.ListFragment {
    private final String SERVER_SHEETMUSIC_SUFFIX = "api/sheetmusic/";

    private static final String JSON_ARRAY = "JSONARRAY";
    private static final String LOG_TAG = "SheetArrayListFragment";
    private JSONArray jsonArray;
    private int deleteButtonVisibility;

    public SheetArrayListFragment() {}

    public static SheetArrayListFragment newInstance(JSONArray jsonArray) {
        SheetArrayListFragment sheetList = new SheetArrayListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    public static SheetArrayListFragment newInstance(JsonArray jsonArray) {
        SheetArrayListFragment sheetList = new SheetArrayListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    public static SheetArrayListFragment newInstance(String jsonArray) {
        SheetArrayListFragment sheetList = new SheetArrayListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray);
        sheetList.setArguments(args);
        return sheetList;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                jsonArray = new JSONArray(getArguments().getString(JSON_ARRAY));
            } catch (JSONException ex) {
                Log.d(LOG_TAG, ex.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sheet_listview, container, false);
        setListAdapter(new SheetListAdapter(getActivity(), R.layout.adapter_sheet_list_item_3,
                jsonArray));
        return view;
    }

    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        Intent openSheet = new Intent(getActivity(), SheetView.class);
        openSheet.putExtra("sheetMusicUrl",
                C.SERVER_ADDR + SERVER_SHEETMUSIC_SUFFIX + String.valueOf(id));
        startActivity(openSheet);
    }


    public void setDeleteButtonVisibility(int visibility) {
        if (visibility == View.GONE || visibility == View.VISIBLE || visibility == View.INVISIBLE) {
            deleteButtonVisibility = visibility;
        } else {
            throw new RuntimeException("Invalid visibility passed to setDeleteButtonVisibility");
        }
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
            JSONObject sheetJson = jsonArray.get(position);

            // Unwrapping the JSON object
            Composition composition =
                    (new Gson()).fromJson(sheetJson.toString(), Composition.class);

            // Populate textViews with information
            ((TextView) parentView.findViewById(R.id.sheet_list_item_title))
                    .setText(composition.getTitle().trim());
            ((TextView) parentView.findViewById(R.id.sheet_list_item_composer_name))
                    .setText(composition.getComposer_name());
            //((TextView) parentView.findViewById(R.id.sheet_list_item_style)).setText(style);
            //((TextView) parentView.findViewById(R.id.sheet_list_item_key)).setText(key);
            //((TextView) parentView.findViewById(R.id.sheet_list_item_date)).setText(date);
            ((TextView) parentView.findViewById(R.id.sheet_list_item_download_count))
                    .setText(CompositionUtil.ParseDownloadCount(composition.getPop()));
            TextView difficultyText =
                    (TextView) parentView.findViewById(R.id.sheet_list_item_difficulty);
            difficultyText.setText(CompositionUtil.ParseDifficulty(composition.getDifficulty()));
            difficultyText.setTextColor(CompositionUtil.ParseDifficultyColor(composition.getDifficulty()));
            return parentView;
        }

        public void invokeDelete(View view) {
            // TODO Send a DELETE request to server
        }
    }

}
