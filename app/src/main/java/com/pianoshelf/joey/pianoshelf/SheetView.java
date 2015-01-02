package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by joey on 24/10/14.
 * Page/Activity for viewing sheet music
 * Goal: Swipe left/right to move pages (intuitive)
 * Goal: Auto-Hiding navigation buttons after some time {Left, Right, Page Number}
 */
public class SheetView extends BaseActivity {
    private String LOG_TAG = "SheetView";
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private Composition composition;

    private MenuItem downloadAction;

    private String[] offlineImages;

    private String offlineRootDirectory =
            Environment.getExternalStorageDirectory() + File.separator + Constants.PIANOSHELF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheetview);

        // fetches the url of the sheet music JSON object
        Intent intent = getIntent();
        String sheetMusicUrl = intent.getStringExtra("sheetMusicUrl");

        final Context context = this;
        // Fetch the JSON object from the URL
        // Create the request object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, sheetMusicUrl, null, new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        // Parse response into Java object with Gson
                        composition =
                                (new Gson()).fromJson(response.toString(), Composition.class);

                        offlineImages = (new SharedPreferenceHelper(context)).
                                getOfflineCompositionImages(composition.getUniqueurl(), null);

                        // Make the download button visible
                        boolean disableDownloadButton = true;
                        for (int i=0; i<composition.getImages().length && disableDownloadButton;++i) {
                            String onlineImageUrl = composition.getImages()[i];
                            String offlineImageLocation = parseSheetUrl(onlineImageUrl);
                            disableDownloadButton = disableDownloadButton &&
                                    (offlineImages != null) &&
                                    (composition.getImages().length != offlineImages.length) &&
                                    (offlineImageLocation.equals(offlineImages[i]));
                        }
                        // Only enable download button if the data in shared preferences are
                        // incomplete
                        if (!disableDownloadButton) {
                            downloadAction.setVisible(true);
                        }

                        // Instantiate a ViewPager and a PagerAdapter.
                        viewPager = (ViewPager) findViewById(R.id.sheetViewPager);
                        pagerAdapter = new SheetViewPagerAdapter(getSupportFragmentManager());
                        // TODO BeginTransaction ?
                        viewPager.setAdapter(pagerAdapter);
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //TODO popup error message
                    }
                });
        // Make the actual request
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sheet_url_view, menu);
        downloadAction = menu.findItem(R.id.sheet_download);
        downloadAction.setVisible(false);
        // Disable add to shelf when user is not logged in.
        if (!getSharedPreferences(Constants.PIANOSHELF, MODE_PRIVATE).
                contains(AUTHORIZATION_TOKEN)) {
            (menu.findItem(R.id.sheet_add_to_shelf)).setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Check existing files and download missing files.
     * Checks the file, if it does not exist then create and request the image.
     * Save the image when the request completes.
     * @param item
     */
    public boolean invokeDownload(MenuItem item) {
        final String compositionFolderName = composition.getUniqueurl();
        final File offlineSheetDirectory;

        // Check composition folder, create it if it does not exist.
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            offlineSheetDirectory = new File(offlineRootDirectory, compositionFolderName);
            if (!offlineSheetDirectory.mkdirs()) {
                Log.e(LOG_TAG, "Directory not created or already present.");
            }
        } else {
            Log.d(LOG_TAG, "External filesystem not mounted.");
            return false;
        }

        // Start async task to download all the images of this composition and store them
        // in external storage
        VolleySingleton requestQueue = VolleySingleton.getInstance(this);
        final SharedPreferenceHelper offlineFiles = new SharedPreferenceHelper(this);
        final String compositionName = composition.getUniqueurl();
        final String[] offlineImages;
        String[] cachedOfflineImages = offlineFiles.
                getOfflineCompositionImages(compositionName, null);
        final String[] onlineImages = composition.getImages();

        if (cachedOfflineImages == null) {
            offlineFiles.setOfflineCompositions(compositionName, composition);
            offlineImages = new String[onlineImages.length];
        } else {
            offlineImages = cachedOfflineImages;
        }
        // Iterate over all sheetUrls and download each image
        for (int i=0; i<onlineImages.length; ++i) {
            final int currentIndex = i;
            final String sheetUrl = onlineImages[i];
            final boolean writeSheetToFile;
            final File offlineSheet;
            final String sheetFileName = parseSheetUrl(sheetUrl);
            // Store the image to external storage
            // Check composition file, skip this write if file exists
            state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                offlineSheet = new File(offlineSheetDirectory, sheetFileName);
                // Only write to file when the file does not exist or the file
                // exists but its not present in share preferences
                if (offlineSheet.exists()) {
                    // Check if we written the file to shared preferences.
                    // If we did, do not write the file.
                    writeSheetToFile = !sheetFileName.equals(offlineImages[i]);
                } else {
                    writeSheetToFile = true;
                }
            } else {
                Log.d(LOG_TAG, "External filesystem not mounted.");
                return false;
            }
            // TODO check for other external storage conditions accordingly
            // http://developer.android.com/guide/topics/data/data-storage.html#filesExternal=
            Log.i(LOG_TAG, sheetFileName);
            Log.i(LOG_TAG, String.valueOf(writeSheetToFile));
            // Update offlineCompositions
            if (writeSheetToFile) {
                ImageRequest downloadRequest = new ImageRequest(sheetUrl,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                // Write the image file
                                FileOutputStream fileOutputStream;
                                try {
                                    fileOutputStream = new FileOutputStream(offlineSheet);
                                    // Compression format PNG ignores the 100 quality setting
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                            fileOutputStream);
                                    fileOutputStream.close();
                                    // Update offlineImages
                                    offlineImages[currentIndex] = sheetFileName;
                                    // Update offline content information
                                    offlineFiles.setOfflineCompositionImages(compositionName,
                                            offlineImages);
                                    setOfflineImages(offlineImages);
                                } catch (FileNotFoundException ex) {
                                    Log.e(LOG_TAG, "File not found.");
                                } catch (IOException ex) {
                                    Log.e(LOG_TAG, "File output stream not closed.");
                                }
                            }
                        }, 0, 0, null,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.e(LOG_TAG, "Image request failed: " + sheetUrl);
                                // TODO Bad request, notify user via toast message
                            }
                        });
                requestQueue.addToRequestQueue(downloadRequest);
            }
        }
        return true;
    }

    /**
     * Forced download of sheet images. Overwrite all files
     * @param item
     */
    public boolean invokeForcedDownload(MenuItem item) {
        return true;
    }

    /**
     * Download the pdf sheet music file.
     * Consider invoking a global download intent as we do not have the capabilities of
     * rendering PDF at the moment. Or we can save to a publicly visible folder and
     * notify the user.
     * @param item
     */
    public boolean invokePDFDownload(MenuItem item) {
        return true;
    }

    public boolean invokeAddToShelf(MenuItem item) {
        SharedPreferences globalPreferences =
                getSharedPreferences(PIANOSHELF, MODE_PRIVATE);
        if (globalPreferences.contains(AUTHORIZATION_TOKEN))
        (new AddSheetToShelfTask()).execute(
                globalPreferences.getString(AUTHORIZATION_TOKEN, null),
                String.valueOf(composition.getId()));
        return true;
    }

    private String parseSheetUrl(String sheetUrl) {
        return sheetUrl.substring(sheetUrl.lastIndexOf('/') + 1);
    }

    private void setOfflineImages(String[] offlineImages) {
        this.offlineImages = offlineImages;
    }

    private class SheetViewPagerAdapter extends FragmentPagerAdapter {
        public SheetViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            String onlineImageUrl = composition.getImages()[position];
            String offlineImageLocation = parseSheetUrl(onlineImageUrl);
            if (offlineImages != null && (position < offlineImages.length) &&
                    (offlineImageLocation.equals(offlineImages[position]))) {
                Log.i(LOG_TAG, "Using offline image.");
                return SheetOfflineFragment.newInstance(offlineRootDirectory + File.separator +
                        composition.getUniqueurl() + File.separator + offlineImageLocation);
            } else {
                Log.i(LOG_TAG, "Fetching online image.");
                return SheetURLFragment.newInstance(onlineImageUrl);
            }
        }

        @Override
        public int getCount() {
            return composition.getImages().length;
        }
    }
}
