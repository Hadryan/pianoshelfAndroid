package com.pianoshelf.joey.pianoshelf.sheet;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.util.List;

/**
 * Created by joey on 17/10/15.
 */
public class SheetArrayGridFragment extends SheetArrayFragment {
    private static final String LOG_TAG = "Sheet Grid Fragment";

    public static SheetArrayGridFragment newInstance() {
        SheetArrayGridFragment sheetList = new SheetArrayGridFragment();
        return sheetList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PreviewRecycler(R.layout.adapter_sheet_array_with_image, mSheetList);
        mRecyclerView.setAdapter(mAdapter);

        int columns = calculateOptimalColumnNumber();

        mLayoutManager = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    // Calculate optimal number of columns
    private int calculateOptimalColumnNumber() {
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        BitmapDrawable previewExample = (BitmapDrawable) getResources().getDrawable(R.drawable.example_preview_image);
        if (previewExample != null) {
            int previewWidth = previewExample.getBitmap().getWidth();
            Log.i(LOG_TAG, "Device width: " + widthPixels + " Preview example width: " + previewWidth);
            // Tolerate resizing the preview down a bit
            return Math.max(1, widthPixels / ((int) (0.9 * previewWidth)));
        } else {
            return 2;
        }
    }

    public class PreviewRecycler extends JsonRecycler<PreviewHolder> {
        protected int mLayout;

        public PreviewRecycler(int layout, List<Composition> composition) {
            super(composition);
            mLayout = layout;
        }

        @Override
        public void onBindViewHolder(PreviewHolder holder, int position) {
            holder.bindSheetJson(mSheetList.get(position));
        }

        @Override
        public PreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(mLayout, parent, false);
            return new PreviewHolder(view);
        }
    }

    public class PreviewHolder extends CompositionViewHolder {
        private URLImageView mPreviewImage;

        public PreviewHolder(View view) {
            super(view);
            mPreviewImage = (URLImageView) view.findViewById(R.id.sheet_music_preview_image);
        }

        @Override
        public void bindSheetJson(Composition sheet) {
            super.bindSheetJson(sheet);
            mPreviewImage.loadImageFromURL("https:" + mComposition.getThumbnail_url(), null);
        }
    }
}
