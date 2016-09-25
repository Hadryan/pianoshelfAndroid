package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.pianoshelf.joey.pianoshelf.composition.Composition;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class will help instantiate some composition object details
 * Created by joey on 12/21/14.
 */
public class SharedPreferenceHelper {
    private Context context;
    private SharedPreferences mSP;

    private static final String PIANOSHELF = "pianoshelf";
    private static final String COMPOSITION_JSON_KEY = "COMPOSITION_JSON_KEY";
    private static final String OFFLINE_COMPOSITIONS = "offline_compositions";

    public SharedPreferenceHelper(Context context) {
        this.context = context;
        mSP = context.getSharedPreferences(PIANOSHELF, Context.MODE_PRIVATE);
        EventBus.getDefault().register(this);
    }

    /** Key Value Pairs **/
    public boolean userLoggedIn() {
        return null != getUser();
    }

    public String getAuthToken() {
        return mSP.getString(C.AUTHORIZATION_TOKEN, null);
    }

    public SharedPreferenceHelper setAuthToken(String token) {
        mSP.edit()
                .putString(C.AUTHORIZATION_TOKEN, token)
                .apply();
        return this;
    }

    public SharedPreferenceHelper removeAuthToken() {
        mSP.edit()
                .remove(C.AUTHORIZATION_TOKEN)
                .apply();
        return this;
    }

    public SharedPreferenceHelper setUser(String user) {
        mSP.edit()
                .putString(C.USERNAME, user)
                .apply();
        return this;
    }

    public String getUser() {
        return mSP.getString(C.USERNAME, null);
    }

    public SharedPreferenceHelper removeUser() {
        mSP.edit()
                .remove(C.USERNAME)
                .apply();
        return this;
    }

    /** Compositions **/

    /**
     * Check preferences for the set of keys. Create the set if it does not exist.
     * The set of keys represent the availability of a offline composition
     *
     * @return
     */
    private Set<String> getOfflineCompositionKeys() {
        HashSet<String> offlineCompositionKeysSet =
                (HashSet<String>) mSP.getStringSet(OFFLINE_COMPOSITIONS, null);
        if (offlineCompositionKeysSet == null) {
            // Add an empty set of keys
            offlineCompositionKeysSet = new HashSet<>();
            mSP.edit()
                    .putStringSet(OFFLINE_COMPOSITIONS, offlineCompositionKeysSet)
                    .apply();
        }
        return offlineCompositionKeysSet;
    }

    /**
     * Fetch the composition JSON object
     *
     * @param compositionName
     * @return
     */
    public Composition getOfflineComposition(String compositionName, Composition defaultValue) {
        if (!getOfflineCompositionKeys().contains(compositionName)) {
            return defaultValue;
        } else {
            SharedPreferences compositionPreference = context.
                    getSharedPreferences(compositionName, Context.MODE_PRIVATE);
            String compositionJsonString = compositionPreference.getString(COMPOSITION_JSON_KEY, null);
            if (compositionJsonString == null) {
                return defaultValue;
            } else {
                return new Gson().fromJson(compositionJsonString, Composition.class);
            }
        }
    }

    public void setOfflineCompositions(String compositionsName, Composition composition) {
        SharedPreferences.Editor compositionEditor = context.
                getSharedPreferences(compositionsName, Context.MODE_PRIVATE).edit();
        compositionEditor.putString(COMPOSITION_JSON_KEY,
                new Gson().toJson(composition, Composition.class));
        compositionEditor.apply();
        // Check if the composition exists in the set of keys. Create it if it does not exist.
        HashSet<String> offlineCompositionKeys = (HashSet<String>) getOfflineCompositionKeys();
        if (!getOfflineCompositionKeys().contains(compositionsName)) {
            offlineCompositionKeys.add(compositionsName);
            mSP.edit()
                    .putStringSet(PIANOSHELF, offlineCompositionKeys)
                    .apply();
        }
    }

    /**
     * Fetch the offline images array/list
     *
     * @param compositionName
     * @return
     */
    public List<String> getOfflineCompositionImages(String compositionName, List<String> defaultValue) {
        Composition offlineComposition = getOfflineComposition(compositionName, null);
        if (offlineComposition == null) {
            return defaultValue;
        } else {
            List<String> offlineCompositionImages = offlineComposition.getOffline_Images();
            if (offlineCompositionImages == null) {
                return defaultValue;
            } else {
                return offlineCompositionImages;
            }
        }
    }

    /**
     * Precondition: compositionName must exist
     *
     * @param compositionName
     * @param compositionImages
     */
    public void setOfflineCompositionImages(String compositionName, List<String> compositionImages) {
        Composition offlineComposition = getOfflineComposition(compositionName, null);
        if (offlineComposition == null) {
            throw new RuntimeException(compositionName + " does not exist");
        } else {
            offlineComposition.setOffline_images(compositionImages);
            setOfflineCompositions(compositionName, offlineComposition);
        }
    }




    /*** EventBus Actions ***/
    public static class RemoveUserAndToken {}

    @Subscribe
    public void onUserRemoveRequest(RemoveUserAndToken request) {
        removeAuthToken();
        removeUser();
    }
}
