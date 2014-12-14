package com.pianoshelf.joey.pianoshelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * This class receives a working URL and loads the URL
 * Created by root on 10/29/14.
 */
public class SheetURLFragment extends Fragment {
    private String sheetUrl;
    private ImageView imageView;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;

    // Default Constructor
    public SheetURLFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sheetUrl = getArguments().getString("sheetUrl");
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
        View view = (View) inflater.inflate(R.layout.fragment_sheet, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.sheetProgress);
        progressBar.setVisibility(View.VISIBLE);

        imageView = (ImageView) view.findViewById(R.id.sheetImage);
        // getActivity used here since Fragment is not a subclass of Context
        imageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
        imageLoader.get(sheetUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    // Dismiss the progress animation
                    imageView.setImageBitmap(response.getBitmap());
                    progressBar.setVisibility(View.GONE);
                } else {
                    //TODO display error image or message
                    //imageView.setImageResource();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Load an error image or display an error dialog
            }
            // Set the height and width of the image here for resizing
        }, imageView.getWidth(), imageView.getHeight());
        return view;
    }

    /**
     * We do not override the onCreate method for parameters, instead we use the
     * newInstance and Bundle static factory design pattern.
     * We will pass in the url of the sheet (later) from SheetURLView
     * to specify which page to load
     * TODO Consider overloading this class with a Bundle parameter
     */
    public static SheetURLFragment newInstance(String sheetUrl) {
        SheetURLFragment sheet = new SheetURLFragment();
        Bundle args = new Bundle();

        args.putString("sheetUrl", sheetUrl);
        sheet.setArguments(args);
        return sheet;
    }
}
