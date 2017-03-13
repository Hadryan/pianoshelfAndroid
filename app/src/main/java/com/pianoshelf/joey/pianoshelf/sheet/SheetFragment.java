package com.pianoshelf.joey.pianoshelf.sheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pianoshelf.joey.pianoshelf.BaseFragment;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.SharedPreferenceHelper;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.composition.CompositionUtil;
import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.rest_api.RWCallback;
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
public class SheetFragment extends BaseFragment {
    public static final String SHEET_ID_INTENT = "SheetView_sheetId";
    private final String LOG_TAG = "SheetFragment";

    // Menu button to download sheetmusic
    //private MenuItem downloadAction;
    private Composition mComposition;
    private long mSheetId;
    private ViewPager mViewPager;
    private List<String> mOfflineImages;

    public static SheetFragment newInstance(long sheetId) {
        SheetFragment sheet = new SheetFragment();
        Bundle args = new Bundle();

        args.putLong(SHEET_ID_INTENT, sheetId);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mSheetId = args.getLong(SHEET_ID_INTENT, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sheetview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager = (ViewPager) view.findViewById(R.id.sheet_viewpager);

        Log.e("test", "pinging api for information");
        getApiService().getSheet((int) mSheetId)
                .enqueue(new RWCallback<RW<Composition, MetaData>>() {
                    @Override
                    public void onResponse(Call<RW<Composition, MetaData>> call, Response<RW<Composition, MetaData>> response) {
                        super.onResponse(call, response);
                        int statusCode = response.body().getMeta().getCode();
                        if (statusCode != 200) {
                            setTitle("Invalid sheet response");
                            Log.w(C.NET, "Sheet music request invalid. " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<RW<Composition, MetaData>> call, Throwable t) {
                        setTitle("Error while loading sheet");
                        t.printStackTrace();
                        Log.e(C.NET, "Sheet music request failed. " + t.getLocalizedMessage());
                    }
                });
    }

    @Subscribe
    public void onSheetInfoEvent(Composition sheetInfo) {
        mComposition = sheetInfo;
        mOfflineImages = new SharedPreferenceHelper(getContext())
                .getOfflineCompositionImages(mComposition.getUniqueurl(), null);

        // Set actionbar title
        setTitle(mComposition.getTitle());

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
        mViewPager.setAdapter(new SheetViewPagerAdapter(getChildFragmentManager()));
    }

    public void invokeAddToShelf(MenuItem item) {
        getApiService().shelfAddSheet(new ShelfSheetMusic(mComposition.getId()))
                .enqueue(new RWCallback<RW<Shelf, MetaData>>() {
                    @Override
                    public void onResponse(Call<RW<Shelf, MetaData>> call, Response<RW<Shelf, MetaData>> response) {
                        int statusCode = response.body().getMeta().getCode();
                        if (statusCode == 200) {
                            Toast.makeText(getActivity(),
                                    R.string.add_shelf_success,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(C.NET, "Invalid Response from shelf add request! Meta: "
                                    + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<RW<Shelf, MetaData>> call, Throwable t) {
                        Toast.makeText(getActivity(),
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
