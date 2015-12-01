package com.pianoshelf.joey.pianoshelf.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * Created by joeyjoeyze on 18/11/15.
 */
public class BitmapUtil {
    private static final String TAG = "BG util";
    public static final String BACKGROUND_IMAGE_FILE_NAME = "background.png";
    public static final String BACKGROUND_IMAGE_KEY = "background_key";
    public static final String BACKGROUND_PREF = "background_pref";

    public static final String BACKGROUND_UPDATE = "background_update";

    // Load background into imageview
    public static boolean LoadFullscreenBackgroundPref(Context context, ImageView imageView) throws IOException {
        Bitmap background = LoadFullscreenBackgroundBitmapPref(context);
        if (background != null) {
            imageView.setImageBitmap(background);
            return true;
        } else {
            return false;
        }
    }

    public static Bitmap LoadFullscreenBackgroundBitmapPref(Context context) throws IOException {
        SharedPreferences backgroundPref = context.getSharedPreferences(BACKGROUND_PREF, Context.MODE_PRIVATE);
        String backgroundImagePath = backgroundPref.getString(BACKGROUND_IMAGE_KEY, null);
        if (backgroundImagePath != null) {
            return LoadFullscreenBitmap(context, backgroundImagePath);
        } else {
            return null;
        }
    }

    public static Bitmap LoadFullscreenBitmap(Context context, String path) throws IOException {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return LoadBitmap(context, path, size.x, size.y);
    }

    public static Bitmap LoadFullscreenBitmap(Context context, Uri uri) throws IOException {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return LoadBitmap(context, uri, size.x, size.y);
    }

    public static Bitmap LoadBitmap(Context context, final File file, int dstWidth, int dstHeight) throws IOException {
        try {
            Bitmap bitmap = LoadBitmap(context, new Callable<InputStream>() {
                @Override
                public InputStream call() throws Exception {
                    return new FileInputStream(file);
                }
            }, dstWidth, dstHeight);
            Log.v(TAG, "Bitmap Loaded " + file.getAbsolutePath());
            return bitmap;
        } catch (IOException e) {
            Log.v(TAG, "Bitmap Load Failed " + file.getAbsolutePath());
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Callable Failed");
        } finally {
            return null;
        }
    }

    public static Bitmap LoadBitmap(Context context, final String filePath, int dstWidth, int dstHeight) throws IOException {
        try {
            Bitmap bitmap = LoadBitmap(context, new Callable<InputStream>() {
                @Override
                public InputStream call() throws Exception {
                    return new FileInputStream(filePath);
                }
            }, dstWidth, dstHeight);
            Log.v(TAG, "Bitmap Loaded " + filePath);
            return bitmap;
        } catch (IOException e) {
            Log.v(TAG, "Bitmap Load Failed " + filePath);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Callable Failed");
        } finally {
            return null;
        }
    }

    public static Bitmap LoadBitmap(Context context, final Uri uri, int dstWidth, int dstHeight) throws IOException {
        try {
            final ContentResolver resolver = context.getContentResolver();
            Bitmap bitmap = LoadBitmap(context, new Callable<InputStream>() {
                @Override
                public InputStream call() throws Exception {
                    return resolver.openInputStream(uri);
                }
            }, dstWidth, dstHeight);
            Log.v(TAG, "Bitmap Loaded " + uri);
            return bitmap;
        } catch (IOException e) {
            Log.v(TAG, "Bitmap Load Failed " + uri);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Callable Failed");
        } finally {
            return null;
        }
    }

    public static Bitmap LoadBitmap(Context context, Callable<InputStream> openStream, int dstWidth, int dstHeight) throws Exception {
        // http://stackoverflow.com/questions/3331527/android-resize-a-large-bitmap-file-to-scaled-output-file
        // User blubl comment
        int inWidth = 0;
        int inHeight = 0;

        InputStream istream = openStream.call();

        // decode image size (decode metadata only, not the whole image)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(istream, null, options);
        istream.close();
        istream = null;

        inWidth = options.outWidth;
        inHeight = options.outHeight;

        // decode full image pre-resized
        istream = openStream.call();
        options = new BitmapFactory.Options();
        // calc rought re-size (this is no exact resize)
        options.inSampleSize = Math.max(inWidth / dstWidth, inHeight / dstHeight);
        // decode full image
        Bitmap roughBitmap = BitmapFactory.decodeStream(istream, null, options);

        // calc exact destination size
        Matrix m = new Matrix();
        RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
        RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
        m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
        float[] values = new float[9];
        m.getValues(values);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);

        return resizedBitmap;
    }

    // Save background bitmap to a file
    public static boolean SaveBackground(Context context, Bitmap background) {
        File filesDir = context.getFilesDir();
        String backgroundImagePath = filesDir.getAbsolutePath() + File.separator + BACKGROUND_IMAGE_FILE_NAME;
        try {
            // Save image
            FileOutputStream ostream = new FileOutputStream(backgroundImagePath);
            background.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            Log.d(TAG, "Image saved to " + backgroundImagePath);

            // Save path to shared preferences for later access
            SharedPreferences.Editor editor = context.getSharedPreferences(BACKGROUND_PREF, Context.MODE_PRIVATE).edit();
            editor.putString(BACKGROUND_IMAGE_KEY, backgroundImagePath);

            Log.d(TAG, "Image saved to shared pref key " + BACKGROUND_IMAGE_KEY + " value " + backgroundImagePath);

            // Notify others to update the background
            editor.putBoolean(BitmapUtil.BACKGROUND_UPDATE, true);
            editor.apply();

            return true;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Background image save failed. " + e.getMessage());
            return false;
        }
    }
}

