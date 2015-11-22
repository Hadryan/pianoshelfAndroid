package com.pianoshelf.joey.pianoshelf;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by joey on 21/11/15.
 */
public class BaseFragment extends Fragment {
    protected Context mContext;
    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(mContext);
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }
}
