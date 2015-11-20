package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by root on 11/8/14.
 * This class deals with displaying a list of sheet music
 * This class reacts in a query format
 */
public class SheetArrayListFragment extends Fragment {
    private final String SERVER_SHEETMUSIC_SUFFIX = "api/sheetmusic/";

    private static final String JSON_ARRAY = "JSONARRAY";
    private static final String LOG_TAG = "SheetArrayListFragment";
    private int mDeleteButtonVisibility = View.GONE;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private JSONArray mSheetListJson;

    public SheetArrayListFragment() {}

    public static SheetArrayListFragment newInstance(JSONArray jsonArray) {
        SheetArrayListFragment sheetList = new SheetArrayListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    public static SheetArrayListFragment newInstance(JsonArray jsonArray) {
        SheetArrayListFragment sheetList = new SheetArrayListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray.toString());
        sheetList.setArguments(args);
        return sheetList;
    }

    public static SheetArrayListFragment newInstance(String jsonArray) {
        SheetArrayListFragment sheetList = new SheetArrayListFragment();
        Bundle args = new Bundle();

        args.putString(JSON_ARRAY, jsonArray);
        sheetList.setArguments(args);
        return sheetList;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                mSheetListJson = new JSONArray(getArguments().getString(JSON_ARRAY));
            } catch (JSONException ex) {
                Log.d(LOG_TAG, ex.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.sheet_recycler);

        mAdapter = new PreviewRecycler(R.layout.adapter_sheet_list_item_3, mSheetListJson);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    public void enableDelete() {
        if (mDeleteButtonVisibility != View.VISIBLE) {
            mDeleteButtonVisibility = View.VISIBLE;
            // reconstruct all views with delete button
            mAdapter.notifyDataSetChanged();
        }
    }

    public void disableDelete() {
        if (mDeleteButtonVisibility != View.GONE) {
            mDeleteButtonVisibility = View.GONE;
            mAdapter.notifyDataSetChanged();
        }
    }

    public class PreviewRecycler extends JsonRecycler<CompositionViewHolder> {
        protected int mLayout;
        public PreviewRecycler(int layout, JSONArray composition) {
            super(composition);
            mLayout = layout;
        }

        @Override
        public void onBindViewHolder(final CompositionViewHolder holder, final int position) {
            holder.bindSheetJson(mJsonList.get(position));
            final long itemId = getItemId(position);
            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openSheet = new Intent(getContext(), SheetView.class);
                        openSheet.putExtra("sheetMusicUrl",
                                C.SERVER_ADDR + SERVER_SHEETMUSIC_SUFFIX + itemId);
                        startActivity(openSheet);
                }
            });
            holder.mDeleteButton.setVisibility(mDeleteButtonVisibility);
            holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mJsonList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mJsonList.size());
                    // TODO remove the actual shelf from server
                }
            });
        }

        @Override
        public CompositionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(mLayout, parent, false);
            return new CompositionViewHolder(view);
        }
    }
}
