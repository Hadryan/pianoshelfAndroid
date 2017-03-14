package com.pianoshelf.joey.pianoshelf.sheet_media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pianoshelf.joey.pianoshelf.BaseFragment;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.sheet.SheetFrameView;

import org.greenrobot.eventbus.EventBus;

import static com.pianoshelf.joey.pianoshelf.sheet.SheetFragment.SHEET_ID_INTENT;

/**
 * Created by Me on 3/13/2017.
 * responsible for displaying detailed information about a sheet
 */

public class MediaFragment extends BaseFragment {
    public static final SheetFrameView.SheetFrameState mState = SheetFrameView.SheetFrameState.INFO;

    private long mSheetId;

    public static MediaFragment newInstance() {
        MediaFragment sheet = new MediaFragment();
        Bundle args = new Bundle();

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
        return inflater.inflate(R.layout.fragment_sheet_media, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventBus.getDefault().post(mState);
    }

}
