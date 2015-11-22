package com.pianoshelf.joey.pianoshelf.sheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pianoshelf.joey.pianoshelf.R;

import java.net.MalformedURLException;
import java.net.URL;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * This class receives a working URL and loads the URL
 * Created by joey on 10/29/14.
 */
public class SheetURLFragment extends Fragment implements URLImageView.ImageLoaded {
    private String sheetUrl;
    private static final String SHEET_URL_ARGUMENT = "sheetUrl";
    private static final String LOG_TAG = "SheetURLFragment";
    private ProgressBar progressBar;
    private URLImageView mImageView;
    private PhotoViewAttacher mAttacher;

    // Default Constructor
    public SheetURLFragment() {}

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
        View view = inflater.inflate(R.layout.fragment_sheet, container, false);


        // Try to parse the URL to see if it is malformed
        try {
            URL sheetUrlTest = new URL(sheetUrl);
        } catch (MalformedURLException ex) {
            Log.e(LOG_TAG, "URL malformed " + sheetUrl + " Message " + ex.getMessage());
            // TODO display error image
        }

        progressBar = (ProgressBar) view.findViewById(R.id.sheetProgress);
        progressBar.setVisibility(View.VISIBLE);

        mImageView = (URLImageView) view.findViewById(R.id.sheetImage);
        mAttacher = new PhotoViewAttacher(mImageView);

        mImageView.loadImageFromURL(sheetUrl, this);

        return view;
    }

    @Override
    public void onImageLoaded(ImageView image) {
        progressBar.setVisibility(View.GONE);
        mAttacher.update();
    }

    @Override
    public void onImageError(ImageView image) {
        Log.w(LOG_TAG, "Image error " + image);
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
