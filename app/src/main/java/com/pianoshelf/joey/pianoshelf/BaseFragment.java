package com.pianoshelf.joey.pianoshelf;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.pianoshelf.joey.pianoshelf.rest_api.RetroShelf;

import org.greenrobot.eventbus.EventBus;

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

    public RetroShelf getApiService() {
        Activity act = getActivity();
        if (act instanceof BaseActivity) {
            return ((BaseActivity) act).apiService;
        }
        return null;
    }

    public void setTitle(String title) {
        Activity act = getActivity();
        if (act instanceof BaseActivity) {
            ((BaseActivity) act).setTitle(title);
        }
    }
}
