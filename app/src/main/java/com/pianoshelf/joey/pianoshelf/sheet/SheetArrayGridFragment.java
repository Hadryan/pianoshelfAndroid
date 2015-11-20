package com.pianoshelf.joey.pianoshelf.sheet;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.JsonArray;
import com.pianoshelf.joey.pianoshelf.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joey on 17/10/15.
 */
public class SheetArrayGridFragment extends Fragment {
    private static final String JSON_ARRAY = "json";
    private static final String LOG_TAG = "Sheet Grid Fragment";
    private GridView mGridView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private JSONArray mSheetListJson;

    public static SheetArrayGridFragment newInstance(JSONArray jsonArray) {
        SheetArrayGridFragment sheetList = new SheetArrayGridFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    public static SheetArrayGridFragment newInstance(JsonArray jsonArray) {
        SheetArrayGridFragment sheetList = new SheetArrayGridFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    public static SheetArrayGridFragment newInstance(String jsonArray) {
        SheetArrayGridFragment sheetList = new SheetArrayGridFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray);
        sheetList.setArguments(args);
        return sheetList;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            try {
                String jsonArray = args.getString(JSON_ARRAY);
                mSheetListJson = new JSONArray(jsonArray);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, ex.toString());
            }
        } else {
            Log.w(LOG_TAG, "no argument bundle");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.sheet_recycler);

        mAdapter = new PreviewRecycler(R.layout.adapter_sheet_array_with_image, mSheetListJson);
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
        public PreviewRecycler(int layout, JSONArray composition) {
            super(composition);
            mLayout = layout;
        }

        @Override
        public void onBindViewHolder(PreviewHolder holder, int position) {
            holder.bindSheetJson(mJsonList.get(position));
        }

        @Override
        public PreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(mLayout, parent, false);
            return new PreviewHolder(view);
        }
    }

    public class PreviewHolder extends CompositionViewHolder {
        private SheetURLImageView mPreviewImage;

        public PreviewHolder(View view) {
            super(view);
            mPreviewImage = (SheetURLImageView) view.findViewById(R.id.sheet_music_preview_image);
        }

        @Override
        public void bindSheetJson(JSONObject sheetJson) {
            super.bindSheetJson(sheetJson);
            mPreviewImage.loadImageFromURL("https:" + mComposition.getThumbnail_url(), null);
        }
    }
}
