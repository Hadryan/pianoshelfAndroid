package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.composition.SimpleComposition;
import com.pianoshelf.joey.pianoshelf.recycler.ListRecycler;
import com.pianoshelf.joey.pianoshelf.recycler.RecyclerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

/**
 * Created by joey on 17/10/15.
 */
public class SheetArrayGridFragment extends RecyclerFragment {
    private static final String LOG_TAG = "Sheet Grid Fragment";

    public static SheetArrayGridFragment newInstance() {
        SheetArrayGridFragment sheetList = new SheetArrayGridFragment();
        return sheetList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PreviewRecycler(R.layout.adapter_sheet_array_with_image);
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
            // Tolerate upscaling the preview a bit
            return Math.max(1, widthPixels / ((int) (1.5 * previewWidth)));
        } else {
            return 2;
        }
    }

    public class PreviewRecycler extends ListRecycler<PreviewHolder, Composition> {
        protected int mLayout;

        public PreviewRecycler(int layout) {
            super();
            mLayout = layout;
            EventBus.getDefault().register(this);
        }

        @Subscribe
        void onQueryFinished(SimpleComposition[] sheetList) {
            mList.addAll(Arrays.asList(sheetList));

            notifyDataSetChanged();
            setMoreItemsRequested(false);
        }


        @Override
        public void onBindViewHolder(PreviewHolder holder, int position) {
            holder.bindSheetJson(mList.get(position));
            final long sheetId = mList.get(position).getId();
            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openSheet = new Intent(getContext(), SheetFrameView.class);
                    openSheet.putExtra(SheetFragment.SHEET_ID_INTENT, sheetId);
                    startActivity(openSheet);
                }
            });
        }

        @Override
        public PreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(mLayout, parent, false);
            return new PreviewHolder(view);
        }
    }

    public class PreviewHolder extends CompositionViewHolder {
        private ImageView mPreviewImage;

        public PreviewHolder(View view) {
            super(view);
            mPreviewImage = (ImageView) view.findViewById(R.id.sheet_music_preview_image);
            mPreviewImage.post(new Runnable() {
                @Override
                public void run() {
                    // Visual fix for UI elements expanding
                    // without this code the imageview will expand when Glide finishes loading
                    // this is rather annoying when scrolling up and down the list
                    // A4 paper size 210mm x 297mm
                    // Letter size 8.5inch by 11inch
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                            mPreviewImage.getLayoutParams();
                    Log.e("YAZE", "size " + params.height + " " + params.width);
                    params.height = mPreviewImage.getWidth() * 22 / 17;
                    mPreviewImage.setLayoutParams(params);
                }
            });
        }

        @Override
        public void bindSheetJson(Composition sheet) {
            super.bindSheetJson(sheet);
            Glide.with(getContext())
                    .load(mComposition.getThumbnail_url())
                    .into(mPreviewImage);
        }
    }
}
