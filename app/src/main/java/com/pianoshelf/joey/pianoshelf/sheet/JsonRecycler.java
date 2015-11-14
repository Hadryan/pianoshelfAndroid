package com.pianoshelf.joey.pianoshelf.sheet;

import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 13/11/15.
 */
public abstract class JsonRecycler<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private static final String LOG_TAG = "Json Recycler";
    protected List<JSONObject> mJsonList;

    public JsonRecycler(JSONArray composers) {
        mJsonList = new ArrayList<>();
        for(int i=0;i<composers.length();++i) {
            try {
                mJsonList.add(composers.getJSONObject(i));
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mJsonList.size();
    }

    @Override
    public long getItemId(int position) {
        try {
            JSONObject jsonObject = mJsonList.get(position);
            return jsonObject.getInt("id");
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

}
