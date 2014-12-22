package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class will help instantiate some composition object details
 * Created by joey on 12/21/14.
 */
public class SharedPreferenceHelper {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferenceEditor;

    public static final String PIANOSHELF = "pianoshelf";
    public static final String COMPOSITION_JSON_KEY = "COMPOSITION_JSON_KEY";
    public static final String OFFLINE_COMPOSITIONS = "offline_compositions";

    public SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PIANOSHELF, Context.MODE_PRIVATE);
        preferenceEditor = sharedPreferences.edit();
    }

    /**
     * Check preferences for the set of keys. Create the set if it does not exist.
     * The set of keys represent the availability of a offline composition
     * @return
     */
    private Set<String> getOfflineCompositionKeys() {
        HashSet<String> offlineCompositionKeysSet =
                (HashSet<String>) sharedPreferences.getStringSet(OFFLINE_COMPOSITIONS, null);
        if (offlineCompositionKeysSet == null) {
            // Add an empty set of keys
            offlineCompositionKeysSet = new HashSet<String>();
            preferenceEditor.putStringSet(OFFLINE_COMPOSITIONS, offlineCompositionKeysSet);
            preferenceEditor.apply();
        }
        return offlineCompositionKeysSet;
    }

    /**
     * Fetch the composition JSON object
     * @param compositionName
     * @return
     */
    public Composition getOfflineComposition(String compositionName, Composition defaultValue) {
        if (!getOfflineCompositionKeys().contains(compositionName)) {
            return defaultValue;
        } else {
            SharedPreferences compositionPreference = context.
                    getSharedPreferences(compositionName, context.MODE_PRIVATE);
            String compositionJsonString = compositionPreference.getString(COMPOSITION_JSON_KEY, null);
            if (compositionJsonString == null) {
                return defaultValue;
            } else {
                return (new Gson()).fromJson(compositionJsonString, Composition.class);
            }
        }
    }

    public void setOfflineCompositions(String compositionsName, Composition composition) {
        SharedPreferences.Editor compositionEditor = context.
                getSharedPreferences(compositionsName, Context.MODE_PRIVATE).edit();
        compositionEditor.putString(COMPOSITION_JSON_KEY,
                (new Gson()).toJson(composition, Composition.class));
        compositionEditor.apply();
        // Check if the composition exists in the set of keys. Create it if it does not exist.
        HashSet<String> offlineCompositionKeys = (HashSet<String>) getOfflineCompositionKeys();
        if (!getOfflineCompositionKeys().contains(compositionsName)) {
            offlineCompositionKeys.add(compositionsName);
            preferenceEditor.putStringSet(PIANOSHELF, offlineCompositionKeys);
            preferenceEditor.apply();
        }
    }

    /**
     * Fetch the offline images array/list
     * @param compositionName
     * @return
     */
    public String[] getOfflineCompositionImages(String compositionName, String[] defaultValue) {
        Composition offlineComposition = getOfflineComposition(compositionName, null);
        if (offlineComposition == null) {
            return defaultValue;
        } else {
            String[] offlineCompositionImages = offlineComposition.getOffline_Images();
            if (offlineCompositionImages == null) {
                return defaultValue;
            } else {
                return offlineCompositionImages;
            }
        }
    }

    /**
     * Precondition: compositionName must exist
     * @param compositionName
     * @param compositionImages
     */
    public void setOfflineCompositionImages(String compositionName, String[] compositionImages) {
        Composition offlineComposition = getOfflineComposition(compositionName, null);
        if (offlineComposition == null) {
            throw new RuntimeException(compositionName + " does not exist");
        } else {
            offlineComposition.setOffline_images(compositionImages);
            setOfflineCompositions(compositionName, offlineComposition);
        }
    }

}
