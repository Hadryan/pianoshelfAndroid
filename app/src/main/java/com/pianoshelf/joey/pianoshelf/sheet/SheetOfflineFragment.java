package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.utility.BitmapUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by joey on 12/22/14.
 */
public class SheetOfflineFragment extends Fragment {
    private static final String LOG_TAG = "Offline Sheet Music";
    private static final String OFFLINE_IMAGE_LOCATION_ARGUMENT = "offlineImageLocation";
    private String offlineImageLocation;
    private ImageView sheetImageView;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            offlineImageLocation = args.getString(OFFLINE_IMAGE_LOCATION_ARGUMENT);
        } else {
            //TODO load error img?
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sheet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.sheetProgress);
        progressBar.setVisibility(View.VISIBLE);

        sheetImageView = (ImageView) view.findViewById(R.id.sheetImage);

        new LoadImageFromDisk(getContext(), offlineImageLocation).execute();
    }

    public static SheetOfflineFragment newInstance(String offlineImageLocation) {
        SheetOfflineFragment sheet = new SheetOfflineFragment();
        Bundle args = new Bundle();

        args.putString(OFFLINE_IMAGE_LOCATION_ARGUMENT, offlineImageLocation);
        sheet.setArguments(args);
        return sheet;
    }

    private class LoadImageFromDisk extends AsyncTask<File, Void, Bitmap> {
        private File mFile;
        private Context mContext;
        private int mImageWidth;
        private int mImageHeight;

        public LoadImageFromDisk(Context context, String filePath) {
            mContext = context;
            mFile = new File(filePath);
            mImageWidth = sheetImageView.getWidth();
            mImageHeight = sheetImageView.getHeight();
        }

        @Override
        protected Bitmap doInBackground(File... params) {
            try {
                Bitmap sheetImage = BitmapUtil.LoadBitmap(mContext, mFile, mImageWidth, mImageHeight);
                return sheetImage;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Load image from file failed " + mFile.getAbsolutePath());
            } finally {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                sheetImageView.setImageBitmap(bitmap);
            } else {
                Log.w(LOG_TAG, "Loaded empty image " + mFile.getAbsolutePath());
            }
            progressBar.setVisibility(View.GONE);
        }
    }
}
