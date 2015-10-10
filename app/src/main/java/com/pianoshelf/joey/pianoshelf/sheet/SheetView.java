package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;
import com.pianoshelf.joey.pianoshelf.VolleySingleton;
import com.pianoshelf.joey.pianoshelf.rest_api.AddSheetToShelfRequest;

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
 * SheeView cannot extend BaseActivity currently due to a bug in the PhotoView library
 */
public class SheetView extends BaseActivity {
    private final String LOG_TAG = "SheetView";
    private Composition composition;

    // Menu button to download sheetmusic
    //private MenuItem downloadAction;

    private String[] offlineImages;

    private final String offlineRootDirectory =
            Environment.getExternalStorageDirectory() + File.separator + C.PIANOSHELF;

    protected SpiceManager mSpiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

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
                (Request.Method.GET, sheetMusicUrl, (String) null, new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        // Parse response into Java object with Gson
                        composition =
                                (new Gson()).fromJson(response.toString(), Composition.class);

                        offlineImages = (new SharedPreferenceHelper(context)).
                                getOfflineCompositionImages(composition.getUniqueurl(), null);

                        // Set actionbar title
                        getSupportActionBar().setTitle(composition.getTitle());

                        // Make the download button visible
                        boolean disableDownloadButton = true;
                        for (int i=0; i<composition.getImages().length && disableDownloadButton;++i) {
                            String onlineImageUrl = composition.getImages()[i];
                            String offlineImageLocation = parseSheetFileNameUrl(onlineImageUrl);
                            disableDownloadButton = (offlineImages != null) && (composition.getImages().length != offlineImages.length) && (offlineImageLocation.equals(offlineImages[i]));
                        }
                        // Only enable download button if the data in shared preferences are
                        // incomplete
                        if (!disableDownloadButton) {
                            //downloadAction.setVisible(true);
                        }

                        // Instantiate a ViewPager and a PagerAdapter.
                        ViewPager viewPager = (ViewPager)
                                findViewById(R.id.sheetViewPager);
                        // TODO BeginTransaction ?
                        viewPager.setAdapter(new SheetViewPagerAdapter(getSupportFragmentManager()));
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
        //downloadAction = menu.findItem(R.id.sheet_download);
        //downloadAction.setVisible(false);
        // Disable add to shelf when user is not logged in.
        if (!getSharedPreferences(C.PIANOSHELF, MODE_PRIVATE).
                contains(C.AUTHORIZATION_TOKEN)) {
            (menu.findItem(R.id.sheet_add_to_shelf)).setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSpiceManager.start(this);
    }

    @Override
    protected void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
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
            final String sheetUrl = parseOnlineSheetUrl(onlineImages[i]);
            final boolean writeSheetToFile;
            final File offlineSheet;
            final String sheetFileName = parseSheetFileNameUrl(sheetUrl);
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
                ImageRequest downloadRequest = new ImageRequest(
                        sheetUrl,
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
                        }, 0, 0, ImageView.ScaleType.CENTER, null,
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
                getSharedPreferences(C.PIANOSHELF, MODE_PRIVATE);
        //TODO Add a check to see if the sheet is already present in the user's shelf
        if (globalPreferences.contains(C.AUTHORIZATION_TOKEN)) {
            performRequest(composition.getId(),
                    globalPreferences.getString(C.AUTHORIZATION_TOKEN, null));
        }
        return true;
    }

    private void performRequest(int id, String authToken) {
        AddSheetToShelfRequest request = new AddSheetToShelfRequest(id, authToken);
        mSpiceManager.execute(request, null, DurationInMillis.ONE_MINUTE,
                new AddSheetToShelfRequestListener());
    }

    private class AddSheetToShelfRequestListener implements RequestListener<Void> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(SheetView.this, "Error during request: " +
                    spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(Void aVoid) {
            Toast.makeText(SheetView.this, R.string.add_shelf_success, Toast.LENGTH_LONG).show();
        }
    }

    private String parseSheetFileNameUrl(String sheetUrl) {
        return sheetUrl.substring(sheetUrl.lastIndexOf('/') + 1);
    }

    private String parseOnlineSheetUrl(String sheetUrl) {
        return "https:" + sheetUrl;
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
            String onlineImageUrl = parseOnlineSheetUrl(composition.getImages()[position]);
            String offlineImageLocation = parseSheetFileNameUrl(onlineImageUrl);
            if (offlineImages != null && (position < offlineImages.length) &&
                    (offlineImageLocation.equals(offlineImages[position]))) {
                Log.i(LOG_TAG, "Using offline image.");
                return SheetOfflineFragment.newInstance(offlineRootDirectory + File.separator +
                        composition.getUniqueurl() + File.separator + offlineImageLocation);
            } else {
                Log.i(LOG_TAG, "Fetching online image. " + onlineImageUrl);
                return SheetURLFragment.newInstance(onlineImageUrl);
            }
        }

        @Override
        public int getCount() {
            return composition.getImages().length;
        }
    }
}
