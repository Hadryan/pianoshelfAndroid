package com.pianoshelf.joey.pianoshelf;

import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Me on 3/13/2017.
 */

public class BaseFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // DO NOT REMOVE
    @Subscribe
    public void bootstrapEventBus(BaseFragment f) {
    }


}
