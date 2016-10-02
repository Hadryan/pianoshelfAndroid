package com.pianoshelf.joey.pianoshelf.sdcard;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joey on 25/09/16.
 */

public class UriUtils {
    public static Uri handleImageUri(Uri uri) {
        Pattern pattern = Pattern.compile("(content://media/.*\\d)");
        if (uri.getPath().contains("content")) {
            Matcher matcher = pattern.matcher(uri.getPath());
            if (matcher.find())
                return Uri.parse(matcher.group(1));
            else
                throw new IllegalArgumentException("Cannot handle this URI");
        } else
            return uri;
    }
    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            Uri newUri = handleImageUri(uri);
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(newUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e){
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
