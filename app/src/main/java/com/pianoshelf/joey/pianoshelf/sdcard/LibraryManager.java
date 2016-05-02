package com.pianoshelf.joey.pianoshelf.sdcard;

import android.util.Log;

import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.io.File;

/**
 * Created by joey on 29/04/16.
 */
public class LibraryManager {
    public static String getRootPath() {
        File library = new File(C.OFFLINE_ROOT_DIRECTORY);
        if (!library.isDirectory()) {
            library.delete();
        }
        if (!library.exists()) {
            library.mkdirs();
            return library.getAbsolutePath();
        } else {
            Log.e(C.FILE_IO, "Failed to create library root path " + C.OFFLINE_ROOT_DIRECTORY);
            return null;
        }
    }

    public static String getSheetDirPath(Composition sheetInfo) {
        String offlinePath = C.OFFLINE_ROOT_DIRECTORY + File.separator +
                sheetInfo.getUniqueurl() + File.separator + sheetInfo.getTitle() + File.separator;
        File offlineFile = new File(offlinePath);

        if (!offlineFile.isDirectory()) {
            offlineFile.delete();
        }

        if (!offlineFile.exists()) {
            offlineFile.mkdirs();
            return offlineFile.getAbsolutePath();
        } else {
            Log.e(C.FILE_IO, "Failed to create sheet music root path " + offlinePath);
            return null;
        }
    }

}
