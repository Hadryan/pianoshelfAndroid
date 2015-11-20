package com.pianoshelf.joey.pianoshelf.sheet;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.composition.CompositionUtil;

import org.json.JSONObject;

/**
 * Created by joey on 13/11/15.
 *
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */
public class CompositionViewHolder extends RecyclerView.ViewHolder {
    private static final String LOG_TAG = "Sheet ViewHolder";
    public View mRootView;
    private TextView mTitle;
    private TextView mComposerName;
    private TextView mDownloadCount;
    private TextView mDifficulty;
    public ImageView mDeleteButton;
    protected Composition mComposition;

    public CompositionViewHolder(View view) {
        super(view);
        init(view);
    }

    private void init(View view) {
        mRootView = view;
        mTitle = (TextView) view.findViewById(R.id.sheet_list_item_title);
        mComposerName = (TextView) view.findViewById(R.id.sheet_list_item_composer_name);
        mDownloadCount = (TextView) view.findViewById(R.id.sheet_list_item_download_count);
        mDifficulty = (TextView) view.findViewById(R.id.sheet_list_item_difficulty);
        mDeleteButton = (ImageView) view.findViewById(R.id.sheet_list_item_delete);
    }


    public void bindSheetJson(JSONObject sheetJson) {
        // Unwrapping the JSON object
        mComposition = new Gson()
                .fromJson(sheetJson.toString(), Composition.class);

        // Populate textViews with information
        mTitle.setText(mComposition.getTitle().trim());
        mComposerName.setText(mComposition.getComposer_name());
        //((TextView) parentView.findViewById(R.id.sheet_list_item_style)).setText(style);
        //((TextView) parentView.findViewById(R.id.sheet_list_item_key)).setText(key);
        //((TextView) parentView.findViewById(R.id.sheet_list_item_date)).setText(date);
        mDownloadCount.setText(CompositionUtil.ParseDownloadCount(mComposition.getPop()));
        mDifficulty.setText(CompositionUtil.ParseDifficulty(mComposition.getDifficulty()));
        mDifficulty.setTextColor(CompositionUtil.ParseDifficultyColor(mComposition.getDifficulty()));
    }
}
