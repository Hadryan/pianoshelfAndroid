package com.pianoshelf.joey.pianoshelf.sheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.R;

import static com.pianoshelf.joey.pianoshelf.sheet.SheetFragment.SHEET_ID_INTENT;

/**
 * Created by Me on 3/13/2017.
 */

public class SheetFrameView extends BaseActivity {

    private SheetFragment mSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheetview);

        long sheetId = getIntent().getLongExtra(SHEET_ID_INTENT, -1);

        mSheetFragment = SheetFragment.newInstance(sheetId);

        replaceMainView(mSheetFragment);
    }

    private void replaceMainView(Fragment frag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sheet_frame, frag)
                .commitNow();
    }
}
