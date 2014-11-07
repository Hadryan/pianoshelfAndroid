package com.pianoshelf.joey.pianoshelf;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * This class receives a working URL and loads the URL
 * Created by root on 10/29/14.
 */
public class Sheet extends Fragment {
    private String sheetUrl;
    private ImageView imageView;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;

    // Default Constructor
    public Sheet() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sheetUrl = getArguments().getString("sheetUrl");
        } else {
            //TODO load error img?
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = (View) inflater.inflate(R.layout.activity_sheet, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.sheetProgress);

        imageView = (ImageView) view.findViewById(R.id.sheetImage);

        // getActivity used here since Fragment is not a subclass of Context
        imageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
        imageLoader.get(sheetUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    // Dismiss the progress animation
                    progressBar.setVisibility(View.GONE);
                    imageView.setImageBitmap(response.getBitmap());
                } else {
                    //TODO display error image or message
                    progressBar.setVisibility(View.GONE);
                    //imageView.setImageResource();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                // TODO Load an error image or display an error dialog
            }
        // Set the height and width of the image here for resizing
        }, imageView.getWidth(), imageView.getHeight());

        return view;
    }

    /**
     * We do not override the onCreate method for parameters, instead we use the
     * newInstance and Bundle static factory design pattern.
     * We will pass in the url of the sheet (later) from SheetView
     * to specify which page to load
     * TODO Consider overloading this class with a Bundle parameter
     */
    public static Sheet newInstance(String sheetUrl) {
        Sheet sheet = new Sheet();
        Bundle args = new Bundle();

        args.putString("sheetUrl", sheetUrl);
        sheet.setArguments(args);
        return sheet;
    }
}
