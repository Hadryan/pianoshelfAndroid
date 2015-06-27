package com.pianoshelf.joey.pianoshelf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;

/**
 * Created by joey on 12/22/14.
 */
public class SheetOfflineFragment extends Fragment {
    private String offlineImageLocation;
    private static final String OFFLINE_IMAGE_LOCATION_ARGUMENT = "offlineImageLocation";
    private ImageView sheetImageView;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;

    // Default Constructor
    public SheetOfflineFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offlineImageLocation = getArguments().getString(OFFLINE_IMAGE_LOCATION_ARGUMENT);
        } else {
            //TODO load error img?
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = (View) inflater.inflate(R.layout.fragment_sheet, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.sheetProgress);
        progressBar.setVisibility(View.VISIBLE);

        sheetImageView = (ImageView) view.findViewById(R.id.sheetImage);

        File imageBitmap = new File(offlineImageLocation);

        if (imageBitmap.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap sheetImage = BitmapFactory.decodeFile(offlineImageLocation);
                    sheetImageView.post(new Runnable() {
                        @Override
                        public void run() {
                            sheetImageView.setImageBitmap(sheetImage);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
        } else {
            throw new RuntimeException("File not found.");
        }
        return view;
    }

    public static SheetOfflineFragment newInstance(String offlineImageLocation) {
        SheetOfflineFragment sheet = new SheetOfflineFragment();
        Bundle args = new Bundle();

        args.putString(OFFLINE_IMAGE_LOCATION_ARGUMENT, offlineImageLocation);
        sheet.setArguments(args);
        return sheet;
    }
}
