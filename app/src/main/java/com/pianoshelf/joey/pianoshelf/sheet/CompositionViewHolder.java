package com.pianoshelf.joey.pianoshelf.sheet;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.Composition;
import com.pianoshelf.joey.pianoshelf.composition.CompositionUtil;

/**
 * Created by joey on 13/11/15.
 * <p/>
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */
public class CompositionViewHolder extends RecyclerView.ViewHolder {
    private static final String LOG_TAG = "Sheet ViewHolder";
    View mRootView;
    ImageView mDeleteButton;
    Composition mComposition;
    TextView mTitle;
    TextView mComposerName;
    TextView mViewCount;
    TextView mDifficulty;

    public CompositionViewHolder(View view) {
        super(view);

        mRootView = view;
        mTitle = (TextView) view.findViewById(R.id.sheet_list_item_title);
        mComposerName = (TextView) view.findViewById(R.id.sheet_list_item_composer_name);
        mViewCount = (TextView) view.findViewById(R.id.sheet_list_item_download_count);
        mDifficulty = (TextView) view.findViewById(R.id.sheet_list_item_difficulty);
        mDeleteButton = (ImageView) view.findViewById(R.id.sheet_list_item_delete);
    }

    public void bindSheetJson(Composition sheet) {
        // Unwrapping the JSON object
        mComposition = sheet;

        // Populate textViews with information
        mTitle.setText(mComposition.getTitle().trim());
        mComposerName.setText(mComposition.getComposer_name());
        //((TextView) parentView.findViewById(R.id.sheet_list_item_style)).setText(style);
        //((TextView) parentView.findViewById(R.id.sheet_list_item_key)).setText(key);
        //((TextView) parentView.findViewById(R.id.sheet_list_item_date)).setText(date);
        mViewCount.setText(CompositionUtil.parseViews(mComposition.getView_count()));
        mDifficulty.setText(CompositionUtil.ParseDifficulty(mComposition.getDifficulty()));
        mDifficulty.setTextColor(CompositionUtil.ParseDifficultyColor(mComposition.getDifficulty()));
    }
}
