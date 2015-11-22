package com.pianoshelf.joey.pianoshelf.utility;

import android.util.Log;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 21/11/15.
 */
public class JsonUtil {
    private static final String LOG_TAG = "JSON util";

    public static List<JSONObject> toList(JSONArray jsonArray) {
        try {
            List<JSONObject> jsonList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                jsonList.add(jsonArray.getJSONObject(i));
            }
            return jsonList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }

    public static List<JSONObject> toList(JsonArray jsonArray) {
        try {
            List<JSONObject> jsonList = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); ++i) {
                jsonList.add(new JSONObject(jsonArray.get(i).toString()));
            }
            return jsonList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }
}
