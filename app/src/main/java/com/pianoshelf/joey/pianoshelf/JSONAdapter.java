package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for JSONObjects
 * Child classes should only need to override getView for a fully functional adapter
 * Created by root on 11/13/14.
 */
public abstract class JSONAdapter extends BaseAdapter {
    protected final Context context;
    protected final List<JSONObject> jsonArray;
    protected final int layout;
    protected LayoutInflater mLayoutInflater;

    public JSONAdapter(Context context, int layout, JSONArray composers) {
        this.context = context;
        this.layout = layout;
        this.jsonArray = new ArrayList<>();
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i=0;i<composers.length();++i) {
            try {
                this.jsonArray.add(composers.getJSONObject(i));
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ConvertView is the view being recycled as one item in the listView goes out of screen
        // Only instantiate the convertView when its null
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layout, parent, false);
        }
        return convertView;
    }

    @Override
    public int getCount(){
        return jsonArray.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return jsonArray.get(position);
    }

    // There may not be a trivial 1 to 1 mapping between the itemId
    // in the adapter versus the backend storage, this is the mapping function
    @Override
    public long getItemId(int position) {
        try {
            JSONObject jsonObject = jsonArray.get(position);
            return jsonObject.getInt("id");
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Parse a difficulty integer to a difficulty string
    public String parseDifficulty(int difficulty) {
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

    public int getDifficultyColor(int difficulty) {
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

    public String parseDownloadCount(int downloadCount) {
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
