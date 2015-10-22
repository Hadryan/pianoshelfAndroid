package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.pianoshelf.joey.pianoshelf.JSONAdapter;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;

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
        if (getArguments() != null) {
            try {
                mSheetListJson = new JSONArray(getArguments().getString(JSON_ARRAY));
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
        return inflater.inflate(R.layout.fragment_sheet_grid_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mGridView = (GridView) view.findViewById(R.id.sheet_grid);
        mGridView.setAdapter(new SheetGridAdapter(getActivity(),
                R.layout.adapter_sheet_array_with_image,
                mSheetListJson));
    }

    private class SheetGridAdapter extends JSONAdapter {
        public SheetGridAdapter(Context context, int layout, JSONArray sheetList) {
            super(context, layout, sheetList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View parentView = super.getView(position, convertView, parent);
            // Unwrap the JSONObject
            JSONObject sheetJson = jsonArray.get(position);

            // Unwrapping the JSON object
            Composition composition =
                    (new Gson()).fromJson(sheetJson.toString(), Composition.class);

            final LinearLayout rootView = (LinearLayout) parentView.findViewById(R.id.sheet_music_card_layout);

            // Populate textViews with information
                    ((TextView) parentView.findViewById(R.id.sheet_list_item_title))
                    .setText(composition.getTitle().trim());
            ((TextView) parentView.findViewById(R.id.sheet_list_item_composer_name))
                    .setText(composition.getComposer_name());
            //((TextView) parentView.findViewById(R.id.sheet_list_item_style)).setText(style);
            //((TextView) parentView.findViewById(R.id.sheet_list_item_key)).setText(key);
            //((TextView) parentView.findViewById(R.id.sheet_list_item_date)).setText(date);
            ((TextView) parentView.findViewById(R.id.sheet_list_item_download_count))
                    .setText(parseDownloadCount(composition.getPop()));
            TextView difficultyText =
                    (TextView) parentView.findViewById(R.id.sheet_list_item_difficulty);
            difficultyText.setText(parseDifficulty(composition.getDifficulty()));
            difficultyText.setTextColor(getDifficultyColor(composition.getDifficulty()));

            SheetURLImageView previewImage = (SheetURLImageView) parentView.findViewById(R.id.sheet_music_preview_image);
            previewImage.loadImageFromURL("https:" + composition.getThumbnail_url(),
                    new SheetURLImageView.ImageLoaded() {
                        @Override
                        public void onImageLoaded(ImageView image) {
                            ViewGroup.LayoutParams params = rootView.getLayoutParams();
                            params.width = image.getWidth();
                            rootView.setLayoutParams(params);
                        }

                        @Override
                        public void onImageError(ImageView image) {

                        }
                    });
            return parentView;
        }
    }

}
