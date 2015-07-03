package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 11/8/14.
 * This class deals with displaying a list of sheet music
 * This class reacts in a query format
 */
public class SheetListFragment extends android.support.v4.app.ListFragment {
    private final String SERVER_SHEETMUSIC_SUFFIX = "api/sheetmusic/";

    private static final String JSON_ARRAY = "JSONARRAY";
    private static final String LOG_TAG = "SheetListFragment";
    private JSONArray jsonArray;

    public SheetListFragment() {}

    public static SheetListFragment newInstance(JSONArray jsonArray) {
        SheetListFragment sheetList = new SheetListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    public static SheetListFragment newInstance(JsonArray jsonArray) {
        SheetListFragment sheetList = new SheetListFragment();
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
            } catch (JSONException ex) {
                Log.d(LOG_TAG, ex.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sheet_listview, container, false);
        setListAdapter(new SheetListAdapter(getActivity(), R.layout.adapter_sheet_list_item_2,
                jsonArray));
        return view;
    }

    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        Intent openSheet = new Intent(getActivity(), SheetView.class);
        openSheet.putExtra("sheetMusicUrl",
                Constants.SERVER_ADDR + SERVER_SHEETMUSIC_SUFFIX + Integer.toString((int) id));
        startActivity(openSheet);
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
                    .setText(parseDownloadCount(composition.getPop()));
            TextView difficultyText =
                    (TextView) parentView.findViewById(R.id.sheet_list_item_difficulty);
            difficultyText.setText(parseDifficulty(composition.getDifficulty()));
            difficultyText.setTextColor(getDifficultyColor(composition.getDifficulty()));
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
            return Color.GRAY;
            /*
            switch (difficulty) {
                case 0: return Color.GRAY;
                case 1: return Color.GREEN;
                case 2: return Color.CYAN;
                case 3: return Color.YELLOW;
                case 4: return Color.MAGENTA;
                case 5: return Color.RED;
                default: throw new RuntimeException("Invalid difficulty");
            }*/
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
