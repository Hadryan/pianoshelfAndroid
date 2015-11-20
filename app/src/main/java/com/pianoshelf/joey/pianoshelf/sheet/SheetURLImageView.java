package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.pianoshelf.joey.pianoshelf.VolleySingleton;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by joey on 21/10/15.
 */
public class SheetURLImageView extends ImageView {
    private static final String LOG_TAG = "ImageView URL";
    private Context mContext;
    private Bitmap mBitmap = null;
    private String mSheetUrl;
    public SheetURLImageView(Context context) {
        super(context);
        init(context);
    }
    public SheetURLImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public SheetURLImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public void loadImageFromURL(final String Url, final ImageLoaded callback) {
        // Try to parse the URL to see if it is malformed
        try {
            java.net.URL sheetUrlTest = new URL(Url);
            Log.v(LOG_TAG, "Sheet URL valid " + sheetUrlTest);
        } catch (MalformedURLException ex) {
            Log.e(LOG_TAG, "URL malformed " + Url + " Message " + ex.getMessage());
            // TODO display error image
        }
        final ImageView sheetMusicImage = this;
        if (mBitmap == null) {
            mSheetUrl = Url;
            ImageLoader imageLoader = VolleySingleton.getInstance(mContext).getImageLoader();
            imageLoader.get(Url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        Log.i(LOG_TAG, "Image loaded " + Url);
                        mBitmap = response.getBitmap();
                        sheetMusicImage.setImageBitmap(mBitmap);
                        if (callback != null) {
                            callback.onImageLoaded(sheetMusicImage);
                        }
                    } else {
                        //TODO display error image or message
                        //sheetMusicImage.setImageResource();
                        Log.d(LOG_TAG, "Bitmap image null " + Url);
                        // Do not invoke the callback here as sometimes responses come in multi-parts
                        // and onResponse gets triggered without an image
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Load an error image or display an error dialog
                    Log.e(LOG_TAG, "Image request error " + error.getMessage() + " url " + Url);
                    if (callback != null) {
                        callback.onImageError(sheetMusicImage);
                    }
                }
                // Set the height and width of the image here for resizing
            }, getWidth(), getHeight());
        } else {
            this.setImageBitmap(mBitmap);
            Log.d(LOG_TAG, "Used existing image bitmap " + mBitmap);
            Log.d(LOG_TAG, "internal Url " + mSheetUrl + " Given Url " + Url);
            if (callback != null) {
                callback.onImageLoaded(sheetMusicImage);
            }
        }
    }

    public interface ImageLoaded {
        void onImageLoaded(ImageView image);
        void onImageError(ImageView image);
    }
}
