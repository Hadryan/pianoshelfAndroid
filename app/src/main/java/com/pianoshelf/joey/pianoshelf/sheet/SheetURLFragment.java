package com.pianoshelf.joey.pianoshelf.sheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pianoshelf.joey.pianoshelf.R;

/**
 * This class receives a working URL and loads the URL
 * Created by joey on 10/29/14.
 */
public class SheetURLFragment extends Fragment implements RequestListener<String, GlideDrawable> {
    private String sheetUrl;
    private static final String SHEET_URL_ARGUMENT = "sheetUrl";
    private static final String LOG_TAG = "SheetURLFragment";
    private ProgressBar progressBar;
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sheetUrl = getArguments().getString(SHEET_URL_ARGUMENT);
        } else {
            //TODO load error img?
        }
    }

    // Currently the progressBar only shows upon first entering the Fragment
    // Need to implement the onResume, onPaused, etc functions to display
    // the spinning progressbar correctly 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_sheet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.sheetProgress);
        progressBar.setVisibility(View.VISIBLE);

        mImageView = (ImageView) view.findViewById(R.id.sheetImage);
        Glide.with(getContext())
                .load(sheetUrl)
                .fitCenter()
                .crossFade()
                .listener(this)
                .into(mImageView);
    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        Log.e(LOG_TAG, e.getMessage());
        progressBar.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        progressBar.setVisibility(View.GONE);
        return false;
    }

    /**
     * We do not override the onCreate method for parameters, instead we use the
     * newInstance and Bundle static factory design pattern.
     * We will pass in the url of the sheet (later) from SheetView
     * to specify which page to load
     * TODO Consider overloading this class with a Bundle parameter
     */
    public static SheetURLFragment newInstance(String sheetUrl) {
        SheetURLFragment sheet = new SheetURLFragment();
        Bundle args = new Bundle();

        args.putString(SHEET_URL_ARGUMENT, sheetUrl);
        sheet.setArguments(args);
        return sheet;
    }
}
