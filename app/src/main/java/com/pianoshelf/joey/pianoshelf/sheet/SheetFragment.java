package com.pianoshelf.joey.pianoshelf.sheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pianoshelf.joey.pianoshelf.BaseFragment;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.FullComposition;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by joey on 24/10/14.
 * Page/Activity for viewing sheet music
 * Goal: Swipe left/right to move pages (intuitive)
 * Goal: Auto-Hiding navigation buttons after some time {Left, Right, Page Number}
 * SheeView cannot extend BaseActivity currently due to a bug in the PhotoView library
 */
public class SheetFragment extends BaseFragment {
    public static final String SHEET_ID_INTENT = "SheetView_sheetId";
    public static final SheetFrameView.SheetFrameState mState = SheetFrameView.SheetFrameState.SHEET;
    private final String LOG_TAG = "SheetFragment";
    private ViewPager mViewPager;

    private FullComposition mComposition = null;

    public static SheetFragment newInstance() {
        SheetFragment sheet = new SheetFragment();
        Bundle args = new Bundle();

        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sheetview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventBus.getDefault().post(mState);

        mViewPager = (ViewPager) view.findViewById(R.id.sheet_viewpager);
        // Race condition where api request returns before we get to setup the view
        if (mComposition != null) {
            mViewPager.setAdapter(new SheetViewPagerAdapter(getChildFragmentManager(), mComposition));
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCompositionEvent(FullComposition sheetInfo) {
        mComposition = sheetInfo;

        // Refresh ViewPager's adapter
        if (mViewPager != null) {
            mViewPager.setAdapter(new SheetViewPagerAdapter(getChildFragmentManager(), sheetInfo));
        }
    }


    private class SheetViewPagerAdapter extends FragmentPagerAdapter {
        private FullComposition mComposition;

        public SheetViewPagerAdapter(FragmentManager fragmentManager, FullComposition composition) {
            super(fragmentManager);
            mComposition = composition;
        }

        @Override
        public Fragment getItem(int position) {
            String onlineImageUrl = mComposition.getImages().get(position);
            Log.i(LOG_TAG, "Fetching online image from: " + onlineImageUrl);
            return SheetURLFragment.newInstance(onlineImageUrl);
        }

        @Override
        public int getCount() {
            return mComposition.getImages().size();
        }
    }
}
