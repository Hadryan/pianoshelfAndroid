package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
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
public class JSONAdapter extends BaseAdapter {
    protected Context context;
    protected List<JSONObject> jsonArray;
    protected int layout;

    public JSONAdapter(Context context, int layout, JSONArray composers) {
        this.context = context;
        this.layout = layout;
        this.jsonArray = new ArrayList<JSONObject>();
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
            convertView = ((LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(layout, parent, false);
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

    @Override
    // There may not be a trivial 1 to 1 mapping between the itemId
    // in the adapter versus the backend storage, this is the mapping function
    public long getItemId(int position) {
        try {
            JSONObject composer = jsonArray.get(position);
            return composer.getInt("id");
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
}
