package com.pianoshelf.joey.pianoshelf;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * This class receives a working URL and loads the URL
 * Created by root on 10/29/14.
 */
public class Sheet extends Fragment {
    private String sheetUrl;
    private NetworkImageView networkImageView;
    private ImageLoader imageLoader;

    // Default Constructor
    public Sheet() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sheetUrl = getArguments().getString("sheetUrl");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.activity_sheet, container, false);

        ((TextView) view.findViewById(R.id.sheetText)).setText(sheetUrl);
        networkImageView = (NetworkImageView) view.findViewById(R.id.sheetImage);
        // getActivity here since Fragment is not a subclass of Context
        imageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
        networkImageView.setImageUrl(sheetUrl, imageLoader);

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
