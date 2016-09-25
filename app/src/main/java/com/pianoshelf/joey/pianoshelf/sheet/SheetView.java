package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.composition.CompositionUtil;
import com.pianoshelf.joey.pianoshelf.rest_api.DeserializeCB;
import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.rest_api.ShelfSheetMusic;
import com.pianoshelf.joey.pianoshelf.shelf.Shelf;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by joey on 24/10/14.
 * Page/Activity for viewing sheet music
 * Goal: Swipe left/right to move pages (intuitive)
 * Goal: Auto-Hiding navigation buttons after some time {Left, Right, Page Number}
 * SheeView cannot extend BaseActivity currently due to a bug in the PhotoView library
 */
public class SheetView extends BaseActivity {
    private final String LOG_TAG = "SheetView";
    private Composition mComposition;

    // Menu button to download sheetmusic
    //private MenuItem downloadAction;

    private ViewPager mViewPager;
    private ActionBar mActionBar;


    private List<String> mOfflineImages;

    public static final String SHEET_ID_INTENT = "SheetView_sheetId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheetview);

        // fetches the url of the sheet music JSON object
        Intent intent = getIntent();

        long sheetId = intent.getLongExtra(SHEET_ID_INTENT, -1);
        Log.i(LOG_TAG, "Loading sheet ID: " + sheetId);

        mViewPager = (ViewPager) findViewById(R.id.sheetViewPager);
        mActionBar = getSupportActionBar();

        apiService.getSheet((int) sheetId)
                .enqueue(new DeserializeCB<RW<Composition, MetaData>>() {
                    @Override
                    public void onResponse(Call<RW<Composition, MetaData>> call, Response<RW<Composition, MetaData>> response) {
                        super.onResponse(call, response);
                        int statusCode = response.body().getMeta().getCode();
                        if (statusCode != 200) {
                            mActionBar.setTitle("Invalid sheet response");
                            Log.w(C.NET, "Sheet music request invalid. " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<RW<Composition, MetaData>> call, Throwable t) {
                        mActionBar.setTitle("Error while loading sheet");
                        t.printStackTrace();
                        Log.e(C.NET, "Sheet music request failed. " + t.getLocalizedMessage());
                    }
                });
    }

    @Subscribe
    public void onSheetInfoEvent(Composition sheetInfo) {
        mComposition = sheetInfo;
        mOfflineImages = (new SharedPreferenceHelper(this)).
                getOfflineCompositionImages(mComposition.getUniqueurl(), null);

        // Log.i(LOG_TAG, "SheetInfo: \n" + new Gson().toJson(mComposition));

        // Set actionbar title
        mActionBar.setTitle(mComposition.getTitle());

        // Make the download button visible
        boolean disableDownloadButton = true;
        List<String> compositionImages = mComposition.getImages();
        for (int i = 0; i < compositionImages.size() && disableDownloadButton; ++i) {
            String onlineImageUrl = compositionImages.get(i);
            String offlineImageFilename = CompositionUtil.offlineSheetFilename(onlineImageUrl);
            // Verify all offline files
            disableDownloadButton = (mOfflineImages != null)
                    && (compositionImages.size() != mOfflineImages.size())
                    && (offlineImageFilename.equals(mOfflineImages.get(i)));
        }
        // Only enable download button if the data in shared preferences are
        // incomplete
        if (!disableDownloadButton) {
            //downloadAction.setVisible(true);
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mViewPager.setAdapter(new SheetViewPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sheet_url_view, menu);

        //downloadAction = menu.findItem(R.id.sheet_download);
        //downloadAction.setVisible(false);

        // Disable add to shelf when user is not logged in.
        menu.findItem(R.id.sheet_add_to_shelf).setEnabled(isUserLoggedIn());
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Check existing files and download missing files.
     * Checks the file, if it does not exist then create and request the image.
     * Save the image when the request completes.
     *
     * @param item
     */
    /*
    public boolean invokeDownload(MenuItem item) {

        // Start async task to download all the images of this mComposition and store them
        // in external storage
        VolleySingleton requestQueue = VolleySingleton.getInstance(this);
        final SharedPreferenceHelper offlineFiles = new SharedPreferenceHelper(this);
        final String compositionName = mComposition.getUniqueurl();
        String[] cachedOfflineImages = offlineFiles.
                getOfflineCompositionImages(compositionName, null);
        final List<String> onlineImages = Arrays.asList(mComposition.getImages());

        if (cachedOfflineImages == null) {
            offlineFiles.setOfflineCompositions(compositionName, mComposition);
            mOfflineImages = new String[onlineImages.size()];
        } else {
            mOfflineImages = cachedOfflineImages;
        }
        // Iterate over all sheetUrls and download each image
        for (int i = 0; i < onlineImages.size(); ++i) {
            final int currentIndex = i;
            final String sheetUrl = onlineImages.get(i);
            final boolean writeSheetToFile;
            final File offlineSheet;
            final String sheetFileName = CompositionUtil.ParseSheetFileNameUrl(sheetUrl);
            // Store the image to external storage
            // Check mComposition file, skip this write if file exists
            state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                offlineSheet = new File(offlineSheetDirectory, sheetFileName);
                // Only write to file when the file does not exist or the file
                // exists but its not present in share preferences
                if (offlineSheet.exists()) {
                    // Check if we written the file to shared preferences.
                    // If we did, do not write the file.
                    writeSheetToFile = !sheetFileName.equals(mOfflineImages[i]);
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
                                FileOutputStream fileOutputStream = null;
                                try {
                                    fileOutputStream = new FileOutputStream(offlineSheet);
                                    // Compression format PNG ignores the 100 quality setting
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                    // Update mOfflineImages
                                    mOfflineImages[currentIndex] = sheetFileName;
                                    // Update offline content information
                                    offlineFiles.setOfflineCompositionImages(compositionName, mOfflineImages);
                                } catch (FileNotFoundException ex) {
                                    Log.e(LOG_TAG, "File not found.");
                                } finally {
                                    IOUtils.closeQuietly(fileOutputStream);
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
    */

    /**
     * Forced download of sheet images. Overwrite all files
     *
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
     *
     * @param item
     */
    public boolean invokePDFDownload(MenuItem item) {
        return true;
    }

    public void invokeAddToShelf(MenuItem item) {
        apiService.shelfAddSheet(new ShelfSheetMusic(mComposition.getId()))
                .enqueue(new DeserializeCB<RW<Shelf, MetaData>>() {
                    @Override
                    public void onResponse(Call<RW<Shelf, MetaData>> call, Response<RW<Shelf, MetaData>> response) {
                        int statusCode = response.body().getMeta().getCode();
                        if (statusCode == 200) {
                            Toast.makeText(SheetView.this,
                                    R.string.add_shelf_success,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(C.NET, "Invalid Response from shelf add request! Meta: "
                                    + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<RW<Shelf, MetaData>> call, Throwable t) {
                        Toast.makeText(SheetView.this,
                                "Failed to add sheet to shelf.",
                                Toast.LENGTH_LONG).show();
                        Log.e(C.NET, "Shelf add request failed. " + t.getLocalizedMessage());
                    }
                });
    }

    private class SheetViewPagerAdapter extends FragmentPagerAdapter {
        public SheetViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            String offlineImageLocation = CompositionUtil.offlineDirPath(mComposition);
            if (mOfflineImages != null && (position < mOfflineImages.size())
                    && (offlineImageLocation.equals(mOfflineImages.get(position)))) {
                Log.i(LOG_TAG, "Using offline image for id " + mComposition.getId() + " page " + position);
                String offlineImagePath = CompositionUtil.offlineSheetPath(mComposition, position);
                return SheetOfflineFragment.newInstance(offlineImagePath);
            } else {
                String onlineImageUrl = mComposition.getImages().get(position);
                Log.i(LOG_TAG, "Fetching online image from: " + onlineImageUrl);
                return SheetURLFragment.newInstance(onlineImageUrl);
            }
        }

        @Override
        public int getCount() {
            return mComposition.getImages().size();
        }
    }
}
